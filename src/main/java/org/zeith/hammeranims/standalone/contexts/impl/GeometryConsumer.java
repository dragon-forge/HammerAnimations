package org.zeith.hammeranims.standalone.contexts.impl;

import lombok.Getter;
import org.zeith.hammeranims.core.client.render.vertex.RenderVertex;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammeranims.standalone.contexts.ContextToken;

import java.util.*;

@Getter
public class GeometryConsumer
{
	public static final ContextToken<GeometryConsumer> GEOMETRY_DUMP = new ContextToken<>(GeometryConsumer.class, InstanceHelpers.tryParseLocation("geodump"));
	
	private final Map<String, List<RenderVertex>> vertices = new LinkedHashMap<>();
	
	public void dump(String tx, List<RenderVertex> vertices)
	{
		this.vertices.computeIfAbsent(tx, t -> new ArrayList<>()).addAll(vertices);
	}
	
	public void reset()
	{
		vertices.clear();
	}
}