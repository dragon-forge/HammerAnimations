import dev.zeith.lzvm.jvm.*;
import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.molang.expression.MLExpression;
import dev.zeith.lzvm.program.LzProgramBody;
import org.zeith.hammeranims.api.animation.interp.Query;

import java.util.*;

public class TestVar
{
	static IClassDefiner CLASS_LOADER = new LzJVM.LzClassLoader();
	static LzJvmCompiler JVM_COMPILER = new LzJvmCompiler();
	static MoLangCompiler MOLANG_COMPILER = new MoLangCompiler();
	
	public static void main(String[] args)
	{
		LzFactory fac = parse("variable.radius = 2.5;");
		
		Query q = new Query();
		fac.instantiate(q).get();
		System.out.println(q);
	}
	
	public static LzFactory parse(String expression)
	{
		// Constant expressions don't need caching since they don't involve any class generation.
		ArrayList<MLExpression> parsed = MOLANG_COMPILER.parse(expression);
		if(parsed.size() == 1)
		{
			OptionalDouble exp = parsed.get(0).asOptimizedDouble();
			if(exp.isPresent()) return new ConstantExpression(exp.getAsDouble());
		}
		
		LzProgramBody program = MOLANG_COMPILER.compile(parsed);
		return LzJVM.compile(JVM_COMPILER, program, 0, CLASS_LOADER);
	}
}