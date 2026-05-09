package org.zeith.hammeranims.api.texture;

import lombok.AllArgsConstructor;
import org.zeith.hammeranims.api.geometry.data.FaceUV;

import java.util.function.IntPredicate;

@AllArgsConstructor
public class PixelFaceVisitor
{
	public final IPixelGetter pixels;
	public final int width, height;
	public final IntPredicate colorMatcher;
	
	public void visit(FaceUV uv)
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
					return;
	}
}