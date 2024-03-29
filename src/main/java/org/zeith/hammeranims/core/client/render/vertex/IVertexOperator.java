package org.zeith.hammeranims.core.client.render.vertex;

import org.zeith.hammeranims.core.client.render.IVertexRenderer;

@FunctionalInterface
public interface IVertexOperator
{
	IVertexRenderer apply(IVertexRenderer renderer);
}