package org.zeith.hammeranims.core.client.render.vertex;

import com.zeitheron.hammercore.utils.color.ColorHelper;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;

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
		return renderer.transform((vt) ->
		{
			vt.red *= r;
			vt.green *= g;
			vt.blue *= b;
			vt.alpha *= a;
			return vt;
		});
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