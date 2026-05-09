package org.zeith.hammeranims.api.texture;

import org.zeith.hammeranims.api.geometry.model.IRenderableBone;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;
import org.zeith.hammerlib.util.colors.ColorHelper;

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
	
	static VertexType determineBoneType(IRenderableBone bone, ITextureAccess texture)
	{
		VertexType[] type = {VertexType.SOLID};
		
		PixelFaceVisitor visitor = new PixelFaceVisitor(texture, texture.getWidth(), texture.getHeight(), color ->
		{
			VertexType v = VertexType.ofAlpha(ColorHelper.getAlphai(color));
			type[0] = v.max(type[0]);
			return v == VertexType.TRANSLUCENT;
		}
		);
		
		bone.visitUVs(type, (vertexTypes, faceUV) ->
				{
					visitor.visit(faceUV);
					return vertexTypes;
				}, vertexTypes -> vertexTypes[0] == VertexType.TRANSLUCENT
		);
		
		return type[0];
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