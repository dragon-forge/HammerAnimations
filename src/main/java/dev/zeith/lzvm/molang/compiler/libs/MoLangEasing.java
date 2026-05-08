package dev.zeith.lzvm.molang.compiler.libs;

import dev.zeith.lzvm.jvm.LzMath;
import dev.zeith.lzvm.molang.compiler.IMoFunctionCallTransformer;
import dev.zeith.lzvm.molang.expression.*;
import dev.zeith.lzvm.program.LzCallInsn;

import java.util.*;
import java.util.function.Function;

import static dev.zeith.lzvm.molang.compiler.MoLangCompiler.dTernaryOperator;
import static dev.zeith.lzvm.molang.compiler.libs.ICompilerLibrary.*;

public class MoLangEasing
{
	private static final double C1 = 1.70158;
	private static final double C2 = C1 * 1.525;
	private static final double C3 = C1 + 1.0;
	private static final double C4 = (2.0 * Math.PI) / 3.0;
	private static final double C5 = (2.0 * Math.PI) / 4.5;
	
	private MoLangEasing()
	{
	}
	
	public static void bind(Map<LzCallInsn, IMoFunctionCallTransformer> c)
	{
		final String JMoEasing = MoLangEasing.class.getName().replace('.', '/');
		
		c.put(dTernaryOperator("ease_in_back"), argsAndExtraPure(dtOpt(MoLangEasing::easeInBack), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInBack"))));
		c.put(dTernaryOperator("ease_in_bounce"), argsAndExtraPure(dtOpt(MoLangEasing::easeInBounce), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInBounce"))));
		c.put(dTernaryOperator("ease_in_circ"), argsAndExtraPure(dtOpt(MoLangEasing::easeInCirc), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInCirc"))));
		c.put(dTernaryOperator("ease_in_cubic"), argsAndExtraPure(dtOpt(MoLangEasing::easeInCubic), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInCubic"))));
		c.put(dTernaryOperator("ease_in_elastic"), argsAndExtraPure(dtOpt(MoLangEasing::easeInElastic), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInElastic"))));
		c.put(dTernaryOperator("ease_in_expo"), argsAndExtraPure(dtOpt(MoLangEasing::easeInExpo), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInExpo"))));
		c.put(dTernaryOperator("ease_in_out_back"), argsAndExtraPure(dtOpt(MoLangEasing::easeInOutBack), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInOutBack"))));
		c.put(dTernaryOperator("ease_in_out_bounce"), argsAndExtraPure(dtOpt(MoLangEasing::easeInOutBounce), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInOutBounce"))));
		c.put(dTernaryOperator("ease_in_out_circ"), argsAndExtraPure(dtOpt(MoLangEasing::easeInOutCirc), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInOutCirc"))));
		c.put(dTernaryOperator("ease_in_out_cubic"), argsAndExtraPure(dtOpt(MoLangEasing::easeInOutCubic), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInOutCubic"))));
		c.put(dTernaryOperator("ease_in_out_elastic"), argsAndExtraPure(dtOpt(MoLangEasing::easeInOutElastic), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInOutElastic"))));
		c.put(dTernaryOperator("ease_in_out_expo"), argsAndExtraPure(dtOpt(MoLangEasing::easeInOutExpo), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInOutExpo"))));
		c.put(dTernaryOperator("ease_in_out_quad"), argsAndExtraPure(dtOpt(MoLangEasing::easeInOutQuad), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInOutQuad"))));
		c.put(dTernaryOperator("ease_in_out_quart"), argsAndExtraPure(dtOpt(MoLangEasing::easeInOutQuart), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInOutQuart"))));
		c.put(dTernaryOperator("ease_in_out_quint"), argsAndExtraPure(dtOpt(MoLangEasing::easeInOutQuint), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInOutQuint"))));
		c.put(dTernaryOperator("ease_in_out_sine"), argsAndExtraPure(dtOpt(MoLangEasing::easeInOutSine), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInOutSine"))));
		c.put(dTernaryOperator("ease_in_quad"), argsAndExtraPure(dtOpt(MoLangEasing::easeInQuad), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInQuad"))));
		c.put(dTernaryOperator("ease_in_quart"), argsAndExtraPure(dtOpt(MoLangEasing::easeInQuart), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInQuart"))));
		c.put(dTernaryOperator("ease_in_quint"), argsAndExtraPure(dtOpt(MoLangEasing::easeInQuint), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInQuint"))));
		c.put(dTernaryOperator("ease_in_sine"), argsAndExtraPure(dtOpt(MoLangEasing::easeInSine), b -> b.addJCall(JMoEasing, dTernaryOperator("easeInSine"))));
		c.put(dTernaryOperator("ease_out_back"), argsAndExtraPure(dtOpt(MoLangEasing::easeOutBack), b -> b.addJCall(JMoEasing, dTernaryOperator("easeOutBack"))));
		c.put(dTernaryOperator("ease_out_bounce"), argsAndExtraPure(dtOpt(MoLangEasing::easeOutBounce), b -> b.addJCall(JMoEasing, dTernaryOperator("easeOutBounce"))));
		c.put(dTernaryOperator("ease_out_circ"), argsAndExtraPure(dtOpt(MoLangEasing::easeOutCirc), b -> b.addJCall(JMoEasing, dTernaryOperator("easeOutCirc"))));
		c.put(dTernaryOperator("ease_out_cubic"), argsAndExtraPure(dtOpt(MoLangEasing::easeOutCubic), b -> b.addJCall(JMoEasing, dTernaryOperator("easeOutCubic"))));
		c.put(dTernaryOperator("ease_out_elastic"), argsAndExtraPure(dtOpt(MoLangEasing::easeOutElastic), b -> b.addJCall(JMoEasing, dTernaryOperator("easeOutElastic"))));
		c.put(dTernaryOperator("ease_out_expo"), argsAndExtraPure(dtOpt(MoLangEasing::easeOutExpo), b -> b.addJCall(JMoEasing, dTernaryOperator("easeOutExpo"))));
		c.put(dTernaryOperator("ease_out_quad"), argsAndExtraPure(dtOpt(MoLangEasing::easeOutQuad), b -> b.addJCall(JMoEasing, dTernaryOperator("easeOutQuad"))));
		c.put(dTernaryOperator("ease_out_quart"), argsAndExtraPure(dtOpt(MoLangEasing::easeOutQuart), b -> b.addJCall(JMoEasing, dTernaryOperator("easeOutQuart"))));
		c.put(dTernaryOperator("ease_out_quint"), argsAndExtraPure(dtOpt(MoLangEasing::easeOutQuint), b -> b.addJCall(JMoEasing, dTernaryOperator("easeOutQuint"))));
		c.put(dTernaryOperator("ease_out_sine"), argsAndExtraPure(dtOpt(MoLangEasing::easeOutSine), b -> b.addJCall(JMoEasing, dTernaryOperator("easeOutSine"))));
	}
	
	private static Function<FuncCallExpression, MLExpression> dtOpt(MoMathLibrary.DoubleTernaryOperator operator)
	{
		return call ->
		{
			OptionalDouble opt1 = call.getChildren()[0].asOptimizedDouble();
			OptionalDouble opt2 = call.getChildren()[1].asOptimizedDouble();
			OptionalDouble opt3 = call.getChildren()[2].asOptimizedDouble();
			if(opt1.isPresent() && opt2.isPresent() && opt3.isPresent()) return new NumberExpression(operator.applyAsDouble(opt1.getAsDouble(), opt2.getAsDouble(), opt3.getAsDouble()));
			return null;
		};
	}
	
	public static double lerp(double start, double end, double t)
	{
		return start + (end - start) * t;
	}
	
	public static double easeInBack(double start, double end, double t)
	{
		double v = C3 * t * t * t - C1 * t * t;
		return lerp(start, end, v);
	}
	
	public static double easeInBounce(double start, double end, double t)
	{
		double v = 1.0 - easeOutBounce01(1.0 - t);
		return lerp(start, end, v);
	}
	
	public static double easeInCirc(double start, double end, double t)
	{
		double v = 1.0 - Math.sqrt(1.0 - t * t);
		return lerp(start, end, v);
	}
	
	public static double easeInCubic(double start, double end, double t)
	{
		double v = t * t * t;
		return lerp(start, end, v);
	}
	
	public static double easeInElastic(double start, double end, double t)
	{
		final double v;
		
		if(t == 0.0)
		{
			v = 0.0;
		} else if(t == 1.0)
		{
			v = 1.0;
		} else
		{
			v = -Math.pow(2.0, 10.0 * t - 10.0)
					* LzMath.sind((t * 10.0 - 10.75) * C4);
		}
		
		return lerp(start, end, v);
	}
	
	public static double easeInExpo(double start, double end, double t)
	{
		double v = (t == 0.0)
				   ? 0.0
				   : Math.pow(2.0, 10.0 * t - 10.0);
		
		return lerp(start, end, v);
	}
	
	public static double easeInOutBack(double start, double end, double t)
	{
		double v;
		
		if(t < 0.5)
		{
			v = (Math.pow(2.0 * t, 2.0)
					* ((C2 + 1.0) * 2.0 * t - C2)) / 2.0;
		} else
		{
			v = (Math.pow(2.0 * t - 2.0, 2.0)
					* ((C2 + 1.0) * (t * 2.0 - 2.0) + C2) + 2.0) / 2.0;
		}
		
		return lerp(start, end, v);
	}
	
	public static double easeInOutBounce(double start, double end, double t)
	{
		double v;
		
		if(t < 0.5)
		{
			v = (1.0 - easeOutBounce01(1.0 - 2.0 * t)) / 2.0;
		} else
		{
			v = (1.0 + easeOutBounce01(2.0 * t - 1.0)) / 2.0;
		}
		
		return lerp(start, end, v);
	}
	
	public static double easeInOutCirc(double start, double end, double t)
	{
		double v;
		
		if(t < 0.5)
		{
			v = (1.0 - Math.sqrt(1.0 - Math.pow(2.0 * t, 2.0))) / 2.0;
		} else
		{
			v = (Math.sqrt(1.0 - Math.pow(-2.0 * t + 2.0, 2.0)) + 1.0) / 2.0;
		}
		
		return lerp(start, end, v);
	}
	
	public static double easeInOutCubic(double start, double end, double t)
	{
		double v;
		
		if(t < 0.5)
		{
			v = 4.0 * t * t * t;
		} else
		{
			v = 1.0 - Math.pow(-2.0 * t + 2.0, 3.0) / 2.0;
		}
		
		return lerp(start, end, v);
	}
	
	public static double easeInOutElastic(double start, double end, double t)
	{
		final double v;
		
		if(t == 0.0)
		{
			v = 0.0;
		} else if(t == 1.0)
		{
			v = 1.0;
		} else if(t < 0.5)
		{
			v = -(Math.pow(2.0, 20.0 * t - 10.0)
					* LzMath.sind((20.0 * t - 11.125) * C5)) / 2.0;
		} else
		{
			v = (Math.pow(2.0, -20.0 * t + 10.0)
					* LzMath.sind((20.0 * t - 11.125) * C5)) / 2.0 + 1.0;
		}
		
		return lerp(start, end, v);
	}
	
	public static double easeInOutExpo(double start, double end, double t)
	{
		final double v;
		
		if(t == 0.0)
		{
			v = 0.0;
		} else if(t == 1.0)
		{
			v = 1.0;
		} else if(t < 0.5)
		{
			v = Math.pow(2.0, 20.0 * t - 10.0) / 2.0;
		} else
		{
			v = (2.0 - Math.pow(2.0, -20.0 * t + 10.0)) / 2.0;
		}
		
		return lerp(start, end, v);
	}
	
	public static double easeInOutQuad(double start, double end, double t)
	{
		double v;
		
		if(t < 0.5)
		{
			v = 2.0 * t * t;
		} else
		{
			v = 1.0 - Math.pow(-2.0 * t + 2.0, 2.0) / 2.0;
		}
		
		return lerp(start, end, v);
	}
	
	public static double easeInOutQuart(double start, double end, double t)
	{
		double v;
		
		if(t < 0.5)
		{
			v = 8.0 * Math.pow(t, 4.0);
		} else
		{
			v = 1.0 - Math.pow(-2.0 * t + 2.0, 4.0) / 2.0;
		}
		
		return lerp(start, end, v);
	}
	
	public static double easeInOutQuint(double start, double end, double t)
	{
		double v;
		
		if(t < 0.5)
		{
			v = 16.0 * Math.pow(t, 5.0);
		} else
		{
			v = 1.0 - Math.pow(-2.0 * t + 2.0, 5.0) / 2.0;
		}
		
		return lerp(start, end, v);
	}
	
	public static double easeInOutSine(double start, double end, double t)
	{
		double v = -(LzMath.cosd(Math.PI * t) - 1.0) / 2.0;
		return lerp(start, end, v);
	}
	
	public static double easeInQuad(double start, double end, double t)
	{
		double v = t * t;
		return lerp(start, end, v);
	}
	
	public static double easeInQuart(double start, double end, double t)
	{
		double v = t * t * t * t;
		return lerp(start, end, v);
	}
	
	public static double easeInQuint(double start, double end, double t)
	{
		double v = t * t * t * t * t;
		return lerp(start, end, v);
	}
	
	public static double easeInSine(double start, double end, double t)
	{
		double v = 1.0 - LzMath.cosd((t * Math.PI) / 2.0);
		return lerp(start, end, v);
	}
	
	public static double easeOutBack(double start, double end, double t)
	{
		double x = t - 1.0;
		double v = 1.0 + C3 * x * x * x + C1 * x * x;
		return lerp(start, end, v);
	}
	
	public static double easeOutBounce(double start, double end, double t)
	{
		double v = easeOutBounce01(t);
		return lerp(start, end, v);
	}
	
	public static double easeOutCirc(double start, double end, double t)
	{
		double x = t - 1.0;
		double v = Math.sqrt(1.0 - x * x);
		return lerp(start, end, v);
	}
	
	public static double easeOutCubic(double start, double end, double t)
	{
		double x = 1.0 - t;
		double v = 1.0 - x * x * x;
		return lerp(start, end, v);
	}
	
	public static double easeOutElastic(double start, double end, double t)
	{
		final double v;
		
		if(t == 0.0)
		{
			v = 0.0;
		} else if(t == 1.0)
		{
			v = 1.0;
		} else
		{
			v = Math.pow(2.0, -10.0 * t)
					* LzMath.sind((t * 10.0 - 0.75) * C4)
					+ 1.0;
		}
		
		return lerp(start, end, v);
	}
	
	public static double easeOutExpo(double start, double end, double t)
	{
		double v = (t == 1.0)
				   ? 1.0
				   : 1.0 - Math.pow(2.0, -10.0 * t);
		
		return lerp(start, end, v);
	}
	
	public static double easeOutQuad(double start, double end, double t)
	{
		double v = 1.0 - (1.0 - t) * (1.0 - t);
		return lerp(start, end, v);
	}
	
	public static double easeOutQuart(double start, double end, double t)
	{
		double x = 1.0 - t;
		double v = 1.0 - x * x * x * x;
		return lerp(start, end, v);
	}
	
	public static double easeOutQuint(double start, double end, double t)
	{
		double x = 1.0 - t;
		double v = 1.0 - x * x * x * x * x;
		return lerp(start, end, v);
	}
	
	public static double easeOutSine(double start, double end, double t)
	{
		double v = LzMath.sind((t * Math.PI) / 2.0);
		return lerp(start, end, v);
	}
	
	// ------------------------------------------------------------
	// Internal helpers
	// ------------------------------------------------------------
	
	private static double easeOutBounce01(double t)
	{
		final double n1 = 7.5625;
		final double d1 = 2.75;
		
		if(t < 1.0 / d1)
		{
			return n1 * t * t;
		} else if(t < 2.0 / d1)
		{
			t -= 1.5 / d1;
			return n1 * t * t + 0.75;
		} else if(t < 2.5 / d1)
		{
			t -= 2.25 / d1;
			return n1 * t * t + 0.9375;
		} else
		{
			t -= 2.625 / d1;
			return n1 * t * t + 0.984375;
		}
	}
}