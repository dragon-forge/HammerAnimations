package dev.zeith.lzvm.molang.compiler.libs;

import dev.zeith.lzvm.exception.LzVMException;
import dev.zeith.lzvm.jvm.LzMath;
import dev.zeith.lzvm.molang.compiler.*;
import dev.zeith.lzvm.molang.expression.*;
import dev.zeith.lzvm.program.LzCallInsn;

import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.Collectors;

import static dev.zeith.lzvm.jvm.LzMath.*;
import static dev.zeith.lzvm.molang.compiler.MoLangCompiler.*;
import static dev.zeith.lzvm.molang.compiler.libs.ICompilerLibrary.*;
import static dev.zeith.lzvm.op.LzOpcodes.*;
import static dev.zeith.lzvm.program.ArgType.DOUBLE;
import static dev.zeith.lzvm.program.LzCallInsn.ofDbl;

public enum MoMathLibrary
		implements ICompilerLibrary
{
	INSTANCE;
	
	public final Map<LzCallInsn, IMoFunctionCallTransformer> callTransformers;
	public final Set<String> requiredClasses;
	public final Map<String, Function<NameExpression, MLExpression>> nameTransformers;
	
	MoMathLibrary()
	{
		Map<LzCallInsn, IMoFunctionCallTransformer> c = new HashMap<>();
		Map<String, Function<NameExpression, MLExpression>> nt = new HashMap<>();
		Set<String> reqCls = new HashSet<>();
		
		reqCls.add(MoLangEasing.class.getName());
		reqCls.add(Math.class.getName());
		reqCls.add(MoMathLibrary.class.getName());
		
		final String JMath = "java/lang/Math";
		final String JMoMath = getClass().getName().replace('.', '/');
		
		// Constants
		nt.put("pi", e -> new NumberExpression(Math.PI));
		nt.put("e", e -> new NumberExpression(Math.E));
		
		// Standard Java Math functions:
		c.put(dUnaryOperator("sin"), argsAndExtraPure(duOpt(val -> LzMath.sind(val * DEG_TO_RAD)), b -> b.addConstD(DEG_TO_RAD).addInsn(MUL).addInsn(FSIN)));
		c.put(dUnaryOperator("cos"), argsAndExtraPure(duOpt(val -> LzMath.cosd(val * DEG_TO_RAD)), b -> b.addConstD(DEG_TO_RAD).addInsn(MUL).addInsn(FCOS)));
		c.put(dBinaryOperator("mod"), argsAndExtraPure(dbOpt((left, right) -> left % right), b -> b.addInsn(MOD)));
		c.put(dUnaryOperator("acos"), argsAndExtraPure(duOpt(val -> Math.acos(val) * RAD_TO_DEG), b -> b.addJCall(JMath, dUnaryOperator("acos")).addConstD(RAD_TO_DEG).addInsn(MUL)));
		c.put(dUnaryOperator("asin"), argsAndExtraPure(duOpt(val -> Math.asin(val) * RAD_TO_DEG), b -> b.addJCall(JMath, dUnaryOperator("asin")).addConstD(RAD_TO_DEG).addInsn(MUL)));
		c.put(dUnaryOperator("atan"), argsAndExtraPure(duOpt(val -> Math.atan(val) * RAD_TO_DEG), b -> b.addJCall(JMath, dUnaryOperator("atan")).addConstD(RAD_TO_DEG).addInsn(MUL)));
		c.put(dBinaryOperator("atan2"), argsAndExtraPure(dbOpt((a, b) -> Math.atan2(a, b) * RAD_TO_DEG), b -> b.addJCall(JMath, dBinaryOperator("atan2")).addConstD(RAD_TO_DEG).addInsn(MUL)));
		c.put(dUnaryOperator("abs"), argsAndExtraPure(duOpt(Math::abs), b -> b.addJCall(JMath, dUnaryOperator("abs"))));
		c.put(dUnaryOperator("sqrt"), argsAndExtraPure(duOpt(Math::sqrt), b -> b.addJCall(JMath, dUnaryOperator("sqrt"))));
		c.put(dUnaryOperator("ceil"), argsAndExtraPure(duOpt(Math::ceil), b -> b.addJCall(JMath, dUnaryOperator("ceil"))));
		c.put(dUnaryOperator("ln"), argsAndExtraPure(duOpt(Math::log), b -> b.addJCall(JMath, dUnaryOperator("log"))));
		c.put(dUnaryOperator("exp"), argsAndExtraPure(duOpt(Math::exp), b -> b.addJCall(JMath, dUnaryOperator("exp"))));
		c.put(dUnaryOperator("floor"), argsAndExtraPure(duOpt(Math::floor), b -> b.addJCall(JMath, dUnaryOperator("floor"))));
		c.put(dBinaryOperator("max"), argsAndExtraPure(dbOpt(Math::max), b -> b.addInsn(MAX)));
		c.put(dBinaryOperator("min"), argsAndExtraPure(dbOpt(Math::min), b -> b.addInsn(MIN)));
		c.put(dBinaryOperator("copy_sign"), argsAndExtraPure(dbOpt(Math::copySign), b -> b.addJCall(JMath, dBinaryOperator("copySign"))));
		
		// MoMath functions
		c.put(dUnaryOperator("sign"), argsAndExtraPure(duOpt(MoMathLibrary::signum), b -> b.addJCall(JMoMath, dUnaryOperator("signum"))));
		c.put(dUnaryOperator("min_angle"), argsAndExtraPure(duOpt(MoMathLibrary::minAngle), b -> b.addJCall(JMoMath, dUnaryOperator("minAngle"))));
		c.put(dUnaryOperator("hermite_blend"), argsAndExtraPure(duOpt(MoMathLibrary::hermiteBlend), b -> b.addJCall(JMoMath, dUnaryOperator("hermiteBlend"))));
		c.put(dTernaryOperator("lerp"), argsAndExtraPure(dtOpt(MoMathLibrary::lerp), b -> b.addJCall(JMoMath, dTernaryOperator("lerp"))));
		c.put(dTernaryOperator("inverse_lerp"), argsAndExtraPure(dtOpt(MoMathLibrary::inverseLerp), b -> b.addJCall(JMoMath, dTernaryOperator("inverseLerp"))));
		c.put(dTernaryOperator("clamp"), argsAndExtraPure(dtOpt(MoMathLibrary::clamp), b -> b.addJCall(JMoMath, dTernaryOperator("clamp"))));
		c.put(dTernaryOperator("lerprotate"), argsAndExtraPure(dtOpt(MoMathLibrary::lerpRotate), b -> b.addJCall(JMoMath, dTernaryOperator("lerpRotate"))));
		c.put(dUnaryOperator("trunc"), argsAndExtraPure(duOpt(MoMathLibrary::trunc), b -> b.addJCall(JMoMath, dUnaryOperator("trunc"))));
		c.put(dUnaryOperator("round"), argsAndExtraPure(duOpt(MoMathLibrary::round), b -> b.addJCall(JMoMath, dUnaryOperator("round"))));
		
		// Random functions may never be optimized
		c.put(dBinaryOperator("random"), argsAndExtra(false, null, b -> b.addJCall(JMoMath, dBinaryOperator("random"))));
		c.put(dBinaryOperator("random_integer"), argsAndExtra(false, null, b -> b.addJCall(JMoMath, dBinaryOperator("randomInt"))));
		c.put(dTernaryOperator("die_roll"), argsAndExtra(false, null, b -> b.addJCall(JMoMath, dTernaryOperator("dieRoll"))));
		c.put(dTernaryOperator("die_roll_integer"), argsAndExtra(false, null, b -> b.addJCall(JMoMath, dTernaryOperator("dieRollInt"))));
		
		c.put(dBinaryOperator("pow"),
				argsAndExtraPure(
						call ->
						{
							OptionalDouble opt1 = call.getChildren()[0].asOptimizedDouble();
							OptionalDouble opt2 = call.getChildren()[1].asOptimizedDouble();
							if(opt1.isPresent() && opt2.isPresent()) return new NumberExpression(Math.pow(opt1.getAsDouble(), opt2.getAsDouble()));
							else if(opt2.isPresent() && LzMath.isOne(opt2.getAsDouble()))
								return call.getChildren()[0];
							return null;
						},
						b -> b.addJCall(JMath, ofDbl("pow", DOUBLE, DOUBLE))
				)
		);
		
		MoLangEasing.bind(c);
		
		Map<LzCallInsn, IMoFunctionCallTransformer> c2 = new HashMap<>();
		Map<String, Function<NameExpression, MLExpression>> nt2 = new HashMap<>();
		for(Map.Entry<LzCallInsn, IMoFunctionCallTransformer> e : c.entrySet()) c2.put(e.getKey().renamed("math." + e.getKey().getName()), e.getValue());
		for(Map.Entry<String, Function<NameExpression, MLExpression>> e : nt.entrySet()) nt2.put("math." + e.getKey(), e.getValue());
		this.callTransformers = Collections.unmodifiableMap(c2);
		this.nameTransformers = Collections.unmodifiableMap(nt2);
		this.requiredClasses = Collections.unmodifiableSet(reqCls);
		
		// https://bedrock.dev/docs/stable/Molang#Math%20Functions
//		ensureTransformersExist(
//				"`math.abs(value)`	Absolute value of value",
//				"`math.acos(value)`	arccos of value",
//				"`math.asin(value)`	arcsin of value",
//				"`math.atan(value)`	arctan of value",
//				"`math.atan2(y, x)`	arctan of y/x. NOTE: the order of arguments!",
//				"`math.ceil(value)`	Round value up to nearest integral number",
//				"`math.clamp(value, min, max)`	Clamp value to between min and max inclusive",
//				"`math.copy_sign(A, B)`	Returns A with the sign of B",
//				"`math.cos(value)`	Cosine (in degrees) of value",
//				"`math.die_roll(num, low, high)`	returns the sum of 'num' random numbers, each with a value from low to high`. Note: the generated random numbers are not integers like normal dice. For that, use `math.die_roll_integer`.",
//				"`math.die_roll_integer(num, low, high)`	returns the sum of 'num' random integer numbers, each with a value from low to high`. Note: the generated random numbers are integers like normal dice.",
//				"`math.ease_in_back(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, overshooting backward before accelerating into the end",
//				"`math.ease_in_bounce(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting with bounce oscillations and settling into the end",
//				"`math.ease_in_circ(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting slow and accelerating along a circular curve toward the end",
//				"`math.ease_in_cubic(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting slow and accelerating rapidly toward the end",
//				"`math.ease_in_elastic(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting with elastic oscillations before accelerating into the end",
//				"`math.ease_in_expo(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting slow and accelerating extremely rapidly toward the end",
//				"`math.ease_in_out_back(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, overshooting at both start and end, with smoother change in the middle",
//				"`math.ease_in_out_bounce(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting and ending with bounce oscillations, smoother in the middle",
//				"`math.ease_in_out_circ(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting and ending slow, with circular acceleration and deceleration in the middle",
//				"`math.ease_in_out_cubic(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting slow, accelerating rapidly in the middle, then slowing again at the end",
//				"`math.ease_in_out_elastic(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, oscillating elastically at both start and end, with stable change in the middle",
//				"`math.ease_in_out_expo(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting and ending slow, with extremely rapid change in the middle",
//				"`math.ease_in_out_quad(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting slow, accelerating in the middle, then slowing again at the end",
//				"`math.ease_in_out_quart(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting slow, accelerating very rapidly in the middle, then slowing again at the end",
//				"`math.ease_in_out_quint(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting slow, accelerating extremely rapidly in the middle, then slowing again at the end",
//				"`math.ease_in_out_sine(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting and ending slow, with smoother change in the middle",
//				"`math.ease_in_quad(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting slow and accelerating toward the end",
//				"`math.ease_in_quart(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting slow and accelerating very rapidly toward the end",
//				"`math.ease_in_quint(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting slow and accelerating extremely rapidly toward the end",
//				"`math.ease_in_sine(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting slow and accelerating smoothly toward the end",
//				"`math.ease_out_back(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, overshooting past the end before settling into it",
//				"`math.ease_out_bounce(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, approaching the end with bounce oscillations that diminish over time",
//				"`math.ease_out_circ(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting fast and decelerating along a circular curve toward the end",
//				"`math.ease_out_cubic(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting fast and decelerating rapidly toward the end",
//				"`math.ease_out_elastic(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, overshooting the end with elastic oscillations before settling",
//				"`math.ease_out_expo(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting extremely fast and decelerating gradually toward the end",
//				"`math.ease_out_quad(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting fast and decelerating toward the end",
//				"`math.ease_out_quart(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting fast and decelerating very rapidly toward the end",
//				"`math.ease_out_quint(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting fast and decelerating extremely rapidly toward the end",
//				"`math.ease_out_sine(start, end, 0_to_1)`	Output goes from start to end via 0_to_1, starting fast and decelerating smoothly toward the end",
//				"`math.exp(value)`	Calculates e to the value'th power",
//				"`math.floor(value)`	Round value down to nearest integral number",
//				"`math.hermite_blend(value)`	Useful for simple smooth curve interpolation using one of the Hermite Basis functions: `3t^2 - 2t^3`. Note that while any valid float is a valid input, this function works best in the range [0,1].",
//				"`math.inverse_lerp(start, end, value)`	Returns the normalized progress between start and end given value",
//				"`math.lerp(start, end, 0_to_1)`	Lerp from start to end via 0_to_1",
//				"`math.lerprotate(start, end, 0_to_1)`	Lerp the shortest direction around a circle from start degrees to end degrees via 0_to_1",
//				"`math.ln(value)`	Natural logarithm of value",
//				"`math.max(A, B)`	Return highest value of A or B",
//				"`math.min(A, B)`	Return lowest value of A or B",
//				"`math.min_angle(value)`	Minimize angle magnitude (in degrees) into the range [-180, 180)",
//				"`math.mod(value, denominator)`	Return the remainder of value / denominator",
//				"`math.pi`	Returns the float representation of the constant pi.",
//				"`math.pow(base, exponent)`	Elevates `base` to the `exponent`'th power",
//				"`math.random(low, high)`	Random value between low and high inclusive",
//				"`math.random_integer(low, high)`	Random integer value between low and high inclusive",
//				"`math.round(value)`	Round value to nearest integral number",
//				"`math.sign(value)`	Returns 1 if value is positive, -1 otherwise",
//				"`math.sin(value)`	Sine (in degrees) of value",
//				"`math.sqrt(value)`	Square root of value",
//				"`math.trunc(value)`	Round value towards zero"
//		);
	}
	
	@Override
	public void register(MoLangCompiler c)
	{
		nameTransformers.forEach(c::registerName);
		callTransformers.forEach(c::registerTransformer);
		requiredClasses.forEach(c::registerRequiredClass);
	}
	
	private void ensureTransformersExist(String... names)
	{
		final Pattern LINE_MAPPER = Pattern.compile("^`?(?<call>[\\w.]+)`?([(\\sx)]+.+)$");
		HashSet<String> missing = Arrays.stream(names).map(s ->
		{
			Matcher m = LINE_MAPPER.matcher(s);
			if(m.find()) return m.group("call");
			throw new RuntimeException("Unable to find transformer for '" + s + "'");
		}).collect(Collectors.toCollection(HashSet::new));
		missing.removeAll(nameTransformers.keySet());
		callTransformers.keySet().stream().map(LzCallInsn::getName).collect(Collectors.toList()).forEach(missing::remove);
		if(missing.isEmpty()) return;
		throw new LzVMException("MoMath is missing functions: " + String.join(", ", missing));
	}
	
	private Function<FuncCallExpression, MLExpression> duOpt(DoubleUnaryOperator operator)
	{
		return call ->
		{
			OptionalDouble opt = call.getChildren()[0].asOptimizedDouble();
			if(opt.isPresent()) return new NumberExpression(operator.applyAsDouble(opt.getAsDouble()));
			return null;
		};
	}
	
	private Function<FuncCallExpression, MLExpression> dbOpt(DoubleBinaryOperator operator)
	{
		return call ->
		{
			OptionalDouble opt1 = call.getChildren()[0].asOptimizedDouble();
			OptionalDouble opt2 = call.getChildren()[1].asOptimizedDouble();
			if(opt1.isPresent() && opt2.isPresent()) return new NumberExpression(operator.applyAsDouble(opt1.getAsDouble(), opt2.getAsDouble()));
			return null;
		};
	}
	
	private Function<FuncCallExpression, MLExpression> dtOpt(DoubleTernaryOperator operator)
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
	
	@FunctionalInterface
	public interface DoubleTernaryOperator
	{
		double applyAsDouble(double a, double b, double c);
	}
	
	public static double signum(double t)
	{
		return t > 0 ? 1 : -1;
	}
	
	public static double hermiteBlend(double t)
	{
		return 3 * t * t - 2 * t * t * t;
	}
	
	public static double minAngle(double angle)
	{
		return ((angle + 180.0) % 360.0 + 360.0) % 360.0 - 180.0;
	}
	
	public static double random(double low, double high)
	{
		return low + Math.random() * (high - low);
	}
	
	public static double randomInt(double low, double high)
	{
		return Math.round((long) low + Math.random() * (long) (high - low));
	}
	
	public static double inverseLerp(double start, double end, double value)
	{
		if(start == end) return 0.0;
		return (value - start) / (end - start);
	}
	
	public static double lerp(double start, double end, double amount)
	{
		amount = Math.max(0.0F, Math.min(1.0F, amount));
		return start + (end - start) * amount;
	}
	
	public static double clamp(double x, double min, double max)
	{
		return Math.max(min, Math.min(max, x));
	}
	
	public static double lerpRotate(double start, double end, double amount)
	{
		start = radify(start);
		end = radify(end);
		
		if(start > end)
		{
			double tmp = start;
			start = end;
			end = tmp;
		}
		
		return end - start > 180.0 ? radify(end + amount * (360.0 - (end - start))) : start + amount * (end - start);
	}
	
	public static double radify(double num)
	{
		return ((num + 180.0) % 360.0 + 180.0) % 360.0;
	}
	
	public static double dieRoll(double num, double low, double high)
	{
		int total = 0;
		for(int i = 0; i++ < num; total += (int) random(low, high)) ;
		return total;
	}
	
	public static double dieRollInt(double num, double low, double high)
	{
		double total = 0;
		for(int i = 0; i++ < num; total += randomInt(low, high)) ;
		return total;
	}
	
	public static double trunc(double x)
	{
		return (int) x;
	}
	
	public static double round(double x)
	{
		return Math.round(x);
	}
}