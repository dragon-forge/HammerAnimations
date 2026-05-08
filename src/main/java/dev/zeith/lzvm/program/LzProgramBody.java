package dev.zeith.lzvm.program;

import dev.zeith.lzvm.StringQuoter;
import dev.zeith.lzvm.op.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Builder
@EqualsAndHashCode(exclude = {"disassemblyCache"})
@AllArgsConstructor
@ToString(exclude = {"disassemblyCache"})
public class LzProgramBody
{
	private static final Object[] EMPTY_ARGS = new Object[0];
	
	public final int @NotNull [] insnList;
	public final double @NotNull [] dConstTable;
	public final @NotNull String[] sConstTable;
	public final @NotNull LzCallInsn[] callTable;
	
	private final String[] disassemblyCache = new String[2];
	
	public void visitOps(boolean skipArgs, LzOpcodeVisitor visitor)
	{
		for(int i = 0; i < insnList.length; i++)
		{
			int ins = insnList[i];
			final int j = i;
			int extra = LzOpcodes.EXTRA_SHIFTS[ins];
			Object[] args = skipArgs || extra == 0 ? EMPTY_ARGS : new Object[extra];
			if(!skipArgs)
			{
				for(int k = 0; k < extra; k++)
					args[k] = getConstant(ins, k, i);
			}
			i += extra;
			visitor.visitInstruction(j, ins, args);
		}
	}
	
	public String disassemble(boolean newlines)
	{
		int cacheIdx = newlines ? 1 : 0;
		if(disassemblyCache[cacheIdx] != null) return disassemblyCache[cacheIdx];
		
		StringBuilder sb = new StringBuilder();
		String insnSuffix = newlines ? "\n" : "; ";
		
		visitOps(false, (index, opcode, args) ->
				{
					sb.append(LzOpcodes.NAME_OF.get(opcode));
					for(Object arg : args)
						sb.append(", ").append(constToString(arg));
					sb.append(insnSuffix);
				}
		);
		
		while(sb.charAt(sb.length() - 1) == '\n') sb.deleteCharAt(sb.length() - 1);
		
		return disassemblyCache[cacheIdx] = sb.toString();
	}
	
	public static String constToString(Object c)
	{
		if(c instanceof Number) return c.toString();
		if(c instanceof String) return StringQuoter.quote(c.toString());
		if(c instanceof LzCallInsn)
		{
			LzCallInsn call = (LzCallInsn) c;
			return call.name + call.descriptor;
		}
		return c == null ? "null" : c.toString();
	}
	
	public Object getConstant(int insn, int ordinal, int programIndex)
	{
		int value = insnList[programIndex + 1 + ordinal];
		switch(insn)
		{
			case LzOpcodes.CONST:
				return dConstTable[value];
			case LzOpcodes.CALL:
				return callTable[value];
			case LzOpcodes.JCALL:
				return ordinal == 1 ? callTable[value] : sConstTable[value];
			case LzOpcodes.READ:
			case LzOpcodes.WRITE:
			case LzOpcodes.SCONST:
			case LzOpcodes.READ_INDEXED:
			case LzOpcodes.WRITE_INDEXED:
				return sConstTable[value];
		}
		return value;
	}
	
	public LzProgramInfo computeInfo()
	{
		int[] insn = this.insnList;
		
		int ptr = -1;
		int maxStackPos = -1;
		int maxLocal = -1;
		int labelCount = 0;
		
		for(int i = 0, len = insn.length; i < len; i++)
		{
			int ins = insn[i];
			i += LzOpcodes.EXTRA_SHIFTS[ins];
			int shift = LzOpcodes.STACK_SHIFT[ins];
			switch(ins)
			{
				case LzOpcodes.LABEL:
					++labelCount;
					break;
				case LzOpcodes.LOAD:
				case LzOpcodes.STORE:
					maxLocal = Math.max(maxLocal, insn[i]);
					break;
				case LzOpcodes.CALL:
				case LzOpcodes.JCALL:
				{
					int callIdx = insn[i];
					LzCallInsn cinsn = callTable[callIdx];
					ptr -= cinsn.argCount;
					ptr++;
				}
				break;
				default:
					ptr += shift;
					break;
			}
			maxStackPos = Math.max(maxStackPos, ptr);
		}
		
		int maxStack = maxStackPos + 1;
		++maxLocal;
		
		return new LzProgramInfo(
				maxStack,
				maxLocal,
				labelCount
		);
	}
}