package org.zeith.hammeranims.core.client.render.vertex;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;

import java.util.Objects;
import java.util.function.Supplier;

public class SpriteRemapVertexOp
		implements IVertexOperator
{
	protected final Supplier<TextureAtlasSprite> sprite;
	
	public SpriteRemapVertexOp(Supplier<TextureAtlasSprite> sprite)
	{
		this.sprite = sprite;
	}
	
	public SpriteRemapVertexOp(TextureMap atlas, ResourceLocation sprite)
	{
		this(atlas, Objects.toString(sprite));
	}
	
	public SpriteRemapVertexOp(TextureMap atlas, String sprite)
	{
		this(() -> atlas.getAtlasSprite(sprite));
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