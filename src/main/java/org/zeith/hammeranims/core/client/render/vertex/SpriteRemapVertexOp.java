package org.zeith.hammeranims.core.client.render.vertex;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;

import java.util.function.Supplier;

public class SpriteRemapVertexOp
		implements IVertexOperator
{
	protected final Supplier<TextureAtlasSprite> sprite;
	
	public SpriteRemapVertexOp(Supplier<TextureAtlasSprite> sprite)
	{
		this.sprite = sprite;
	}
	
	@Override
	public IVertexRenderer apply(IVertexRenderer renderer)
	{
		TextureAtlasSprite s = sprite.get();
		return renderer.transform((v) ->
		{
			v.u = s.getInterpolatedU(v.u * 16F);
			v.v = s.getInterpolatedV(v.v * 16F);
			return v;
		});
	}
	
	@Override
	public String toString()
	{
		return "SpriteRemapVertexOp{" +
			   "sprite=" + sprite +
			   '}';
	}
}