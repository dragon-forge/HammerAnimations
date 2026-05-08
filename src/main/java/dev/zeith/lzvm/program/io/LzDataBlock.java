package dev.zeith.lzvm.program.io;

import lombok.*;

import java.lang.reflect.*;
import java.util.*;

@Value
public class LzDataBlock
{
	public static final int EOF = 0;
	public static final int NAME = 1;
	public static final int PROGRAM_INFO = 2;
	public static final int INSTRUCTIONS = 3;
	public static final int DOUBLE_CONSTANTS = 4;
	public static final int STRING_CONSTANTS = 5;
	public static final int CALLS = 6;
	
	public static final int B_COUNT = CALLS + 1;
	
	public static final Map<String, Integer> BY_NAME = computeByName();
	public static final Map<Integer, String> NAME_OF = computeNameOf();
	
	int name;
	int length;
	
	public boolean is(int name)
	{
		return this.name == name;
	}
	
	@SneakyThrows
	private static Map<String, Integer> computeByName()
	{
		Map<String, Integer> m = new HashMap<>();
		for(Field f : LzDataBlock.class.getDeclaredFields())
		{
			if(Modifier.isStatic(f.getModifiers()) && f.getType().isPrimitive() && !f.getName().startsWith("B_"))
			{
				String name = f.getName();
				int insn = f.getInt(null);
				m.put(name, insn);
			}
		}
		return Collections.unmodifiableMap(m);
	}
	
	@SneakyThrows
	private static Map<Integer, String> computeNameOf()
	{
		Map<Integer, String> m = new HashMap<>();
		for(Field f : LzDataBlock.class.getDeclaredFields())
		{
			if(Modifier.isStatic(f.getModifiers()) && f.getType().isPrimitive() && !f.getName().startsWith("B_"))
			{
				String name = f.getName();
				int insn = f.getInt(null);
				m.put(insn, name);
			}
		}
		return Collections.unmodifiableMap(m);
	}
}