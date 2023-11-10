package org.zeith.hammeranims.core.js;

import net.minecraft.util.Mth;
import org.openjdk.nashorn.api.scripting.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;

import javax.script.*;
import java.util.*;

public class ExpressionParser
{
	public static final MathJS MATH = new MathJS();
	
	public static InterpolatedDouble parse(String expression)
	{
		// Try parsing expression as contstant first.
		try
		{
			return InterpolatedDouble.constant(Double.parseDouble(expression));
		} catch(Throwable e)
		{
		}
		
		NashornScriptEngine js = (NashornScriptEngine) new NashornScriptEngineFactory().getScriptEngine();
		try
		{
			js.put("Java", null); // Prevent exploiting Java types.
			js.put("Math", MATH);
			js.put("math", MATH);
			
			String fun = "function get(query) {\n\tvar q = query;\n\treturn " + expression + ";\n}";
			
			var bindings = (ScriptObjectMirror) js.createBindings();
			js.eval(fun, bindings);
			var get = (ScriptObjectMirror) bindings.getMember("get");
			
			InterpolatedDouble id0 = get.to(InterpolatedDouble.class);
			
			return query ->
			{
				try
				{
					return id0.get(query);
				} catch(RuntimeException e)
				{
					e.printStackTrace();
					return Double.NaN;
				}
			};
		} catch(ScriptException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private static final Random rng = new Random();
	
	public static class MathJS
	{
		public final double pi = Math.PI;
		
		public double cos(double x)
		{
			return Mth.cos((float) (x * Mth.DEG_TO_RAD));
		}
		
		public double sin(double x)
		{
			return Mth.sin((float) (x * Mth.DEG_TO_RAD));
		}
		
		public double abs(double x)
		{
			return Math.abs(x);
		}
		
		public double clamp(double value, double min, double max)
		{
			return value < min ? min : value > max ? max : value;
		}
		
		public double pow(double base, double exponent)
		{
			return Math.pow(base, exponent);
		}
		
		public double sqrt(double x)
		{
			return Math.sqrt(x);
		}
		
		public double asin(double x)
		{
			return Math.asin(x);
		}
		
		public double acos(double x)
		{
			return Math.acos(x);
		}
		
		public double atan(double x)
		{
			return Math.atan(x);
		}
		
		public double atan2(double y, double x)
		{
			return Math.atan2(y, x);
		}
		
		public double random(double low, double high)
		{
			return low + Math.random() * (high - low);
		}
		
		public int random_integer(int low, int high)
		{
			return low + rng.nextInt(high - low) + 1;
		}
		
		public double ceil(double x)
		{
			return Math.ceil(x);
		}
		
		public double floor(double x)
		{
			return Math.floor(x);
		}
		
		public double ln(double x)
		{
			return Math.log(x);
		}
		
		public double exp(double x)
		{
			return Math.exp(x);
		}
		
		public double hermite_blend(double t)
		{
			return 3 * Math.pow(t, 2) - 2 * MATH.pow(t, 3);
		}
		
		public double die_roll(int num, double low, double high)
		{
			double sum = 0;
			for(int i = 0; i < num; i++) sum += random(low, high);
			return sum;
		}
		
		public int die_roll_integer(int num, int low, int high)
		{
			int sum = 0;
			for(int i = 0; i < num; i++) sum += random_integer(low, high);
			return sum;
		}
		
		public double round(double x)
		{
			return Math.round(x);
		}
		
		public int trunc(double value)
		{
			return (int) value;
		}
		
		public double mod(double value, double denominator)
		{
			return value % denominator;
		}
		
		public double lerp(double a, double b, double O_to_1)
		{
			return a + (b - a) * O_to_1;
		}
		
		public double max(double a, double b)
		{
			return Math.max(a, b);
		}
		
		public double min(double a, double b)
		{
			return Math.min(a, b);
		}
		
		public double min_angle(double value)
		{
			return ((value + 180.0) % 360.0 + 360.0) % 360.0 - 180.0;
		}
		
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