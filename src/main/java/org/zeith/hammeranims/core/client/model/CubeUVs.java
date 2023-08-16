package org.zeith.hammeranims.core.client.model;

import org.zeith.hammeranims.core.impl.api.geometry.decoder.UVDefinition;
import org.zeith.hammeranims.core.utils.EnumFacing;
import org.joml.*;

import java.lang.Math;
import java.util.Map;

public interface CubeUVs
{
	SizedUV get(EnumFacing direction);
	
	class BoxUVResolver
			implements CubeUVs
	{
		private final Vector2i uv;
		private final Vector3f size;
		
		public BoxUVResolver(Vector2i uv, Vector3f size)
		{
			this.uv = uv;
			this.size = size;
		}
		
		@Override
		public SizedUV get(EnumFacing direction)
		{
			switch(direction)
			{
				case EAST:
					return SizedUV.fromFloats(uv.x(),
							uv.y() + depth(), uv.x() + depth(), uv.y() + depth() + height()
					);
				case WEST:
					return SizedUV.fromFloats(
							uv.x() + depth() + width(),
							uv.y() + depth(), uv.x() + depth() + width() + depth(), uv.y() + depth() + height()
					);
				case DOWN:
					return SizedUV.fromFloats(
							uv.x() + depth() + width(), uv.y() + depth(), uv.x() + depth() + width() + width(), uv.y());
				case UP:
					return SizedUV.fromFloats(
							uv.x() + depth(), uv.y(), uv.x() + depth() + width(), uv.y() + depth());
				case NORTH:
					return SizedUV.fromFloats(
							uv.x() + depth(),
							uv.y() + depth(), uv.x() + depth() + width(), uv.y() + depth() + height()
					);
				case SOUTH:
					return SizedUV.fromFloats(
							uv.x() + depth() + width() + depth(),
							uv.y() + depth(),
							uv.x() + depth() + width() + depth() + width(), uv.y() + depth() + height()
					);
				default:
					throw new IllegalStateException("Wtf?");
			}
		}
		
		public int width()
		{
			return (int) size.x();
		}
		
		public int height()
		{
			return (int) size.y();
		}
		
		public int depth()
		{
			return (int) size.z();
		}
	}
	
	class FacedUVResolver
			implements CubeUVs
	{
		private final Map<EnumFacing, UVDefinition.FaceUVDefinition> mappings;
		
		public FacedUVResolver(Map<EnumFacing, UVDefinition.FaceUVDefinition> mappings)
		{
			this.mappings = mappings;
		}
		
		@Override
		public SizedUV get(EnumFacing direction)
		{
			UVDefinition.FaceUVDefinition uvDefinition = mappings.get(direction);
			if(uvDefinition == null) throw new IllegalStateException("???");
			
			return new SizedUV(uvDefinition.uv().x(), uvDefinition.uv().y(),
					uvDefinition.uv().x() + uvDefinition.size().x(), uvDefinition.uv().y() + uvDefinition.size().y()
			);
		}
	}
	
	class SizedUV
	{
		private final int u1;
		private final int v1;
		private final int u2;
		private final int v2;
		
		public SizedUV(int u1, int v1, int u2, int v2)
		{
			this.u1 = u1;
			this.v1 = v1;
			this.u2 = u2;
			this.v2 = v2;
		}
		
		public int u1()
		{
			return u1;
		}
		
		public int v1()
		{
			return v1;
		}
		
		public int u2()
		{
			return u2;
		}
		
		public int v2()
		{
			return v2;
		}
		
		public static SizedUV fromFloats(float u1, float v1, float u2, float v2)
		{
			return new SizedUV((int) Math.ceil(u1), (int) Math.ceil(v1), (int) Math.ceil(u2), (int) Math.ceil(v2));
		}
	}
}