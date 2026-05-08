package dev.zeith.lzvm.jvm;

import dev.zeith.lzvm.op.LzVarOp;

public class LzMath
{
	public static final double DEG_TO_RAD = (Math.PI / 180);
	public static final double RAD_TO_DEG = (180 / Math.PI);
	public static final double EPS = 1.0E-8;
	private static final float[] SIN = new float[65536];
	
	static
	{
		for(int i = 0; i < SIN.length; ++i)
			SIN[i] = (float) Math.sin(i * Math.PI * 2.0 / 65536.0);
	}
	
	public static float sin(float pValue)
	{
		return SIN[(int) (pValue * 10430.378F) & 0xFFFF];
	}
	
	public static float cos(float pValue)
	{
		return SIN[(int) (pValue * 10430.378F + 16384F) & 0xFFFF];
	}
	
	public static double coerce(boolean value)
	{
		return value ? 1.0 : 0.0;
	}
	
	public static boolean isZero(double d)
	{
		return Math.abs(d) < EPS;
	}
	
	public static boolean isNotZero(double d)
	{
		return Math.abs(d) > EPS;
	}
	
	public static boolean isOne(double d)
	{
		return Math.abs(d - 1) < EPS;
	}
	
	// Methods invoked from LzVM -> JVM compiled bytecode
	
	public static double eqd(double left, double right)
	{
		return coerce(Math.abs(left - right) < EPS);
	}
	
	public static double neqd(double left, double right)
	{
		return coerce(Math.abs(left - right) > EPS);
	}
	
	public static double gtd(double left, double right)
	{
		return coerce(left > right);
	}
	
	public static double getd(double left, double right)
	{
		return coerce(left > right - EPS);
	}
	
	public static double ltd(double left, double right)
	{
		return coerce(left < right);
	}
	
	public static double letd(double left, double right)
	{
		return coerce(left <= right + EPS);
	}
	
	public static double coald(double left, double right)
	{
		return Math.abs(left) > EPS ? left : right;
	}
	
	public static double andd(double left, double right)
	{
		return LzMath.isZero(left) || LzMath.isZero(right) ? 0.0 : 1.0;
	}
	
	public static double ord(double left, double right)
	{
		return LzMath.isZero(left) && LzMath.isZero(right) ? 0.0 : 1.0;
	}
	
	public static double notd(double d)
	{
		return Math.abs(d) < EPS ? 1.0 : 0.0;
	}
	
	public static double sind(double pValue)
	{
		return SIN[(int) (pValue * 10430.378F) & 0xFFFF];
	}
	
	public static double cosd(double pValue)
	{
		return SIN[(int) (pValue * 10430.378F + 16384F) & 0xFFFF];
	}
	
	public static String concs(Object a, Object b)
	{
		return String.valueOf(a).concat(String.valueOf(b));
	}
	
	public static void set(double v, LzVarOp var)
	{
		var.set(v);
	}
	
	public static void set(double index, double v, LzVarOp var)
	{
		var.set(index, v);
	}
}