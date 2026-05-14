package motest.util;

import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.LzMath;
import dev.zeith.lzvm.op.*;
import org.zeith.hammeranims.core.molang.MolangExpressionParser;

import java.util.*;

public class LzTestRunner
{
	public static void runTrue(String expression)
	{
		DummyVariableStore vars = new DummyVariableStore();
		double res = run(vars, expression);
		if(!LzMath.isOne(res))
			throw new AssertionError(expression + " expected to be true(1), but got " + res + ";\nVariables: " + vars);
	}
	
	public static void runLog(String expression)
	{
		DummyVariableStore vars = new DummyVariableStore();
		double res = run(vars, expression);
		System.out.println(expression + " = " + String.format("%0,2f", res));
		if(!vars.vars.isEmpty())
			System.out.println(" Variables: " + vars);
	}
	
	public static double run(String expression)
	{
		return run(new DummyVariableStore(), expression);
	}
	
	public static double run(LzVariableStore vars, String expression)
	{
		MolangExpressionParser.disableOptimizations();
		return MolangExpressionParser.parse(expression).instantiate(vars).get();
	}
	
	public static class DummyVariableStore
			implements LzVariableStore
	{
		protected final Map<String, LzVarOp> vars = new HashMap<String, LzVarOp>();
		
		@Override
		public LzCallOp findCall(String name, String descriptor)
		{
			return LzCallOp.NO_OP;
		}
		
		@Override
		public LzVarOp findVar(String name)
		{
			return vars.computeIfAbsent(name, s -> LzVarOp.readWrite());
		}
		
		@Override
		public LzVarOp tempVar(String name)
		{
			return findVar(name);
		}
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			for(Map.Entry<String, LzVarOp> var : vars.entrySet())
				sb.append("\n\t").append(var.getKey()).append(" = ").append(var.getValue());
			return sb.toString();
		}
	}
}