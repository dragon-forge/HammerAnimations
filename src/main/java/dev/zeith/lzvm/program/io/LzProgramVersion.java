package dev.zeith.lzvm.program.io;

import dev.zeith.lzvm.program.LzProgram;
import dev.zeith.lzvm.program.io.readers.*;
import dev.zeith.lzvm.program.io.writers.*;
import lombok.AllArgsConstructor;

import java.io.*;
import java.util.function.*;

@AllArgsConstructor
public enum LzProgramVersion
{
	UNKNOWN(null, null),
	V1(LzReaderV1::new, LzWriterV1::new);
	
	public final Function<InputStream, LzReader> reader;
	public final BiFunction<OutputStream, LzProgram, LzWriter> writer;
	
	public static LzProgramVersion find(int version)
	{
		LzProgramVersion[] reg = values();
		if(version < 1 || version > reg.length) return UNKNOWN;
		return reg[version];
	}
}