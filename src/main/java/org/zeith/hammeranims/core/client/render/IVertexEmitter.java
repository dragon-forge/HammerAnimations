package org.zeith.hammeranims.core.client.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.*;
import org.zeith.hammeranims.core.client.render.vertex.RenderVertex;

public interface IVertexEmitter
{
	IVertexEmitter POSITION_TEX_LMAP_COLOR = new IVertexEmitter()
	{
		@Override
		public VertexFormat getFormat()
		{
			return DefaultVertexFormats.POSITION_TEX_LMAP_COLOR;
		}
		
		@Override
		public void emit(BufferBuilder buffer, RenderVertex vertex)
		{
			int packedLight = vertex.packedLight;
			int k3 = packedLight >> 16 & 65535;
			int l3 = packedLight & 65535;
			buffer.pos(vertex.x, vertex.y, vertex.z)
			      .tex(vertex.u, vertex.v)
			      .lightmap(k3, l3)
			      .color(vertex.red, vertex.green, vertex.blue, vertex.alpha)
			      .endVertex();
		}
	};
	
	IVertexEmitter FULL = new IVertexEmitter()
	{
		@Override
		public VertexFormat getFormat()
		{
			return VertexFormatsHA.FULL;
		}
		
		@Override
		public void emit(BufferBuilder buffer, RenderVertex vertex)
		{
			int packedLight = vertex.packedLight;
			int k3 = packedLight >> 16 & 65535;
			int l3 = packedLight & 65535;
			buffer.pos(vertex.x, vertex.y, vertex.z)
			      .normal(vertex.nx, vertex.ny, vertex.nz)
			      .tex(vertex.u, vertex.v)
			      .lightmap(k3, l3)
			      .color(vertex.red, vertex.green, vertex.blue, vertex.alpha)
			      .endVertex();
		}
	};
	
	VertexFormat getFormat();
	
	void emit(BufferBuilder buffer, RenderVertex vertex);
}