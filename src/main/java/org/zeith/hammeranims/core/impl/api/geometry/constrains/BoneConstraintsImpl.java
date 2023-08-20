package org.zeith.hammeranims.core.impl.api.geometry.constrains;

import org.zeith.hammeranims.api.geometry.constrains.IBoneConstraints;
import org.zeith.hammeranims.joml.Vector3d;

import static java.lang.Double.*;

public class BoneConstraintsImpl
		implements IBoneConstraints
{
	public boolean hasTranslation, hasRotation, hasScale;
	
	public Vector3d minTranslation = new Vector3d(NEGATIVE_INFINITY), maxTranslation = new Vector3d(POSITIVE_INFINITY);
	public Vector3d minRotation = new Vector3d(NEGATIVE_INFINITY), maxRotation = new Vector3d(POSITIVE_INFINITY);
	public Vector3d minScale = new Vector3d(NEGATIVE_INFINITY), maxScale = new Vector3d(POSITIVE_INFINITY);
	
	@Override
	public boolean hasTranslation()
	{
		return hasTranslation;
	}
	
	@Override
	public boolean hasRotation()
	{
		return hasRotation;
	}
	
	@Override
	public boolean hasScale()
	{
		return hasScale;
	}
	
	@Override
	public double getMinTranslationX()
	{
		return minTranslation.x;
	}
	
	@Override
	public double getMaxTranslationX()
	{
		return maxTranslation.x;
	}
	
	@Override
	public double getMinTranslationY()
	{
		return minTranslation.y;
	}
	
	@Override
	public double getMaxTranslationY()
	{
		return maxTranslation.y;
	}
	
	@Override
	public double getMinTranslationZ()
	{
		return minTranslation.z;
	}
	
	@Override
	public double getMaxTranslationZ()
	{
		return maxTranslation.z;
	}
	
	@Override
	public double getMinRotationX()
	{
		return minRotation.x;
	}
	
	@Override
	public double getMaxRotationX()
	{
		return maxRotation.x;
	}
	
	@Override
	public double getMinRotationY()
	{
		return minRotation.y;
	}
	
	@Override
	public double getMaxRotationY()
	{
		return maxRotation.y;
	}
	
	@Override
	public double getMinRotationZ()
	{
		return minRotation.z;
	}
	
	@Override
	public double getMaxRotationZ()
	{
		return maxRotation.z;
	}
	
	@Override
	public double getMinScaleX()
	{
		return minScale.x;
	}
	
	@Override
	public double getMaxScaleX()
	{
		return maxScale.x;
	}
	
	@Override
	public double getMinScaleY()
	{
		return minScale.y;
	}
	
	@Override
	public double getMaxScaleY()
	{
		return maxScale.y;
	}
	
	@Override
	public double getMinScaleZ()
	{
		return minScale.z;
	}
	
	@Override
	public double getMaxScaleZ()
	{
		return maxScale.z;
	}
}
