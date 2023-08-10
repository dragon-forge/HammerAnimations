package org.zeith.hammeranims.core.client.model;

import org.lwjgl.util.vector.Quaternion;

public final class QuaternionHelper
{
	public static Quaternion create(float pX, float pY, float pZ, boolean pDegrees)
	{
		if(pDegrees)
		{
			pX *= ((float) Math.PI / 180F);
			pY *= ((float) Math.PI / 180F);
			pZ *= ((float) Math.PI / 180F);
		}
		
		float f = sin(0.5F * pX);
		float f1 = cos(0.5F * pX);
		float f2 = sin(0.5F * pY);
		float f3 = cos(0.5F * pY);
		float f4 = sin(0.5F * pZ);
		float f5 = cos(0.5F * pZ);
		
		float i = f * f3 * f5 + f1 * f2 * f4;
		float j = f1 * f2 * f5 - f * f3 * f4;
		float k = f * f2 * f5 + f1 * f3 * f4;
		float r = f1 * f3 * f5 - f * f2 * f4;
		
		return new Quaternion(i, j, k, r);
	}
	
	private static float cos(float pAngle)
	{
		return (float) Math.cos(pAngle);
	}
	
	private static float sin(float pAngle)
	{
		return (float) Math.sin(pAngle);
	}
}