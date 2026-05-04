package org.zeith.hammeranims.molang.runtime;

import org.zeith.hammeranims.molang.runtime.struct.QueryStruct;

import java.util.function.*;

public final class MoLangMath
{
	public static final double DEG_TO_RAD = (Math.PI / 180);
	public static final double RAD_TO_DEG = (180 / Math.PI);
	
	private static final float[] SIN = new float[65536];
	
	static
	{
		for(int i = 0; i < SIN.length; ++i)
			SIN[i] = (float) Math.sin(i * Math.PI * 2.0 / 65536.0);
	}
	
	public static QueryStruct init(QueryStruct lib)
	{
		register(lib::setFunction);
		return lib;
	}
	
	public static void register(BiConsumer<String, Function<MoParams, Object>> lib)
	{
		lib.accept("pi", params -> Math.PI);
		lib.accept("cos", params -> cos((float) (params.getDouble(0) * DEG_TO_RAD)));
		lib.accept("sin", params -> sin((float) (params.getDouble(0) * DEG_TO_RAD)));
		lib.accept("abs", params -> Math.abs(params.getDouble(0)));
		lib.accept("clamp", params -> Math.min(params.getDouble(1), Math.max(params.getDouble(0), params.getDouble(2))));
		lib.accept("pow", params -> Math.pow(params.getDouble(0), params.getDouble(1)));
		lib.accept("sqrt", params -> Math.sqrt(params.getDouble(0)));
		lib.accept("acos", params -> Math.acos(params.getDouble(0)) * RAD_TO_DEG);
		lib.accept("asin", params -> Math.asin(params.getDouble(0)) * RAD_TO_DEG);
		lib.accept("atan", params -> Math.atan(params.getDouble(0)) * RAD_TO_DEG);
		lib.accept("atan2", params -> Math.atan2(params.getDouble(0), params.getDouble(1)) * RAD_TO_DEG);
		lib.accept("random", params -> random(params.getDouble(0), params.getDouble(1)));
		lib.accept("random_integer", params -> randomInt(params.getInt(0), params.getInt(1)));
		lib.accept("ceil", params -> Math.ceil(params.getDouble(0)));
		lib.accept("floor", params -> Math.floor(params.getDouble(0)));
		lib.accept("ln", params -> Math.log(params.getDouble(0)));
		lib.accept("exp", params -> Math.exp(params.getDouble(0)));
		lib.accept("hermite_blend", params -> hermiteBlend(params.getInt(0)));
		lib.accept("die_roll", params -> dieRoll(params.getInt(0), params.getDouble(1), params.getDouble(2)));
		lib.accept("die_roll_integer", params -> dieRollInt(params.getInt(0), params.getInt(1), params.getInt(2)));
		lib.accept("round", params -> Math.round(params.getDouble(0)));
		lib.accept("trunc", params -> Math.floor(params.getDouble(0)));
		lib.accept("mod", params -> params.getDouble(0) % params.getDouble(1));
		lib.accept("lerp", params -> lerp(params.getDouble(0), params.getDouble(1), params.getDouble(2)));
		lib.accept("max", params -> Math.max(params.getDouble(0), params.getDouble(1)));
		lib.accept("min", params -> Math.min(params.getDouble(0), params.getDouble(1)));
		lib.accept("min_angle", params -> ((params.getDouble(0) + 180.0) % 360.0 + 360.0) % 360.0 - 180.0);
		lib.accept("lerp_rotate", params -> lerpRotate(params.getDouble(0), params.getDouble(1), params.getDouble(2)));
		lib.accept("lerprotate", params -> lerpRotate(params.getDouble(0), params.getDouble(1), params.getDouble(2)));
	}
	
	public static float sin(float pValue)
	{
		return SIN[(int) (pValue * 10430.378F) & '\uffff'];
	}
	
	public static float cos(float pValue)
	{
		return SIN[(int) (pValue * 10430.378F + 16384.0F) & '\uffff'];
	}
	
	public static double random(double low, double high)
	{
		return low + Math.random() * (high - low);
	}
	
	public static int randomInt(int low, int high)
	{
		return (int) Math.round((double) low + Math.random() * (double) (high - low));
	}
	
	public static double dieRoll(int num, double low, double high)
	{
		int total = 0;
		for(int i = 0; i++ < num; total += (int) random(low, high)) ;
		return total;
	}
	
	public static int dieRollInt(int num, int low, int high)
	{
		int total = 0;
		for(int i = 0; i++ < num; total += randomInt(low, high)) ;
		return total;
	}
	
	public static int hermiteBlend(int value)
	{
		return 3 * value ^ 2 - 2 * value ^ 3;
	}
	
	public static double lerp(double start, double end, double amount)
	{
		amount = Math.max(0.0F, Math.min(1.0F, amount));
		return start + (end - start) * amount;
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
}
