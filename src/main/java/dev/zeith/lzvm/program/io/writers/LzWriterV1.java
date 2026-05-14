package dev.zeith.lzvm.program.io.writers;

import dev.zeith.lzvm.program.*;
import dev.zeith.lzvm.program.io.LzDataBlock;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LzWriterV1
		extends LzWriter
{
	public LzWriterV1(OutputStream stream, LzProgram program)
	{
		super(stream, program);
	}
	
	@Override
	protected void setup()
	{
		regEncoderSimple(LzDataBlock.NAME, s -> s.write(program.name.getBytes(StandardCharsets.UTF_8)));
		
		regEncoder(LzDataBlock.PROGRAM_INFO, s ->
				{
					LzProgramInfo info = program.info;
					if(info == null) return false;
					s.writeInt(info.labelCount);
					s.writeInt(info.maxLocals);
					s.writeInt(info.maxStack);
					return true;
				}
		);
		
		regEncoderSimple(LzDataBlock.INSTRUCTIONS, s ->
				{
					int[] insn = program.body.insnList;
					s.writeInt(insn.length);
					for(int i : insn) s.write(i);
				}
		);
		
		regEncoderSimple(LzDataBlock.DOUBLE_CONSTANTS, s ->
				{
					double[] insn = program.body.dConstTable;
					s.writeShort(insn.length);
					for(double i : insn) s.writeDouble(i);
				}
		);
		
		regEncoderSimple(LzDataBlock.STRING_CONSTANTS, s ->
				{
					String[] insn = program.body.sConstTable;
					s.writeShort(insn.length);
					for(String i : insn) s.writeUTF(i);
				}
		);
		
		regEncoderSimple(LzDataBlock.CALLS, s ->
				{
					LzCallInsn[] insn = program.body.callTable;
					s.writeShort(insn.length);
					for(LzCallInsn c : insn) writeCall(s, c);
				}
		);
	}
	
	protected void writeCall(DataOutputStream out, LzCallInsn call)
			throws IOException
	{
		out.writeUTF(call.name);
		writeArg(out, call.returnType);
		out.writeByte(call.argCount);
		for(int i = 0; i < call.argCount; i++)
			writeArg(out, call.argTypes[i]);
	}
	
	protected void writeArg(DataOutputStream out, ArgType type)
			throws IOException
	{
		out.writeByte(type.ordinal());
	}
}