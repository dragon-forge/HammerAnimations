package dev.zeith.lzvm.program.io.readers;

import dev.zeith.lzvm.program.*;
import dev.zeith.lzvm.program.io.LzDataBlock;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LzReaderV1
		extends LzReader
{
	protected LzProgram.LzProgramBuilder program = LzProgram.builder();
	protected LzProgramBody.LzProgramBodyBuilder body = LzProgramBody.builder();
	protected LzProgramInfo.LzProgramInfoBuilder info;
	
	public LzReaderV1(InputStream stream)
	{
		super(stream);
	}
	
	@Override
	protected void setup()
	{
		regDecoder(LzDataBlock.NAME, readingByteArray(block -> program.name(new String(block, StandardCharsets.UTF_8))));
		
		regDecoder(LzDataBlock.PROGRAM_INFO, readingByteBuf(buf ->
				{
					LzProgramInfo.LzProgramInfoBuilder builder = LzProgramInfo.builder();
					builder.labelCount(buf.getInt());
					builder.maxLocals(buf.getInt());
					builder.maxStack(buf.getInt());
					this.info = builder;
				})
		);
		
		regDecoder(LzDataBlock.INSTRUCTIONS, readingByteBuf(b ->
				{
					int[] insn = new int[b.getInt()];
					for(int i = 0; i < insn.length; i++)
						insn[i] = b.get();
					body.insnList(insn);
				})
		);
		
		regDecoder(LzDataBlock.DOUBLE_CONSTANTS, readingByteBuf(b ->
				{
					double[] dConst = new double[Short.toUnsignedInt(b.getShort())];
					for(int i = 0; i < dConst.length; i++)
						dConst[i] = b.getDouble();
					body.dConstTable(dConst);
				})
		);
		
		regDecoder(LzDataBlock.STRING_CONSTANTS, readingStream(b ->
				{
					String[] sConst = new String[b.readUnsignedShort()];
					for(int i = 0; i < sConst.length; i++)
						sConst[i] = b.readUTF();
					body.sConstTable(sConst);
				})
		);
		
		regDecoder(LzDataBlock.CALLS, readingStream(b ->
				{
					LzCallInsn[] calls = new LzCallInsn[b.readUnsignedShort()];
					for(int i = 0; i < calls.length; i++)
						calls[i] = readCall(b);
					body.callTable(calls);
				})
		);
	}
	
	protected LzCallInsn readCall(DataInputStream in)
			throws IOException
	{
		String name = in.readUTF();
		ArgType ret = readArg(in);
		int argCount = in.readUnsignedByte();
		ArgType[] args = new ArgType[argCount];
		for(int i = 0; i < argCount; i++) args[i] = readArg(in);
		return new LzCallInsn(name, ret, args);
	}
	
	
	protected ArgType readArg(DataInputStream in)
			throws IOException
	{
		return ArgType.byOrdinal(in.readUnsignedByte());
	}
	
	@Override
	protected LzProgram buildProgram()
	{
		return program.info(info != null ? info.build() : null).body(body.build()).build();
	}
}