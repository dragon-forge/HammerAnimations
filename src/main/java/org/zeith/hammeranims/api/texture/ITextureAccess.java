package org.zeith.hammeranims.api.texture;

import org.zeith.hammeranims.api.geometry.model.IRenderableBone;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;
import org.zeith.hammerlib.util.colors.ColorHelper;

import java.util.Optional;

public interface ITextureAccess
		extends IPixelGetter
{
	ITextureAccess MISSING_TEXTURE = new ITextureAccess()
	{
		@Override
		public int getARGB(int x, int y)
		{
			return y < 16 / 2 ^ x < 16 / 2 ? 0xFFF800F8 : 0xFF000000;
		}
		
		@Override
		public int getWidth()
		{
			return 16;
		}
		
		@Override
		public int getHeight()
		{
			return 16;
		}
	};
	
	int getWidth();
	
	int getHeight();
	
	/**
	 * Returns an optional containing the render type of a given bone with a provided texture.
	 * <p>
	 * It may return an empty optional if every pixel of the UV space the bone uses is completely blank. (Alpha = 0)
	 */
	static Optional<VertexType> determineBoneType(IRenderableBone bone, ITextureAccess texture)
	{
		// Arrays are pointers and thus allow mutating their values from lambdas.
		final VertexType[] type = {VertexType.SOLID};
		final boolean[] nonTransparent = {false};
		final int[] visitedUvs = {0};
		
		PixelFaceVisitor visitor = new PixelFaceVisitor(texture, texture.getWidth(), texture.getHeight(), color ->
		{
			int a = (color >> 24) & 0xFF;
			if(a > 0) nonTransparent[0] = true;
			VertexType v = VertexType.ofAlpha(a);
			type[0] = v.max(type[0]);
			return v == VertexType.TRANSLUCENT;
		});
		
		bone.visitUVs(type, (vertexTypes, faceUV) ->
				{
					visitedUvs[0]++;
					visitor.visit(faceUV);
					return vertexTypes;
				},
				vertexTypes -> vertexTypes[0] == VertexType.TRANSLUCENT
		);
		
		if(visitedUvs[0] == 0 || (type[0] == VertexType.CUTOUT && !nonTransparent[0]))
			return Optional.empty();
		
		return Optional.of(type[0]);
	}
	
	static ITextureAccess from(final int width, final int height, final IPixelGetter getter)
	{
		return new ITextureAccess()
		{
			@Override
			public int getWidth()
			{
				return width;
			}
			
			@Override
			public int getHeight()
			{
				return height;
			}
			
			@Override
			public int getARGB(int x, int y)
			{
				return getter.getARGB(x, y);
			}
			
			@Override
			public String toString()
			{
				return "ITextureAccess{resolution=" + width + "x" + height + ",pixels=" + getter + "}";
			}
		};
	}
}