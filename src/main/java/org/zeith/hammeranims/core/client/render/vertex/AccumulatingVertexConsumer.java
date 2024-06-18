package org.zeith.hammeranims.core.client.render.vertex;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AccumulatingVertexConsumer
		implements VertexConsumer
{
	protected final List<Consumer<VertexConsumer>> accumulator = new ArrayList<>(256);
	
	public void applyAndReset(VertexConsumer pipe)
	{
		accumulator.forEach(c -> c.accept(pipe));
		accumulator.clear();
	}
	
	protected VertexConsumer record(Consumer<VertexConsumer> action)
	{
		accumulator.addLast(action);
		return this;
	}
	
	@Override
	public VertexConsumer addVertex(float x, float y, float z)
	{
		return record(c -> c.addVertex(x, y, z));
	}
	
	@Override
	public VertexConsumer setColor(int r, int g, int b, int a)
	{
		return record(c -> c.setColor(r, g, b, a));
	}
	
	@Override
	public VertexConsumer setUv(float u, float v)
	{
		return record(c -> c.setUv(u, v));
	}
	
	@Override
	public VertexConsumer setUv1(int u, int v)
	{
		return record(c -> c.setUv1(u, v));
	}
	
	@Override
	public VertexConsumer setUv2(int u, int v)
	{
		return record(c -> c.setUv2(u, v));
	}
	
	@Override
	public VertexConsumer setNormal(float x, float y, float z)
	{
		return record(c -> c.setNormal(x, y, z));
	}
	
	@Override
	public void addVertex(float x, float y, float z, int color, float u, float v, int packedOverlay, int packedLight, float nx, float ny, float nz)
	{
		record(c -> c.addVertex(x, y, z, color, u, v, packedOverlay, packedLight, nx, ny, nz));
	}
	
	public static IntoSource register(List<IntoSource> registry, RenderType type)
	{
		var src = new IntoSource(type);
		registry.add(src);
		return src;
	}
	
	public static class IntoSource
			extends AccumulatingVertexConsumer
	{
		public final RenderType type;
		
		public IntoSource(RenderType type)
		{
			this.type = type;
		}
		
		public void applyAndReset(MultiBufferSource source)
		{
			applyAndReset(source.getBuffer(type));
		}
	}
}