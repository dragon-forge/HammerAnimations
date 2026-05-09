package org.zeith.hammeranims.api.geometry.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;

@FunctionalInterface
public interface ISplitVertexConsumer
{
	VertexConsumer byVertexType(VertexType type);
	
	static ISplitVertexConsumer unary(VertexConsumer out)
	{
		return new ISplitVertexConsumer()
		{
			@Override
			public VertexConsumer byVertexType(VertexType type)
			{
				return out;
			}
			
			@Override
			public String toString()
			{
				return "ISplitVertexConsumer.unary(" + out + ")";
			}
		};
	}
	
	static ISplitVertexConsumer simple(VertexConsumer defaultOut, VertexConsumer opaque, VertexConsumer cutout, VertexConsumer translucent)
	{
		VertexConsumer[] byType = new VertexConsumer[] {
				defaultOut,
				opaque,
				cutout,
				translucent
		};
		return new ISplitVertexConsumer()
		{
			@Override
			public VertexConsumer byVertexType(VertexType type)
			{
				return byType[type.ordinal()];
			}
			
			@Override
			public String toString()
			{
				return "ISplitVertexConsumer.simple(" + defaultOut + ", " + opaque + ", " + cutout + ", " + translucent + ")";
			}
		};
	}
}