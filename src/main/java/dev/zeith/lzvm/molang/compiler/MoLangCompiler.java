package dev.zeith.lzvm.molang.compiler;

import dev.zeith.lzvm.jvm.*;
import dev.zeith.lzvm.molang.compiler.libs.*;
import dev.zeith.lzvm.molang.expression.*;
import dev.zeith.lzvm.molang.parser.MoParser;
import dev.zeith.lzvm.molang.tokenizer.Tokenizer;
import dev.zeith.lzvm.op.LzOpcodes;
import dev.zeith.lzvm.program.*;

import java.util.*;
import java.util.function.Function;

public class MoLangCompiler
{
	protected final Map<String, Function<NameExpression, MLExpression>> nameTransformers = new HashMap<>();
	protected final Map<LzCallInsn, IMoFunctionCallTransformer> callTransformers = new HashMap<>();
	protected final Map<String, String> aliases = new HashMap<>();
	protected final Set<String> requiredClasses = new HashSet<>();
	
	public boolean optimize = true;
	
	public MoLangCompiler()
	{
		linkLibrary(MoMathLibrary.INSTANCE);
		registerAlias("q", "query");
		registerAlias("v", "variable");
		registerAlias("t", "temp");
		registerAlias("c", "context");
	}
	
	public static LzCallInsn dUnaryOperator(String name)
	{
		return LzCallInsn.ofDbl(name, ArgType.DOUBLE);
	}
	
	public static LzCallInsn dBinaryOperator(String name)
	{
		return LzCallInsn.ofDbl(name, ArgType.DOUBLE, ArgType.DOUBLE);
	}
	
	public static LzCallInsn dTernaryOperator(String name)
	{
		return LzCallInsn.ofDbl(name, ArgType.DOUBLE, ArgType.DOUBLE, ArgType.DOUBLE);
	}
	
	public void includeRequiredClasses(Set<String> intoSet)
	{
		intoSet.addAll(this.requiredClasses);
	}
	
	public MoLangCompiler registerRequiredClass(String className)
	{
		requiredClasses.add(className);
		return this;
	}
	
	public MoLangCompiler registerAlias(String alias, String origin)
	{
		this.aliases.put(alias, origin);
		return this;
	}
	
	public MoLangCompiler linkLibrary(ICompilerLibrary library)
	{
		library.register(this);
		return this;
	}
	
	public MoLangCompiler registerTransformer(LzCallInsn call, IMoFunctionCallTransformer transformer)
	{
		callTransformers.put(call, transformer);
		return this;
	}
	
	public MoLangCompiler registerName(String name, Function<NameExpression, MLExpression> transformer)
	{
		nameTransformers.put(name, transformer);
		return this;
	}
	
	public IMoFunctionCallTransformer findCallTransformer(LzCallInsn call)
	{
		return callTransformers.get(call);
	}
	
	public MLExpression findNameTransformer(NameExpression nex)
	{
		Function<NameExpression, MLExpression> n = nameTransformers.get(nex.name);
		if(n != null) return n.apply(nex);
		return nex;
	}
	
	public LzFactory parseFactory(LzJvmCompiler compiler, String expression, IClassDefiner definer)
	{
		ArrayList<MLExpression> parsed = parse(expression);
		if(parsed.size() == 1)
		{
			OptionalDouble exp = parsed.get(0).asOptimizedDouble();
			if(exp.isPresent())
				return new ConstantExpression(exp.getAsDouble());
		}
		return LzJVM.compile(compiler, compile(parsed), 0, definer);
	}
	
	public LzProgramBody parseAndCompile(String expression)
	{
		return compile(parse(expression));
	}
	
	public LzProgramBody compile(ArrayList<MLExpression> expression)
	{
		LzProgramBuilder pb = LzProgramBuilder.of(0);
		for(MLExpression expr : expression) expr.toLz(this, pb, ExpressionScope.EMPTY);
		pb.addInsn(LzOpcodes.RETURN);
		return pb.build();
	}
	
	public ArrayList<MLExpression> parse(String expression)
	{
		Tokenizer tkn = new Tokenizer();
		tkn.init(expression);
		MoParser p = new MoParser(this.aliases, tkn);
		ArrayList<MLExpression> exprs = p.parse();
		
		if(optimize)
			for(int i = 0, len = exprs.size(); i < len; i++)
			{
				MLExpression expr = exprs.get(i);
				MLExpression opt = expr.optimizeStatic(this);
				if(opt != expr) exprs.set(i, opt);
			}
		
		return exprs;
	}
}