package dev.zeith.lzvm.program.io;

import dev.zeith.lzvm.program.LzProgram;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LzProgramIo
{
	private static final byte[] LZP_HEADER = "LzP".getBytes(StandardCharsets.US_ASCII);
	private static final byte VERSION = 1;
	
	public static LzProgram read(InputStream in)
			throws IOException
	{
		for(byte b : LZP_HEADER)
			if(b != in.read())
				throw new MalformedLzProgramException("Broken LzP header.");
		int ver = in.read();
		LzProgramVersion reg = LzProgramVersion.find(ver);
		if(reg.reader == null) throw new MalformedLzProgramException("Unknown LzP version " + ver);
		return reg.reader.apply(in).read();
	}
	
	public static void write(OutputStream out, LzProgram program, LzProgramVersion version)
			throws IOException
	{
		out.write(LZP_HEADER);
		out.write(VERSION);
		version.writer.apply(out, program).write();
	}
}