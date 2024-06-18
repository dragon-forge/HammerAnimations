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
		accumulator.add(action);
		return this;
	}
	
	@Override
	public VertexConsumer vertex(double x, double y, double z)
	{
		return record(c -> c.vertex(x, y, z));
	}
	
	@Override
	public VertexConsumer color(int r, int g, int b, int a)
	{
		return record(c -> c.color(r, g, b, a));
	}
	
	@Override
	public VertexConsumer uv(float u, float v)
	{
		return record(c -> c.uv(u, v));
	}
	
	@Override
	public VertexConsumer overlayCoords(int u, int v)
	{
		return record(c -> c.overlayCoords(u, v));
	}
	
	@Override
	public VertexConsumer uv2(int u, int v)
	{
		return record(c -> c.uv2(u, v));
	}
	
	@Override
	public VertexConsumer normal(float x, float y, float z)
	{
		return record(c -> c.normal(x, y, z));
	}
	
	@Override
	public void endVertex()
	{
		record(VertexConsumer::endVertex);
	}
	
	@Override
	public void vertex(float x, float y, float z, float r, float g, float b, float a, float u, float v, int packedOverlay, int packedLight, float nx, float ny, float nz)
	{
		record(c -> c.vertex(x, y, z, r, g, b, a, u, v, packedOverlay, packedLight, nx, ny, nz));
	}
	
	@Override
	public void defaultColor(int r, int g, int b, int a)
	{
		record(c -> c.defaultColor(r, g, b, a));
	}
	
	@Override
	public void unsetDefaultColor()
	{
		record(VertexConsumer::endVertex);
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