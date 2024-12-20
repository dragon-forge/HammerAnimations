package org.zeith.hammeranims.standalone.hammeranims;

import lombok.Getter;
import org.zeith.hammeranims.core.client.render.IVertexOutput;
import org.zeith.hammeranims.core.client.render.vertex.RenderVertex;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;

import java.util.*;

@Getter
public class GatheringVertexOutput
		implements IVertexOutput
{
	private final Map<VertexType, List<RenderVertex>> vertices = new HashMap<>();
	
	@Override
	public void vertex(VertexType type, RenderVertex... vertex)
	{
		vertices.computeIfAbsent(type, t -> new ArrayList<>())
				.addAll(List.of(vertex));
	}
}