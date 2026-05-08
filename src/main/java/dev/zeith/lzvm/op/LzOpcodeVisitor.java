package dev.zeith.lzvm.op;

public interface LzOpcodeVisitor
{
	void visitInstruction(int index, int opcode, Object[] args);
}