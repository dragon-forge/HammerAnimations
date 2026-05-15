package dev.zeith.lzvm.jvm;

import dev.zeith.lzvm.api.JzRuntimeProvider;
import dev.zeith.lzvm.exception.*;
import dev.zeith.lzvm.op.LzOpcodes;
import dev.zeith.lzvm.program.*;
import lombok.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

import static org.objectweb.asm.Opcodes.*;

public class LzJvmCompiler
		implements JzRuntimeProvider<LzJvmCompiler>
{
	public static final String LzExpression = dev.zeith.lzvm.jvm.LzExpression.class.getName().replace('.', '/');
	public static final String LzVariableStore = dev.zeith.lzvm.LzVariableStore.class.getName().replace('.', '/');
	public static final String LzVarOp = dev.zeith.lzvm.op.LzVarOp.class.getName().replace('.', '/');
	public static final String LzCallOp = dev.zeith.lzvm.op.LzCallOp.class.getName().replace('.', '/');
	public static final String LzGenerated = Generated.class.getName().replace('.', '/');
	public static final String LzFMath = LzMath.class.getName().replace('.', '/');
	public static final String Math = Math.class.getName().replace('.', '/');
	
	public static final String L_LzExpression = "L" + LzExpression + ";";
	public static final String L_LzVariableStore = "L" + LzVariableStore + ";";
	public static final String L_LzVarOp = "L" + LzVarOp + ";";
	public static final String L_LzCallOp = "L" + LzCallOp + ";";
	public static final String L_LzGenerated = "L" + LzGenerated + ";";
	
	@Getter
	@Setter
	protected LzJCallShutter jCallShutter = LzJCallShutter.ALLOW_EVERYTHING;
	
	@Getter
	@Setter
	protected boolean generatedAnnotation = false;
	
	@Getter
	@Setter
	protected boolean useSineLookupTable = true;
	
	@Getter
	@Setter
	protected IClassDefiner classDefiner;
	
	@Override
	public LzFactory expression(LzProgramBody body)
	{
		if(classDefiner == null) classDefiner = new LzJVM.LzClassLoader();
		return LzJVM.compile(this, body, 0, classDefiner);
	}
	
	public byte[] compile(String name, LzProgramBody body, int argCount)
	{
		final ClassNode cn = new ClassNode();
		cn.version = V1_8;
		cn.access = ACC_PUBLIC;
		cn.name = name.replace('.', '/');
		cn.superName = "java/lang/Object";
		cn.interfaces = Collections.singletonList(LzExpression);
		
		final Set<String> usedFieldNames = new HashSet<>();
		final Set<String> usedMethodNames = new HashSet<>();
		
		usedFieldNames.add("isFactory");
		
		usedMethodNames.add("toString");
		usedMethodNames.add("wait");
		usedMethodNames.add("notify");
		usedMethodNames.add("notifyAll");
		usedMethodNames.add("hashCode");
		usedMethodNames.add("getClass");
		usedMethodNames.add("equals");
		usedMethodNames.add("clone");
		usedMethodNames.add("finalize");
		
		usedMethodNames.add("instantiate");
		usedMethodNames.add("get");
		
		final int startingFieldCount = usedFieldNames.size();
		
		final FieldNode isFactory = new FieldNode(ACC_PRIVATE | ACC_FINAL, "isFactory", "Z", null, null);
		cn.fields.add(isFactory);
		
		//<editor-fold desc="expression ctor">
		final MethodNode ctor = new MethodNode(ACC_PUBLIC, "<init>", "(" + L_LzVariableStore + ")V", null, null);
		InsnList cinsn = ctor.instructions;
		{
			cinsn.add(new VarInsnNode(ALOAD, 0));
			cinsn.add(mCall(INVOKESPECIAL, cn.superName, "<init>", "()V"));
			cinsn.add(new VarInsnNode(ALOAD, 0));
			cinsn.add(new InsnNode(ICONST_0));
			cinsn.add(new FieldInsnNode(PUTFIELD, cn.name, isFactory.name, isFactory.desc));
			cinsn.add(new InsnNode(RETURN));
		}
		cn.methods.add(ctor);
		//</editor-fold>
		
		//<editor-fold desc="factory ctor">
		final MethodNode fctor = new MethodNode(ACC_PUBLIC, "<init>", "()V", null, null);
		cinsn = fctor.instructions;
		{
			cinsn.add(new VarInsnNode(ALOAD, 0));
			cinsn.add(mCall(INVOKESPECIAL, cn.superName, "<init>", "()V"));
			cinsn.add(new VarInsnNode(ALOAD, 0));
			cinsn.add(new InsnNode(ICONST_1));
			cinsn.add(new FieldInsnNode(PUTFIELD, cn.name, isFactory.name, isFactory.desc));
			cinsn.add(new InsnNode(RETURN));
		}
		cn.methods.add(fctor);
		//</editor-fold>
		
		final Function<String, FieldNode> varGetter = getVarFieldHelper(cn, usedFieldNames, ctor);
		final Function<LzCallInsn, FieldNode> callGetter = getCallFieldHelper(cn, usedFieldNames, ctor);
		final Map<LzCallInsn, MethodNode> registeredCalls = new HashMap<>(body.callTable.length); // preallocate all calls
		final String disassembly = body.disassemble(true);
		cn.visibleAnnotations = generated(generatedAnnotation ? disassembly : "<HIDDEN>", Collections.emptyMap(), !generatedAnnotation);
		
		//<editor-fold desc="toString()">
		final MethodNode toString = new MethodNode(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
		cinsn = toString.instructions;
		{
			LabelNode notFactory = new LabelNode();
			LabelNode end = new LabelNode();
			String exprStr = dev.zeith.lzvm.jvm.LzExpression.class.getSimpleName() + "{" + body.disassemble(false) + "}";
			String factoryStr = dev.zeith.lzvm.jvm.LzFactory.class.getSimpleName() + "{" + body.disassemble(false) + "}";
			cinsn.add(new VarInsnNode(ALOAD, 0));
			cinsn.add(new FieldInsnNode(GETFIELD, cn.name, isFactory.name, isFactory.desc));
			cinsn.add(new JumpInsnNode(IFEQ, notFactory));
			cinsn.add(new LdcInsnNode(factoryStr));
			cinsn.add(new JumpInsnNode(GOTO, end));
			cinsn.add(notFactory);
			cinsn.add(new LdcInsnNode(exprStr));
			cinsn.add(end);
			cinsn.add(new InsnNode(ARETURN));
		}
		cn.methods.add(toString);
		//</editor-fold>
		
		//<editor-fold desc="get([D)D">
		MethodNode m = new MethodNode(ACC_PUBLIC, "get", "([D)D", null, null);
		cn.methods.add(m);
		
		InsnList insn = m.instructions;
		
		// create locals array
//		if(useArray)
//		{
//			pushInt(insn, program.info.maxLocals); // some temp space
//			insn.add(new IntInsnNode(NEWARRAY, T_DOUBLE));
//			insn.add(new VarInsnNode(ASTORE, 2)); // locals at slot 2
//		}
		
		int[] code = body.insnList;
		
		Map<Integer, LabelNode> labelMap = new HashMap<>();
		Set<Integer> usedLocals = new HashSet<>();
		
		final AtomicInteger labelIndex = new AtomicInteger();
		body.visitOps(false, (index, opcode, args) ->
				{
					if(opcode == LzOpcodes.LABEL)
						labelMap.put(labelIndex.getAndIncrement(), new LabelNode());
					else if(opcode == LzOpcodes.LOAD || opcode == LzOpcodes.STORE)
						usedLocals.add((int) args[0]);
				}
		);
		labelIndex.set(0);
		
		IntSupplier newLocal = () ->
		{
			int local = argCount + 1 + usedLocals.size() * 2;
			while(!usedLocals.add(local))
				local++;
			return local;
		};
		
		Set<String> varsToReset = new HashSet<>();
		Set<String> initializedVars = new HashSet<>();
		
		Stack<ArgType> stack = new Stack<>();
		
		// --- Emit instructions ---
		for(int i = 0; i < code.length; i++)
		{
			int op = code[i];
			
			switch(op)
			{
				case LzOpcodes.RETURN:
				{
					// If nothing is on the stack, replicate LzVM's behavior of returning zero.
					if(stack.isEmpty()) insn.add(new LdcInsnNode(0.0));
					insn.add(new InsnNode(DRETURN));
				}
				break;
				
				case LzOpcodes.CONST:
				{
					int idx = code[++i];
					double val = body.dConstTable[idx];
					insn.add(new LdcInsnNode(val));
					stack.push(ArgType.DOUBLE);
				}
				break;
				
				case LzOpcodes.SCONST:
				{
					int constIdx = code[++i];
					insn.add(new LdcInsnNode(body.sConstTable[constIdx]));
					stack.push(ArgType.STRING);
				}
				break;
				
				case LzOpcodes.LOAD:
				{
					int idx = code[++i];
					
					if(/*useArray ||*/ idx < argCount)
					{
						insn.add(new VarInsnNode(ALOAD, /*idx >= argCount ? 2 :*/ 1));
						pushInt(insn, idx);
						insn.add(new InsnNode(DALOAD));
					} else
					{
						insn.add(new VarInsnNode(DLOAD, 1 + idx));
					}
					
					stack.push(ArgType.DOUBLE);
				}
				break;
				
				case LzOpcodes.STORE:
				{
					tryPop(i, op, stack, ArgType.DOUBLE);
					
					int idx = code[++i];
					if(/*useArray ||*/ idx < argCount)
					{
						insn.add(new VarInsnNode(DSTORE, 3));
						insn.add(new VarInsnNode(ALOAD, /*idx >= argCount ? 2 :*/ 1));
						pushInt(insn, idx);
						insn.add(new VarInsnNode(DLOAD, 3));
						insn.add(new InsnNode(DASTORE));
					} else
					{
						insn.add(new VarInsnNode(DSTORE, 1 + idx));
					}
				}
				break;
				
				case LzOpcodes.JCALL:
				{
					String owner = body.sConstTable[code[++i]];
					LzCallInsn call = body.callTable[code[++i]];
					
					if(jCallShutter != LzJCallShutter.ALLOW_EVERYTHING && !jCallShutter.permits(owner.replace('/', '.'), call))
						throw new LzVMCallNotFoundException(LzOpcodes.opNameIndexed(i, op) + ": jcall to " + owner + " was not allowed by the compilers jcall shutter.");
					
					insn.add(mCall(
							INVOKESTATIC,
							owner.replace('.', '/'),
							call.name,
							call.jvmDescriptor
					));
					
					for(int j = 0; j < call.argCount; j++)
					{
						ArgType pop = stack.pop();
						ArgType expect = call.argTypes[call.argCount - 1 - j];
						if(pop != expect)
							throw new LzVMCallNotFoundException(
									LzOpcodes.opNameIndexed(i, op) + ": Invalid jcall signature. Expected " + expect.desc + " in " + call.descriptor + " but argument " + j + " was " + pop.desc);
					}
					
					stack.push(call.returnType);
				}
				break;
				
				case LzOpcodes.CALL:
				{
					int callIdx = code[++i];
					LzCallInsn call = body.callTable[callIdx];
					
					MethodNode method;
					if((method = registeredCalls.get(call)) == null)
					{
						FieldNode callField = callGetter.apply(call);
						registeredCalls.put(call, method = createCallMethod(call, cn, usedMethodNames, callField));
					}
					
					// Add the lookup into the stack
					insn.add(new VarInsnNode(ALOAD, 0));
					
					// Call the method
					insn.add(mCall(
							INVOKESTATIC,
							cn.name,
							method.name,
							method.desc
					));
					
					for(int j = call.argCount - 1; j >= 0; j--)
					{
						ArgType pop = stack.pop();
						ArgType expect = call.argTypes[j];
						if(pop != expect)
							throw new LzVMCallNotFoundException(
									LzOpcodes.opNameIndexed(i, op) + ": Invalid call signature. Expected " + expect.desc + " in " + call.descriptor + " but argument " + j + " was " + pop.desc);
					}
					
					stack.push(call.returnType);
				}
				break;
				
				case LzOpcodes.ADD:
				{
					ArgType pop2 = stack.pop();
					ArgType pop1 = stack.pop();
					
					if(pop1 == ArgType.STRING || pop2 == ArgType.STRING)
					{
						// Concatenate string and something else
						insn.add(mCall(INVOKESTATIC, LzFMath, "concs", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;"));
						stack.push(ArgType.STRING);
						break;
					}
					
					insn.add(new InsnNode(DADD));
					stack.push(ArgType.DOUBLE);
				}
				break;
				case LzOpcodes.SUB:
				{
					tryPop(i, op, stack, ArgType.DOUBLE);
					tryPop(i, op, stack, ArgType.DOUBLE);
					insn.add(new InsnNode(DSUB));
					stack.push(ArgType.DOUBLE);
				}
				break;
				case LzOpcodes.MUL:
				{
					tryPop(i, op, stack, ArgType.DOUBLE);
					tryPop(i, op, stack, ArgType.DOUBLE);
					insn.add(new InsnNode(DMUL));
					stack.push(ArgType.DOUBLE);
				}
				break;
				case LzOpcodes.DIV:
				{
					tryPop(i, op, stack, ArgType.DOUBLE);
					insn.add(new InsnNode(DDIV));
					stack.push(ArgType.DOUBLE);
				}
				break;
				case LzOpcodes.MOD:
				{
					tryPop(i, op, stack, ArgType.DOUBLE);
					insn.add(new InsnNode(DREM));
					stack.push(ArgType.DOUBLE);
				}
				break;
				case LzOpcodes.EQUALS:
					insn.add(mCall(INVOKESTATIC, LzFMath, "eqd", "(DD)D")); stack.pop(); break;
				case LzOpcodes.NOT_EQUALS:
					insn.add(mCall(INVOKESTATIC, LzFMath, "neqd", "(DD)D")); stack.pop(); break;
				case LzOpcodes.GREATER_THAN:
					insn.add(mCall(INVOKESTATIC, LzFMath, "gtd", "(DD)D")); stack.pop(); break;
				case LzOpcodes.GREATER_EQ_THAN:
					insn.add(mCall(INVOKESTATIC, LzFMath, "getd", "(DD)D")); stack.pop(); break;
				case LzOpcodes.LESS_THAN:
					insn.add(mCall(INVOKESTATIC, LzFMath, "ltd", "(DD)D")); stack.pop(); break;
				case LzOpcodes.LESS_EQ_THAN:
					insn.add(mCall(INVOKESTATIC, LzFMath, "letd", "(DD)D")); stack.pop(); break;
				case LzOpcodes.COALESCE:
					insn.add(mCall(INVOKESTATIC, LzFMath, "coald", "(DD)D")); stack.pop(); break;
				case LzOpcodes.AND:
					insn.add(mCall(INVOKESTATIC, LzFMath, "andd", "(DD)D")); stack.pop(); break;
				case LzOpcodes.OR:
					insn.add(mCall(INVOKESTATIC, LzFMath, "ord", "(DD)D")); stack.pop(); break;
				case LzOpcodes.NOT:
				{
					tryPop(i, op, stack, ArgType.DOUBLE);
					insn.add(mCall(INVOKESTATIC, LzFMath, "notd", "(D)D"));
					stack.push(ArgType.DOUBLE);
					break;
				}
				case LzOpcodes.FSIN:
				{
					tryPop(i, op, stack, ArgType.DOUBLE);
					if(useSineLookupTable)
						insn.add(mCall(INVOKESTATIC, LzFMath, "sind", "(D)D"));
					else
						insn.add(mCall(INVOKESTATIC, Math, "sin", "(D)D"));
					stack.push(ArgType.DOUBLE);
					break;
				}
				case LzOpcodes.FCOS:
				{
					tryPop(i, op, stack, ArgType.DOUBLE);
					if(useSineLookupTable)
						insn.add(mCall(INVOKESTATIC, LzFMath, "cosd", "(D)D"));
					else
						insn.add(mCall(INVOKESTATIC, Math, "cos", "(D)D"));
					stack.push(ArgType.DOUBLE);
					break;
				}
				
				case LzOpcodes.READ:
				{
					int idx = code[++i];
					String nameStr = body.sConstTable[idx];
					FieldNode varHolder = varGetter.apply(nameStr);
					
					insn.add(new VarInsnNode(ALOAD, 0));
					insn.add(new FieldInsnNode(GETFIELD, cn.name, varHolder.name, varHolder.desc));
					insn.add(mCall(
							INVOKEINTERFACE,
							LzVarOp,
							"get",
							"()D"
					));
					
					stack.push(ArgType.DOUBLE);
					
					if(!initializedVars.contains(nameStr))
						varsToReset.add(nameStr);
				}
				break;
				
				case LzOpcodes.WRITE:
				{
					tryPop(i, op, stack, ArgType.DOUBLE);
					
					int idx = code[++i];
					String nameStr = body.sConstTable[idx];
					FieldNode varHolder = varGetter.apply(nameStr);
					
					insn.add(new VarInsnNode(ALOAD, 0));
					insn.add(new FieldInsnNode(GETFIELD, cn.name, varHolder.name, varHolder.desc));
					insn.add(mCall(
							INVOKESTATIC,
							LzFMath,
							"set",
							"(D" + L_LzVarOp + ")V"
					));
					
					initializedVars.add(nameStr);
				}
				break;
				
				case LzOpcodes.LABEL:
				{
					insn.add(labelMap.get(labelIndex.getAndIncrement()));
				}
				break;
				
				case LzOpcodes.JUMP:
				{
					int target = code[++i];
					insn.add(new JumpInsnNode(GOTO, labelMap.get(target)));
				}
				break;
				
				case LzOpcodes.JUMP_IF_FALSE:
				{
					int target = code[++i];
					insn.add(mCall(INVOKESTATIC, LzFMath, "isNotZero", "(D)Z"));
					insn.add(new JumpInsnNode(IFEQ, labelMap.get(target)));
				}
				break;
				
				case LzOpcodes.JUMP_IF_TRUE:
				{
					int target = code[++i];
					insn.add(mCall(INVOKESTATIC, LzFMath, "isZero", "(D)Z"));
					insn.add(new JumpInsnNode(IFEQ, labelMap.get(target)));
				}
				break;
				
				case LzOpcodes.TO_STRING:
				{
					ArgType pop = stack.pop();
					String inDesc = "Ljava/lang/Object;";
					if(pop == ArgType.DOUBLE) inDesc = pop.jvmDesc;
					insn.add(mCall(INVOKESTATIC, "java/lang/String", "valueOf", "(" + inDesc + ")Ljava/lang/String;"));
					stack.push(ArgType.STRING);
				}
				break;
				
				case LzOpcodes.POP:
				{
					stack.pop();
					insn.add(new InsnNode(POP));
				}
				break;
				
				case LzOpcodes.READ_INDEXED:
				{
					tryPop(i, op, stack, ArgType.DOUBLE);
					
					int idx = code[++i];
					String nameStr = body.sConstTable[idx];
					FieldNode varHolder = varGetter.apply(nameStr);
					
					insn.add(new VarInsnNode(ALOAD, 0));
					insn.add(new FieldInsnNode(GETFIELD, cn.name, varHolder.name, varHolder.desc));
					insn.add(mCall(
							INVOKESTATIC,
							LzVarOp,
							"get",
							"(D" + L_LzVarOp + ")D"
					));
					
					stack.push(ArgType.DOUBLE);
					
					if(!initializedVars.contains(nameStr))
						varsToReset.add(nameStr);
				}
				break;
				
				case LzOpcodes.WRITE_INDEXED:
				{
					// Pop two doubles for write
					tryPop(i, op, stack, ArgType.DOUBLE);
					tryPop(i, op, stack, ArgType.DOUBLE);
					
					int idx = code[++i];
					String nameStr = body.sConstTable[idx];
					FieldNode varHolder = varGetter.apply(nameStr);
					
					insn.add(new VarInsnNode(ALOAD, 0));
					insn.add(new FieldInsnNode(GETFIELD, cn.name, varHolder.name, varHolder.desc));
					insn.add(mCall(
							INVOKESTATIC,
							LzFMath,
							"set",
							"(DD" + L_LzVarOp + ")V"
					));
					
					initializedVars.add(nameStr);
				}
				break;
				
				case LzOpcodes.MIN:
				{
					tryPop(i, op, stack, ArgType.DOUBLE);
					tryPop(i, op, stack, ArgType.DOUBLE);
					insn.add(mCall(INVOKESTATIC, Math, "min", "(DD)D"));
					stack.push(ArgType.DOUBLE);
				}
				break;
				
				case LzOpcodes.MAX:
				{
					tryPop(i, op, stack, ArgType.DOUBLE);
					tryPop(i, op, stack, ArgType.DOUBLE);
					insn.add(mCall(INVOKESTATIC, Math, "max", "(DD)D"));
					stack.push(ArgType.DOUBLE);
				}
				break;
				
				default:
					throw new IllegalStateException("Unknown opcode: " + LzOpcodes.opNameIndexed(i, op));
			}
		}
		
		InsnList resetVars = new InsnList();
		for(String nameStr : varsToReset)
			if(nameStr.startsWith("temp.")) // we only reset temp vars
			{
				FieldNode varHolder = varGetter.apply(nameStr);
				resetVars.add(new VarInsnNode(ALOAD, 0)); // this
				resetVars.add(new FieldInsnNode(GETFIELD, cn.name, varHolder.name, varHolder.desc)); //.var
				resetVars.add(mCall(INVOKEINTERFACE, LzVarOp, "reset", "()V")); // .reset()
			}
		insn.insert(resetVars);
		
		// At the end, always return 0
		if(insn.getLast().getOpcode() != DRETURN)
		{
			insn.add(new LdcInsnNode(0.0));
			insn.add(new InsnNode(DRETURN));
		}
		//</editor-fold>
		
		//<editor-fold desc="instantiate(LzVariableStore)LzExpression">
		final MethodNode instantiate = new MethodNode(ACC_PUBLIC, "instantiate", "(" + L_LzVariableStore + ")" + L_LzExpression, null, null);
		cinsn = instantiate.instructions;
		if(usedFieldNames.size() == startingFieldCount)
		{
			FieldNode instanceNode = new FieldNode(ACC_PRIVATE, "instance", L_LzExpression, null, null);
			cn.fields.add(instanceNode);
			
			// no fields were added during generation, return a cached instance
			cinsn.add(new VarInsnNode(ALOAD, 0));
			cinsn.add(new FieldInsnNode(GETFIELD, cn.name, instanceNode.name, instanceNode.desc));
			cinsn.add(new InsnNode(ARETURN));
			
			// Modify factory constructor to initialize instance field
			{
				cinsn = new InsnList();
				cinsn.add(new VarInsnNode(ALOAD, 0));
				cinsn.add(new TypeInsnNode(NEW, cn.name));
				cinsn.add(new InsnNode(DUP));
				cinsn.add(new InsnNode(ACONST_NULL));
				cinsn.add(mCall(
						INVOKESPECIAL,
						cn.name,
						ctor.name,
						ctor.desc
				));
				cinsn.add(new FieldInsnNode(PUTFIELD, cn.name, instanceNode.name, instanceNode.desc));
				AbstractInsnNode ret = findNode(fctor.instructions, insnNode -> insnNode.getOpcode() == RETURN);
				fctor.instructions.insertBefore(ret, cinsn);
			}
			
			// Modify instance constructor to reference to itself
			{
				cinsn = new InsnList();
				cinsn.add(new VarInsnNode(ALOAD, 0));
				cinsn.add(new InsnNode(DUP));
				cinsn.add(new FieldInsnNode(PUTFIELD, cn.name, instanceNode.name, instanceNode.desc));
				AbstractInsnNode ret = findNode(ctor.instructions, insnNode -> insnNode.getOpcode() == RETURN);
				ctor.instructions.insertBefore(ret, cinsn);
			}
		} else
		{
			cinsn.add(new TypeInsnNode(NEW, cn.name)); // new expression instance
			cinsn.add(new InsnNode(DUP));
			cinsn.add(new VarInsnNode(ALOAD, 1)); // load LzVariableStore
			cinsn.add(mCall(
					INVOKESPECIAL,
					cn.name,
					ctor.name,
					ctor.desc
			));
			cinsn.add(new InsnNode(ARETURN));
		}
		cn.methods.add(instantiate);
		//</editor-fold>
		
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cn.accept(cw);
		return cw.toByteArray();
	}
	
	protected AbstractInsnNode findNode(InsnList list, Predicate<AbstractInsnNode> filter)
	{
		// Iterating over the InsnList isn't a thing in 6.2. Use older ListIterator approach.
		ListIterator<AbstractInsnNode> itr = list.iterator();
		while(itr.hasNext())
		{
			AbstractInsnNode i = itr.next();
			if(filter.test(i))
				return i;
		}
		return null;
	}
	
	protected Function<String, FieldNode> getVarFieldHelper(ClassNode owner, Set<String> usedFieldNames, MethodNode ctor)
	{
		Map<String, FieldNode> registeredVarFields = new HashMap<>();
		return var -> registeredVarFields.computeIfAbsent(var, fName ->
				{
					FieldNode fn = new FieldNode(
							ACC_PRIVATE | ACC_FINAL,
							JavaNamingConventions.processUniqueMethodName(fName, usedFieldNames),
							L_LzVarOp,
							null,
							null
					);
					fn.visibleAnnotations = generated(fName, Collections.emptyMap(), false);
					owner.fields.add(fn);
					
					InsnList inject = new InsnList();
					
					inject.add(new VarInsnNode(ALOAD, 0)); // this
					
					inject.add(new VarInsnNode(ALOAD, 1)); // store
					inject.add(new LdcInsnNode(fName)); // string
					inject.add(mCall( // call
							INVOKEINTERFACE,
							LzVariableStore,
							fName.startsWith("temp.") ? "tempVar" : "findVar",
							"(Ljava/lang/String;)" + L_LzVarOp
					));
					
					inject.add(new FieldInsnNode(PUTFIELD, owner.name, fn.name, fn.desc)); // store to field
					
					AbstractInsnNode ret = findNode(ctor.instructions, insnNode -> insnNode.getOpcode() == RETURN);
					ctor.instructions.insertBefore(ret, inject);
					
					return fn;
				}
		);
	}
	
	protected Function<LzCallInsn, FieldNode> getCallFieldHelper(ClassNode owner, Set<String> usedFieldNames, MethodNode ctor)
	{
		Map<LzCallInsn, FieldNode> registeredCallFields = new HashMap<>();
		return callId -> registeredCallFields.computeIfAbsent(callId, call ->
				{
					FieldNode fn = new FieldNode(
							ACC_PRIVATE | ACC_FINAL,
							JavaNamingConventions.processUniqueMethodName(call.name, usedFieldNames),
							L_LzCallOp,
							null,
							null
					);
					fn.visibleAnnotations = generated(call.name + call.descriptor, Collections.emptyMap(), false);
					owner.fields.add(fn);
					
					InsnList inject = new InsnList();
					
					inject.add(new VarInsnNode(ALOAD, 0)); // this
					inject.add(new VarInsnNode(ALOAD, 1)); // store
					inject.add(new LdcInsnNode(call.name)); // string
					inject.add(new LdcInsnNode(call.descriptor)); // string
					inject.add(mCall( // call
							INVOKEINTERFACE,
							LzVariableStore,
							"findCall",
							"(Ljava/lang/String;Ljava/lang/String;)" + L_LzCallOp
					));
					inject.add(new FieldInsnNode(PUTFIELD, owner.name, fn.name, fn.desc)); // store to field
					
					AbstractInsnNode ret = findNode(ctor.instructions, insnNode -> insnNode.getOpcode() == RETURN);
					ctor.instructions.insertBefore(ret, inject);
					
					return fn;
				}
		);
	}
	
	private MethodNode createCallMethod(LzCallInsn call, ClassNode owner, Set<String> usedMethodNames, FieldNode callField)
	{
		StringBuilder desc = new StringBuilder("(");
		ArgType[] argTypes = call.argTypes;
		for(ArgType t : argTypes)
			desc.append(t.jvmDesc);
		desc.append("L").append(owner.name).append(";)").append(call.returnType.jvmDesc);
		
		MethodNode m = new MethodNode(
				ACC_PRIVATE | ACC_STATIC,
				JavaNamingConventions.processUniqueMethodName(call.name, usedMethodNames),
				desc.toString(),
				null,
				null
		);
		
		final int argc = call.argCount;
		
		Map<String, Object> props = new HashMap<>();
		props.put("argCount", argc);
		m.visibleAnnotations = generated(call.name, props, false);
		
		InsnList insn = m.instructions;
		
		int RETURN_INSTRUCT = call.returnType == ArgType.DOUBLE ? DRETURN : ARETURN;
		
		// -----------------------------
		// 1. Compute JVM slot layout properly
		// -----------------------------
		int[] slots = new int[argc];
		int slotCursor = 0;
		for(int i = 0; i < argc; i++)
		{
			slots[i] = slotCursor;
			if(call.argTypes[i] == ArgType.DOUBLE)
				slotCursor += 2;
			else
				slotCursor += 1;
		}
		
		int storeSlot = slotCursor;
		
		// --- pack args into Object[] ---
		insn.add(new LdcInsnNode(argc));
		insn.add(new TypeInsnNode(ANEWARRAY, "java/lang/Object"));
		
		for(int i = 0; i < argc; ++i)
		{
			insn.add(new InsnNode(DUP));
			pushInt(insn, i);
			
			int slot = slots[i];
			
			if(call.argTypes[i] == ArgType.DOUBLE)
			{
				insn.add(new VarInsnNode(DLOAD, slot));
				insn.add(mCall(
						INVOKESTATIC,
						"java/lang/Double",
						"valueOf",
						"(D)Ljava/lang/Double;"
				));
			} else
			{
				insn.add(new VarInsnNode(ALOAD, slot));
			}
			
			insn.add(new InsnNode(AASTORE));
		}
		
		// --- DUP Object[] because we still need it after call lookup ---
		insn.add(new InsnNode(DUP));
		
		// --- resolve call ---
		insn.add(new VarInsnNode(ALOAD, storeSlot));
		insn.add(new FieldInsnNode(GETFIELD,
				owner.name,
				callField.name,
				callField.desc
		));
		
		// --- swap to correct order for call ---
		insn.add(new InsnNode(SWAP));
		
		// --- call(Object[]) ---
		insn.add(mCall(
				INVOKEINTERFACE,
				LzCallOp,
				"call",
				"([Ljava/lang/Object;)D"
		));
		
		insn.add(new InsnNode(RETURN_INSTRUCT));
		owner.methods.add(m);
		return m;
	}
	
	private List<AnnotationNode> generated(String value, Map<String, Object> rest, boolean forced)
	{
		if(!generatedAnnotation && !forced) return null;
		AnnotationNode an = new AnnotationNode(L_LzGenerated);
		an.values = new ArrayList<>();
		an.values.add("value");
		an.values.add(value);
		for(Map.Entry<String, Object> e : rest.entrySet())
		{
			an.values.add(e.getKey());
			an.values.add(e.getValue());
		}
		List<AnnotationNode> nodes = new ArrayList<>();
		nodes.add(an);
		return nodes;
	}
	
	private static void pushInt(InsnList insn, int v)
	{
		if(v >= -1 && v <= 5)
			insn.add(new InsnNode(ICONST_0 + v));
		else if(v <= Byte.MAX_VALUE)
			insn.add(new IntInsnNode(BIPUSH, v));
		else if(v <= Short.MAX_VALUE)
			insn.add(new IntInsnNode(SIPUSH, v));
		else
			insn.add(new LdcInsnNode(v));
	}
	
	@SuppressWarnings("deprecation") // methodCall is not actually deprecated,
	private static MethodInsnNode mCall(final int opcode, final String owner, final String name, final String descriptor)
	{
		return new MethodInsnNode(opcode, owner, name, descriptor);
	}
	
	private void tryPop(int i, int op, Stack<ArgType> stack, ArgType expect)
	{
		if(stack.isEmpty())
			throw new LzVMStackUnderflowException(LzOpcodes.opNameIndexed(i, op) + " expected " + expect.desc + " on the stack, but stack is empty.");
		ArgType popped = stack.pop();
		if(popped != expect)
			throw new LzVMException(LzOpcodes.opNameIndexed(i, op) + " expected " + expect.desc + " on the stack, but got " + popped.desc + ".");
	}
}