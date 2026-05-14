import dev.zeith.lzvm.jvm.LzExpression;
import org.zeith.hammeranims.api.animation.interp.Query;
import org.zeith.hammeranims.core.molang.MolangExpressionParser;

import java.util.*;

public class TestExpressionParser
{
	public static void main(String[] args)
	{
		Map<String, Map<Double, Double>> tests = Map.ofEntries(
				Map.entry("math.asin(q.anim_time)", Map.of(0D, 0D, 1D, 90D))
		);
		
		for(Map.Entry<String, Map<Double, Double>> test : tests.entrySet())
		{
			Query q = new Query();
			LzExpression expr = MolangExpressionParser.parse(test.getKey()).instantiate(q);
			
			System.out.println("f(x) = " + test.getKey().replace("q.anim_time", "x"));
			
			for(Map.Entry<Double, Double> testCase : test.getValue().entrySet())
			{
				q.anim_time = testCase.getKey();
				double res = expr.get();
				System.out.println("  f(" + q.anim_time + ") = " + res);
				if(Math.abs(res - testCase.getValue()) > 0.0001)
					throw new RuntimeException("Failed test " + test.getKey() + " with value " + q.anim_time + ", expected " + testCase.getValue());
			}
		}
	}
}