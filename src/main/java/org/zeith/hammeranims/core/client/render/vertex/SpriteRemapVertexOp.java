package org.zeith.hammeranims.core.client.render.vertex;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;

import java.util.function.Function;
import java.util.function.Supplier;

public class SpriteRemapVertexOp
		implements IVertexOperator
{
	protected final Supplier<TextureAtlasSprite> sprite;
	
	public SpriteRemapVertexOp(Supplier<TextureAtlasSprite> sprite)
	{
		this.sprite = sprite;
	}
	
	public SpriteRemapVertexOp(ResourceLocation atlas, ResourceLocation sprite)
	{
		this(Minecraft.getInstance().getTextureAtlas(atlas), sprite);
	}
	
	public SpriteRemapVertexOp(Function<ResourceLocation, TextureAtlasSprite> atlas, ResourceLocation sprite)
	{
		this(() -> atlas.apply(sprite));
	}
	
	@Override
	public IVertexRenderer apply(IVertexRenderer renderer)
	{
		TextureAtlasSprite s = sprite.get();
		return (x, y, z, red, green, blue, alpha, u, v, packedOverlay, packedLight, nx, ny, nz, vType) ->
				renderer.vertex(x, y, z, red, green, blue, alpha, s.getU(u * 16F), s.getV(v * 16F), packedOverlay, packedLight, nx, ny, nz, vType);
	}
	
	@Override
	public String toString()
	{
		return "SpriteRemapVertexOp{" +
			   "sprite=" + (sprite != null ? sprite.get() : null) +
			   '}';
	}
}