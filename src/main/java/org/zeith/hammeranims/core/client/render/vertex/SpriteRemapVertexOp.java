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
		return (x, y, z, red, green, blue, alpha, u, v, packedOverlay, packedLight, nx, ny, nz) ->
				renderer.vertex(x, y, z, red, green, blue, alpha, s.getU(u * 16F), s.getV(v * 16F), packedOverlay, packedLight, nx, ny, nz);
	}
}