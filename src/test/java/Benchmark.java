import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.op.*;
import net.minecraft.util.Mth;
import org.openjdk.nashorn.api.scripting.*;
import org.zeith.hammeranims.api.animation.interp.*;

import javax.script.ScriptException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Benchmark
{
	public static final MathJS MATH = new MathJS();
	
	public static void main(String[] args)
	{
		final String expression = "math.sin(45 + q.anim_time * 90) * 16 - math.cos(90 + q.anim_time * 90) * 16";
		final QueryObject q = new QueryObject();
		
		IExpression js = compileJS(expression, q);
		IExpression lz = compileLz(expression, q);
		
		long maxRunCount = 100_000_000;
		
		System.out.println();
		benchmark("JS", js, maxRunCount, q);
		System.out.println();
		benchmark("LZ", lz, maxRunCount, q);
		System.out.println();
	}
	
	public static void benchmark(String name, IExpression expr, long maxRunCount, QueryObject q)
	{
		long runCount = 0L;
		
		long minTime = Long.MAX_VALUE;
		long maxTime = Long.MIN_VALUE;
		
		long curCpuTimeNs = 0L;
		while(runCount < maxRunCount)
		{
			long start = System.nanoTime();
			expr.get();
			long time = System.nanoTime() - start;
			curCpuTimeNs += time;
			runCount++;
			minTime = Math.min(minTime, time);
			maxTime = Math.max(maxTime, time);
			q.anim_time = runCount / 1000D;
		}
		Duration testTime = Duration.of(curCpuTimeNs, ChronoUnit.NANOS);
		
		String pref = "[" + name + "]: ";
		System.out.println(pref + "Executed " + runCount + " in " + testTime);
		System.out.println(pref + "Average: " + (curCpuTimeNs * 1.0 / runCount) + " ns/run");
		System.out.println(pref + "Minimum run time: " + minTime + " ns");
		System.out.println(pref + "Maximum run time: " + maxTime + " ns");
	}
	
	public static IExpression compileLz(String expr, QueryObject q)
	{
		LzJvmCompiler jvm = new LzJvmCompiler();
		MoLangCompiler compiler = new MoLangCompiler();
		return compiler.parseFactory(jvm, expr, new LzJVM.LzClassLoader()).instantiate(q)::get;
	}
	
	public static IExpression compileJS(String expr, QueryObject q)
	{
		NashornScriptEngine js = (NashornScriptEngine) new NashornScriptEngineFactory().getScriptEngine();
		try
		{
			js.put("Java", null); // Prevent exploiting Java types.
			js.put("Math", MATH);
			js.put("math", MATH);
			
			String fun = "function get() {return " + expr + ";\n}";
			
			var bindings = (ScriptObjectMirror) js.createBindings();
			js.eval(fun, bindings);
			var get = (ScriptObjectMirror) bindings.getMember("get");
			
			IExpression id0 = get.to(IExpression.class);
			
			return () ->
			{
				try
				{
					js.put("query", q);
					js.put("q", q);
					return id0.get();
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
	
	public interface IExpression
	{
		double get();
	}
	
	public static class QueryObject
			implements LzVariableStore
	{
		protected final Map<String, LzVarOp> vars = new HashMap<>();
		
		public double anim_time;
		
		{
			vars.put("query.anim_time", LzVarOp.readOnly(() -> anim_time));
		}
		
		@Override
		public LzCallOp findCall(String name, String descriptor)
		{
			return LzCallOp.NO_OP;
		}
		
		@Override
		public LzVarOp findVar(String name)
		{
			return vars.computeIfAbsent(name, l -> new ReadWriteVariable());
		}
		
		@Override
		public LzVarOp tempVar(String name)
		{
			return LzVarOp.tempVar();
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