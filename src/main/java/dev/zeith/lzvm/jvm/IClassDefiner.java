package dev.zeith.lzvm.jvm;

public interface IClassDefiner
{
	Class<?> defineClass(byte[] bytecode);
}