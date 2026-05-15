package dev.zeith.lzvm.program.io.writers;

import dev.zeith.lzvm.program.LzProgram;
import dev.zeith.lzvm.program.io.LzDataBlock;
import lombok.Value;

import java.io.*;
import java.util.*;

public abstract class LzWriter
{
	protected final List<NamedBlockEncoder> encoders = new ArrayList<>(LzDataBlock.B_COUNT);
	protected final DataOutputStream stream;
	protected final LzProgram program;
	
	public LzWriter(OutputStream stream, LzProgram program)
	{
		this.stream = stream instanceof DataOutputStream ? (DataOutputStream) stream : new DataOutputStream(stream);
		this.program = program;
		setup();
	}
	
	protected abstract void setup();
	
	protected final void regEncoder(int name, IBlockEncoder encoder)
	{
		this.encoders.add(new NamedBlockEncoder(name, encoder));
	}
	
	protected final void regEncoderSimple(int name, ISimpleBlockEncoder encoder)
	{
		this.encoders.add(new NamedBlockEncoder(name, s ->
		{
			encoder.write(s);
			return true;
		}
		));
	}
	
	public void write()
			throws IOException
	{
		for(NamedBlockEncoder e : encoders)
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try(DataOutputStream dos = new DataOutputStream(out))
			{
				if(!e.encoder.createBlock(dos))
					continue;
			}
			
			// Write block onto the stream
			writeDataBlock(e.name, out.toByteArray());
		}
		
		writeDataBlock(LzDataBlock.EOF, new byte[0]);
	}
	
	protected void writeDataBlock(int name, byte[] data)
			throws IOException
	{
		// Header
		stream.writeShort(name);
		stream.writeInt(data.length);
		
		// Body
		stream.write(data);
	}
	
	
	@Value
	protected static class NamedBlockEncoder
	{
		int name;
		IBlockEncoder encoder;
	}
	
	@FunctionalInterface
	protected interface IBlockEncoder
	{
		boolean createBlock(DataOutputStream out)
				throws IOException;
	}
	
	@FunctionalInterface
	protected interface ISimpleBlockEncoder
	{
		void write(DataOutputStream out)
				throws IOException;
	}
}