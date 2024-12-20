package org.zeith.hammeranims.core.js;

import org.zeith.hammeranims.standalone.utils.math.MathHelper;
import lombok.val;
import org.zeith.hammeranims.api.animation.interp.IVariableAccess;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.standalone.wasm.JsFactory;
import org.zeith.hammeranims.standalone.wasm.itfs.MathJSObject;

import java.util.Locale;
import java.util.Random;

public class ExpressionParser
{
	public static final MathJSObject MATH = new MathJS();
	
	public static <T extends IVariableAccess> InterpolatedDouble<T> parse(String expression)
	{
		expression = ExpressionFixer.fixExpression(expression);
		
		// Try parsing expression as constant first.
		try
		{
			return InterpolatedDouble.constant(Double.parseDouble(expression));
		} catch(Throwable e)
		{
		}
		
		String fun = "(function(unused, mathjs, q, query) {\n\treturn " + expression.toLowerCase(Locale.ROOT).replace("math", "mathjs") + ";\n})";
		
		Object[] args = new Object[] {
				null, MATH, null, null
		};
		
		val res = JsFactory.parse4Args(fun, args);
		if(res == null) return query -> 0;
		
		return query ->
		{
			try
			{
				args[2] = args[3] = query.toJS();
				return res.get();
			} catch(RuntimeException e)
			{
				e.printStackTrace();
				return Double.NaN;
			}
		};
	}
	
	private static final Random rng = new Random();
	
	public static class MathJS
			implements MathJSObject
	{
		private final double pi = Math.PI;
		
		@Override
		public double getPi() {return pi;}
		
		@Override
		public double cos(double x)
		{
			return MathHelper.cos(x * MathHelper.torad);
		}
		
		@Override
		public double sin(double x)
		{
			return MathHelper.sin(x * MathHelper.torad);
		}
		
		@Override
		public double abs(double x)
		{
			return Math.abs(x);
		}
		
		@Override
		public double clamp(double value, double min, double max)
		{
			return value < min ? min : value > max ? max : value;
		}
		
		@Override
		public double pow(double base, double exponent)
		{
			return Math.pow(base, exponent);
		}
		
		@Override
		public double sqrt(double x)
		{
			return Math.sqrt(x);
		}
		
		@Override
		public double asin(double x)
		{
			return Math.asin(x);
		}
		
		@Override
		public double acos(double x)
		{
			return Math.acos(x);
		}
		
		@Override
		public double atan(double x)
		{
			return Math.atan(x);
		}
		
		@Override
		public double atan2(double y, double x)
		{
			return Math.atan2(y, x);
		}
		
		@Override
		public double random(double low, double high)
		{
			return low + Math.random() * (high - low);
		}
		
		@Override
		public int random_integer(int low, int high)
		{
			return low + rng.nextInt(high - low) + 1;
		}
		
		@Override
		public double ceil(double x)
		{
			return Math.ceil(x);
		}
		
		@Override
		public double floor(double x)
		{
			return Math.floor(x);
		}
		
		@Override
		public double ln(double x)
		{
			return Math.log(x);
		}
		
		@Override
		public double exp(double x)
		{
			return Math.exp(x);
		}
		
		@Override
		public double hermite_blend(double t)
		{
			return 3 * Math.pow(t, 2) - 2 * MATH.pow(t, 3);
		}
		
		@Override
		public double die_roll(int num, double low, double high)
		{
			double sum = 0;
			for(int i = 0; i < num; i++) sum += random(low, high);
			return sum;
		}
		
		@Override
		public int die_roll_integer(int num, int low, int high)
		{
			int sum = 0;
			for(int i = 0; i < num; i++) sum += random_integer(low, high);
			return sum;
		}
		
		@Override
		public double round(double x)
		{
			return Math.round(x);
		}
		
		@Override
		public int trunc(double value)
		{
			return (int) value;
		}
		
		@Override
		public double mod(double value, double denominator)
		{
			return value % denominator;
		}
		
		@Override
		public double lerp(double a, double b, double O_to_1)
		{
			return a + (b - a) * O_to_1;
		}
		
		@Override
		public double max(double a, double b)
		{
			return Math.max(a, b);
		}
		
		@Override
		public double min(double a, double b)
		{
			return Math.min(a, b);
		}
		
		@Override
		public double max(double a, double b, double... extraValues)
		{
			double max = Math.max(a, b);
			for(double value : extraValues) max = Math.max(value, max);
			return max;
		}
		
		@Override
		public double min(double a, double b, double... extraValues)
		{
			double max = Math.min(a, b);
			for(double value : extraValues) max = Math.min(value, max);
			return max;
		}
		
		@Override
		public double max(double b)
		{
			return b;
		}
		
		@Override
		public double min(double b)
		{
			return b;
		}
		
		@Override
		public double min_angle(double value)
		{
			return ((value + 180.0) % 360.0 + 360.0) % 360.0 - 180.0;
		}
		
		@Override
		public double lerprotate(double start, double end, double t)
		{
			// Normalize start and end angles to the range [0, 360)
			start = (start % 360 + 360) % 360;
			end = (end % 360 + 360) % 360;
			
			// Calculate the shortest angular distance between start and end
			double angleDifference = ((end - start + 180.0) % 360.0 + 360.0) % 360.0 - 180.0;
			
			// Calculate the interpolated angle based on t
			double interpolatedAngle = start + t * angleDifference;
			
			// Normalize the interpolated angle to the range [0, 360)
			interpolatedAngle = (interpolatedAngle % 360 + 360) % 360;
			
			return interpolatedAngle;
		}
	}
}