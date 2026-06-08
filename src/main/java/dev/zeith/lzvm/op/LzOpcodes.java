package dev.zeith.lzvm.op;

import lombok.SneakyThrows;

import java.util.*;

public interface LzOpcodes
{
	int RETURN = 1; // Complete execution and return current value.
	int CONST = 2; // Load value from const table onto the stack.
	int SCONST = 3; // Load string value from const table onto the stack.
	int LOAD = 4; // Load value from locals table onto the stack.
	int STORE = 5; // Store current value into locals table.
	int JCALL = 6; // Call static java method
	int CALL = 7; // Calls
	int ADD = 8; // Pops two doubles from the stack and pushes their sum onto the stack
	int SUB = 9; // Pops two doubles from the stack and pushes their sub onto the stack
	int MUL = 10; // Pops two doubles from the stack and pushes their mul onto the stack
	int DIV = 11; // Pops two doubles from the stack and pushes their div onto the stack
	int MOD = 12;
	int EQUALS = 13;
	int NOT_EQUALS = 14;
	int GREATER_THAN = 15;
	int GREATER_EQ_THAN = 16;
	int LESS_THAN = 17;
	int LESS_EQ_THAN = 18;
	int COALESCE = 19;
	int AND = 20;
	int OR = 21;
	int NOT = 22;
	int FSIN = 23;
	int FCOS = 24;
	int READ = 25; // Read variable by const (next insn points to a string name in varTable) and push onto the stack
	int WRITE = 26; // Pop the double from stack and write it into LzVariableStore (next insn points to a string name in varTable)
	int LABEL = 27; // Marks a label that the GOTO insn can jump to. All labels get their starting at 0.
	int JUMP = 28; // Jumps to the label by the next insn index. (the index 0 always points to the first label)
	int JUMP_IF_FALSE = 29;
	int JUMP_IF_TRUE = 30;
	int TO_STRING = 31; // Converts any object on the stack into a string
	int POP = 32;
	int READ_INDEXED = 33;
	int WRITE_INDEXED = 34;
	int MIN = 35;
	int MAX = 36;
	
	int I_LAST = MAX;
	int I_COUNT = I_LAST + 1;
	
	int[] EXTRA_SHIFTS = OpCodeIndexer.computeExtraShifts();
	int[] STACK_SHIFT = OpCodeIndexer.computeStackShift();
	
	Map<String, Integer> BY_NAME = OpCodeIndexer.computeByName();
	Map<Integer, String> NAME_OF = OpCodeIndexer.computeNameOf();
	
	static String opName(int opcode)
	{
		return "Op(" + LzOpcodes.NAME_OF.getOrDefault(opcode, Integer.toString(opcode)) + ")";
	}
	
	static String opNameIndexed(int idx, int opcode)
	{
		return "Op[" + idx + "](" + LzOpcodes.NAME_OF.getOrDefault(opcode, Integer.toString(opcode)) + ")";
	}
	
	@SuppressWarnings("DuplicatedCode")
	class OpCodeIndexer
	{
		private static int[] computeExtraShifts()
		{
			int[] c = new int[I_COUNT];
			c[CONST] = 1;
			c[SCONST] = 1;
			c[LOAD] = 1;
			c[STORE] = 1;
			c[JCALL] = 2;
			c[CALL] = 1;
			c[READ] = 1;
			c[WRITE] = 1;
			c[JUMP] = 1;
			c[JUMP_IF_FALSE] = 1;
			c[JUMP_IF_TRUE] = 1;
			c[READ_INDEXED] = 1;
			c[WRITE_INDEXED] = 1;
			return c;
		}
		
		private static int[] computeStackShift()
		{
			int[] c = new int[I_COUNT];
			
			c[RETURN] = -1;
			c[CONST] = 1;
			c[SCONST] = 1;
			c[LOAD] = 1;
			c[STORE] = -1;
			c[ADD] = -1;
			c[SUB] = -1;
			c[MUL] = -1;
			c[DIV] = -1;
			c[MOD] = -1;
			c[EQUALS] = -1;
			c[NOT_EQUALS] = -1;
			c[GREATER_THAN] = -1;
			c[GREATER_EQ_THAN] = -1;
			c[LESS_THAN] = -1;
			c[LESS_EQ_THAN] = -1;
			c[COALESCE] = -1;
			c[AND] = -1;
			c[OR] = -1;
			c[READ] = 1;
			c[WRITE] = -1;
			c[JUMP_IF_FALSE] = -1;
			c[JUMP_IF_TRUE] = -1;
			c[POP] = -1;
			c[WRITE_INDEXED] = -2;
			c[MIN] = -1;
			c[MAX] = -1;
			
			// calls are dynamic in nature and the argument count is always followed by the instruction
			c[JCALL] = -1;
			c[CALL] = -1;
			
			return c;
		}
		
		@SneakyThrows
		private static Map<String, Integer> computeByName()
		{
			Map<String, Integer> m = new HashMap<>();
			m.put("RETURN", RETURN);
			m.put("CONST", CONST);
			m.put("SCONST", SCONST);
			m.put("LOAD", LOAD);
			m.put("STORE", STORE);
			m.put("JCALL", JCALL);
			m.put("CALL", CALL);
			m.put("ADD", ADD);
			m.put("SUB", SUB);
			m.put("MUL", MUL);
			m.put("DIV", DIV);
			m.put("MOD", MOD);
			m.put("EQUALS", EQUALS);
			m.put("NOT_EQUALS", NOT_EQUALS);
			m.put("GREATER_THAN", GREATER_THAN);
			m.put("GREATER_EQ_THAN", GREATER_EQ_THAN);
			m.put("LESS_THAN", LESS_THAN);
			m.put("LESS_EQ_THAN", LESS_EQ_THAN);
			m.put("COALESCE", COALESCE);
			m.put("AND", AND);
			m.put("OR", OR);
			m.put("NOT", NOT);
			m.put("FSIN", FSIN);
			m.put("FCOS", FCOS);
			m.put("READ", READ);
			m.put("WRITE", WRITE);
			m.put("LABEL", LABEL);
			m.put("JUMP", JUMP);
			m.put("JUMP_IF_FALSE", JUMP_IF_FALSE);
			m.put("JUMP_IF_TRUE", JUMP_IF_TRUE);
			m.put("TO_STRING", TO_STRING);
			m.put("POP", POP);
			m.put("READ_INDEXED", READ_INDEXED);
			m.put("WRITE_INDEXED", WRITE_INDEXED);
			m.put("MIN", MIN);
			m.put("MAX", MAX);
			return Collections.unmodifiableMap(m);
		}
		
		@SneakyThrows
		private static Map<Integer, String> computeNameOf()
		{
			Map<Integer, String> m = new HashMap<>();
			m.put(RETURN, "RETURN");
			m.put(CONST, "CONST");
			m.put(SCONST, "SCONST");
			m.put(LOAD, "LOAD");
			m.put(STORE, "STORE");
			m.put(JCALL, "JCALL");
			m.put(CALL, "CALL");
			m.put(ADD, "ADD");
			m.put(SUB, "SUB");
			m.put(MUL, "MUL");
			m.put(DIV, "DIV");
			m.put(MOD, "MOD");
			m.put(EQUALS, "EQUALS");
			m.put(NOT_EQUALS, "NOT_EQUALS");
			m.put(GREATER_THAN, "GREATER_THAN");
			m.put(GREATER_EQ_THAN, "GREATER_EQ_THAN");
			m.put(LESS_THAN, "LESS_THAN");
			m.put(LESS_EQ_THAN, "LESS_EQ_THAN");
			m.put(COALESCE, "COALESCE");
			m.put(AND, "AND");
			m.put(OR, "OR");
			m.put(NOT, "NOT");
			m.put(FSIN, "FSIN");
			m.put(FCOS, "FCOS");
			m.put(READ, "READ");
			m.put(WRITE, "WRITE");
			m.put(LABEL, "LABEL");
			m.put(JUMP, "JUMP");
			m.put(JUMP_IF_FALSE, "JUMP_IF_FALSE");
			m.put(JUMP_IF_TRUE, "JUMP_IF_TRUE");
			m.put(TO_STRING, "TO_STRING");
			m.put(POP, "POP");
			m.put(READ_INDEXED, "READ_INDEXED");
			m.put(WRITE_INDEXED, "WRITE_INDEXED");
			m.put(MIN, "MIN");
			m.put(MAX, "MAX");
			return Collections.unmodifiableMap(m);
		}
	}
}