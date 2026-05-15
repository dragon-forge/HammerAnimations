package dev.zeith.lzvm.vm;

import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.api.JzRuntimeProvider;
import dev.zeith.lzvm.exception.*;
import dev.zeith.lzvm.jvm.*;
import dev.zeith.lzvm.op.*;
import dev.zeith.lzvm.program.*;
import dev.zeith.lzvm.vm.jvm.JvmJClassLoader;
import lombok.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class LzVM
		implements JzRuntimeProvider<LzVM>
{
	protected final JClassLoader loader;
	protected final Map<String, Map<LzCallInsn, Optional<JMethod>>> jvmCache = new ConcurrentHashMap<>();
	
	public boolean logInterpretSteps = false;
	
	@Getter
	@Setter
	private LzJCallShutter jCallShutter = LzJCallShutter.ALLOW_EVERYTHING;
	
	@Getter
	@Setter
	public boolean useSineLookupTable = true;
	
	public LzVM(JClassLoader loader)
	{
		this.loader = loader;
	}
	
	public LzVM()
	{
		this(new JvmJClassLoader(Thread.currentThread().getContextClassLoader()));
	}
	
	@Override
	public LzFactory expression(LzProgramBody body)
	{
		LzProgramInfo info = body.computeInfo();
		LzFactory[] fac = new LzFactory[1];
		fac[0] = store -> new LzExpression()
		{
			final LzProgramStack stack = info.mallocStack(0);
			
			@Override
			public double get()
			{
				return interpret(store, body, stack);
			}
			
			@Override
			public double get(double... args)
			{
				return interpret(store, body, stack);
			}
			
			@Override
			public LzExpression instantiate(LzVariableStore store)
			{
				return fac[0].instantiate(store);
			}
		};
		return fac[0];
	}
	
	protected JMethod findMethod(String owner, LzCallInsn call)
	{
		Map<LzCallInsn, Optional<JMethod>> map = jvmCache.computeIfAbsent(owner, k -> new ConcurrentHashMap<>());
		return map.computeIfAbsent(call, c ->
				Optional.ofNullable(loader.loadClass(owner.replace('/', '.')))
				        .map(cls -> cls.getDeclaredMethod(call))
		).orElse(null);
	}
	
	public double interpret(LzVariableStore vars, LzProgram program, double... args)
			throws LzVMException
	{
		LzProgramStack stack = program.info.mallocStack(args.length);
		Object[] a = new Object[args.length];
		for(int i = 0; i < args.length; i++) a[i] = args[i];
		stack.fillArgs(a);
		return interpret(vars, program.body, stack);
	}
	
	public double interpret(LzVariableStore vars, LzProgram program, LzProgramStack stack)
			throws LzVMException
	{
		return interpret(vars, program.body, stack);
	}
	
	protected void log(String str)
	{
		System.out.println(str);
	}
	
	public double interpret(LzVariableStore vars, LzProgramBody program, LzProgramStack pStack)
			throws LzVMException
	{
		int[] insn = program.insnList;
		LzCallInsn[] callTable = program.callTable;
		double[] consts = program.dConstTable;
		String[] sConsts = program.sConstTable;
		
		Object[] stack = pStack.stack;
		Object[] locals = pStack.locals;
		
		Map<String, LzVarOp> varCache = new HashMap<>();
		Function<String, LzVarOp> fetchVar = name ->
		{
			if(name.startsWith("temp.")) return vars.tempVar(name);
			return vars.findVar(name);
		};
		Function<String, LzVarOp> getVar = n -> varCache.computeIfAbsent(n, fetchVar);
		
		List<Integer> labelCords = new ArrayList<>();
		program.visitOps(true, (index, opcode, args) ->
				{
					if(opcode == LzOpcodes.LABEL)
						labelCords.add(index);
				}
		);
		
		int ptr = -1;
		
		Set<String> initializedVars = new HashSet<>();
		
		final boolean useSineLookupTable = this.useSineLookupTable;
		final boolean logInterpretSteps = this.logInterpretSteps;
		
		String vinsn = null;
		LzCallInsn cinsn = null;
		int i = 0, op = -1, expect = 0;
		try
		{
			for(; i < insn.length; i++)
			{
				op = insn[i];
				
				if(logInterpretSteps)
				{
					String[] args = new String[LzOpcodes.EXTRA_SHIFTS[op]];
					for(int j = 0; j < args.length; j++)
						args[j] = LzProgramBody.constToString(program.getConstant(op, j, i));
					log("Exec " + LzOpcodes.opNameIndexed(i, op) + " " + Arrays.toString(args));
				}
				
				switch(op)
				{
					case LzOpcodes.RETURN:
						return ptr < 0 ? 0.0 : coerce(stack[ptr]);
					case LzOpcodes.CONST:
					{
						double val = consts[insn[++i]];
						try
						{
							stack[++ptr] = val;
						} catch(ArrayIndexOutOfBoundsException e)
						{
							throw new LzVMStackOverflowException(LzOpcodes.opNameIndexed(i, op) + ": attempted to store stack @ " + ptr);
						}
						break;
					}
					case LzOpcodes.SCONST:
					{
						String val = sConsts[insn[++i]];
						stack[++ptr] = val;
						break;
					}
					case LzOpcodes.LOAD:
					{
						Object val = locals[insn[++i]];
						stack[++ptr] = val;
						break;
					}
					case LzOpcodes.STORE:
					{
						Object val = stack[ptr--];
						locals[insn[++i]] = val;
						break;
					}
					
					case LzOpcodes.JCALL:
					{
						String owner = vinsn = sConsts[insn[++i]];
						cinsn = callTable[insn[++i]];
						
						if(jCallShutter != LzJCallShutter.ALLOW_EVERYTHING && !jCallShutter.permits(owner.replace('/', '.'), cinsn))
							throw new LzVMCallNotFoundException(LzOpcodes.opNameIndexed(i, op) + ": jcall to " + owner + " was not allowed by the interpreters jcall shutter.");
						
						JMethod method = findMethod(owner, cinsn);
						Object[] capturedArgs = new Object[cinsn.argCount];
						expect = capturedArgs.length;
						for(int j = capturedArgs.length - 1; j >= 0; --j) capturedArgs[j] = stack[ptr--];
						stack[++ptr] = method.invoke(capturedArgs);
					}
					break;
					
					case LzOpcodes.CALL:
					{
						int callIdx = insn[++i];
						cinsn = callTable[callIdx];
						LzCallOp call = vars.findCallByName(cinsn);
						Object[] capturedArgs = new Object[cinsn.argCount];
						expect = capturedArgs.length;
						for(int j = capturedArgs.length - 1; j >= 0; --j) capturedArgs[j] = stack[ptr--];
						stack[++ptr] = call.call(capturedArgs);
					}
					break;
					
					case LzOpcodes.ADD:
					case LzOpcodes.SUB:
					case LzOpcodes.MUL:
					case LzOpcodes.DIV:
					case LzOpcodes.MOD:
					case LzOpcodes.EQUALS:
					case LzOpcodes.NOT_EQUALS:
					case LzOpcodes.GREATER_THAN:
					case LzOpcodes.GREATER_EQ_THAN:
					case LzOpcodes.LESS_THAN:
					case LzOpcodes.LESS_EQ_THAN:
					case LzOpcodes.COALESCE:
					case LzOpcodes.AND:
					case LzOpcodes.OR:
					case LzOpcodes.MIN:
					case LzOpcodes.MAX:
					{
						expect = 2;
						double right = coerce(stack[ptr--]);
						double left = coerce(stack[ptr--]);
						stack[++ptr] = LzBinaryOp.byOpcode(op).operate(left, right);
					}
					break;
					case LzOpcodes.NOT:
					{
						double onStack = coerce(stack[ptr]);
						stack[ptr] = LzMath.notd(onStack);
					}
					break;
					case LzOpcodes.FSIN:
					{
						double onStack = coerce(stack[ptr]);
						stack[ptr] = useSineLookupTable ? LzMath.sind(onStack) : Math.sin(onStack);
					}
					break;
					case LzOpcodes.FCOS:
					{
						double onStack = coerce(stack[ptr]);
						stack[ptr] = useSineLookupTable ? LzMath.cosd(onStack) : Math.cos(onStack);
					}
					break;
					
					case LzOpcodes.READ:
					{
						int varIdx = insn[++i];
						vinsn = sConsts[varIdx];
						LzVarOp var = getVar.apply(vinsn);
						if(!initializedVars.contains(vinsn)) var.reset();
						stack[++ptr] = var.get();
					}
					break;
					
					case LzOpcodes.WRITE:
					{
						expect = 1;
						int varIdx = insn[++i];
						vinsn = sConsts[varIdx];
						getVar.apply(vinsn).set(coerce(stack[ptr--]));
						initializedVars.add(vinsn);
					}
					break;
					
					case LzOpcodes.LABEL:
						break;
					
					case LzOpcodes.JUMP:
					{
						int lblIdx = insn[++i];
						i = labelCords.get(lblIdx);
					}
					break;
					
					case LzOpcodes.JUMP_IF_FALSE:
					{
						int lblIdx = insn[++i];
						Object o = stack[ptr--];
						if(LzMath.isZero(coerce(o)))
							i = labelCords.get(lblIdx);
					}
					break;
					
					case LzOpcodes.JUMP_IF_TRUE:
					{
						int lblIdx = insn[++i];
						Object o = stack[ptr--];
						if(LzMath.isNotZero(coerce(o)))
							i = labelCords.get(lblIdx);
					}
					break;
					
					case LzOpcodes.TO_STRING:
					{
						Object obj = stack[ptr--];
						stack[++ptr] = String.valueOf(obj);
					}
					break;
					
					case LzOpcodes.POP:
					{
						Object obj = stack[ptr--];
					}
					break;
					
					case LzOpcodes.READ_INDEXED:
					{
						double index = coerce(stack[ptr--]);
						int varIdx = insn[++i];
						vinsn = sConsts[varIdx];
						LzVarOp var = getVar.apply(vinsn);
						if(!initializedVars.contains(vinsn)) var.reset();
						stack[++ptr] = var.get(index);
					}
					break;
					
					case LzOpcodes.WRITE_INDEXED:
					{
						double index = coerce(stack[ptr--]);
						expect = 1;
						int varIdx = insn[++i];
						vinsn = sConsts[varIdx];
						getVar.apply(vinsn).set(index, coerce(stack[ptr--]));
						initializedVars.add(vinsn);
					}
					break;
					
					default:
						throw new LzVMOperationNotSupportedException("Unknown opcode: " + LzOpcodes.opNameIndexed(i, op));
				}
			}
		} catch(ArrayIndexOutOfBoundsException e)
		{
			if(ptr < 0)
				throw new LzVMStackUnderflowException(LzOpcodes.opNameIndexed(i, op) + " expected " + expect + " arguments on the stack.", e);
			throw new LzVMException(LzOpcodes.opNameIndexed(i, op) + " failed to execute.", e);
		} catch(NullPointerException e)
		{
			if(cinsn != null && op == LzOpcodes.CALL)
				throw new LzVMCallNotFoundException("Could not find call " + cinsn.name + cinsn.descriptor + " with " + cinsn.argCount + " arguments.", e);
			if(cinsn != null && op == LzOpcodes.JCALL)
				throw new LzVMCallNotFoundException("Could not find jvm call " + vinsn.replace('/', '.') + "." + cinsn.name + cinsn.descriptor + " with " + cinsn.argCount + " arguments.", e);
			if(vinsn != null && (op == LzOpcodes.READ || op == LzOpcodes.WRITE))
				throw new LzVMCallNotFoundException("Could not find var " + vinsn, e);
			throw e;
		}
		
		return 0.0;
	}
	
	public static double coerce(Object obj)
	{
		return obj instanceof Number ? ((Number) obj).doubleValue() : (obj != null ? 1.0 : 0.0);
	}
}