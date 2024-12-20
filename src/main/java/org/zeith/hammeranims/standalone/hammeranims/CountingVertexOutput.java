package org.zeith.hammeranims.standalone.hammeranims;

import lombok.Getter;
import shaded.joml.Vector3f;
import org.zeith.hammeranims.core.client.render.IVertexOutput;
import org.zeith.hammeranims.core.client.render.vertex.RenderVertex;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;

@Getter
public class CountingVertexOutput
		implements IVertexOutput
{
	private Vector3f min = new Vector3f(), max = new Vector3f();
	
	private int vertexCount;
	
	@Override
	public void vertex(VertexType type, RenderVertex... vertex)
	{
		vertexCount += vertex.length;
		for(RenderVertex vt : vertex)
		{
			var p = vt.pos();
			min = min.min(p);
			max = max.max(p);
		}
	}
}