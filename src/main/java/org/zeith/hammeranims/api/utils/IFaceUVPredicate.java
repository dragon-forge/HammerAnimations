package org.zeith.hammeranims.api.utils;

import org.zeith.hammeranims.api.geometry.data.FaceUV;

public interface IFaceUVPredicate
{
	boolean test(int cube, int face, FaceUV uv);
	
	default IFaceUVPredicate negate()
	{
		IFaceUVPredicate origin = this;
		return new IFaceUVPredicate()
		{
			@Override
			public boolean test(int cube, int face, FaceUV uv)
			{
				return !origin.test(cube, face, uv);
			}
			
			@Override
			public IFaceUVPredicate negate()
			{
				return origin;
			}
		};
	}
}