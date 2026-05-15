package org.zeith.hammeranims.api.texture;

import lombok.AllArgsConstructor;
import org.zeith.hammeranims.api.geometry.data.FaceUV;
import org.zeith.hammeranims.api.utils.IFaceUVPredicate;

import java.util.function.IntPredicate;

@AllArgsConstructor
public class PixelFacePredicate
		implements IFaceUVPredicate
{
	public static final IntPredicate FULL_OPACITY = argb -> ((argb >> 24) & 0xFF) == 0xFF;
	public static final IntPredicate ZERO_OPACITY = argb -> ((argb >> 24) & 0xFF) == 0;
	public static final IntPredicate PARTIAL_OPACITY = argb ->
	{
		int a = (argb >> 24) & 0xFF;
		return a > 0 && a < 0xFF;
	};
	
	public final IPixelGetter pixels;
	public final int width, height;
	public final IntPredicate colorMatcher;
	
	@Override
	public boolean test(int cube, int face, FaceUV uv)
	{
		float uMin = Math.min(uv.getU1(), uv.getU2());
		float vMin = Math.min(uv.getV1(), uv.getV2());
		
		float uMax = Math.max(uv.getU1(), uv.getU2());
		float vMax = Math.max(uv.getV1(), uv.getV2());
		
		int x1 = (int) Math.floor(uMin * width), x2 = (int) Math.ceil(uMax * width);
		int y1 = (int) Math.floor(vMin * height), y2 = (int) Math.ceil(vMax * height);
		
		for(int x = x1; x < x2; x++)
			for(int y = y1; y < y2; y++)
				if(colorMatcher.test(pixels.getARGB(x, y)))
					return true;
		
		return false;
	}
}