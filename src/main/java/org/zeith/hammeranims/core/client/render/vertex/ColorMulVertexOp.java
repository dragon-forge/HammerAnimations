package org.zeith.hammeranims.core.client.render.vertex;

import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammerlib.util.colors.ColorHelper;

public class ColorMulVertexOp
		implements IVertexOperator
{
	protected float r, g, b, a;
	
	public ColorMulVertexOp(float r, float g, float b, float a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public ColorMulVertexOp(int rgba)
	{
		this(ColorHelper.getRed(rgba), ColorHelper.getGreen(rgba), ColorHelper.getBlue(rgba), ColorHelper.getAlpha(rgba));
	}
	
	@Override
	public IVertexRenderer apply(IVertexRenderer renderer)
	{
		return (x, y, z, red, green, blue, alpha, u, v, packedOverlay, packedLight, nx, ny, nz, vType) ->
				renderer.vertex(x, y, z, red * r, green * g, blue * b, alpha * a, u, v, packedOverlay, packedLight, nx, ny, nz, vType);
	}
	
	@Override
	public String toString()
	{
		return "ColorMulVertexOp{" +
			   "r=" + r +
			   ", g=" + g +
			   ", b=" + b +
			   ", a=" + a +
			   '}';
	}
}