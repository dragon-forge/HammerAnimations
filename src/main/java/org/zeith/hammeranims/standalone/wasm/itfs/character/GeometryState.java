package org.zeith.hammeranims.standalone.wasm.itfs.character;

import shaded.joml.Vector3f;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSNumber;
import org.teavm.jso.impl.JS;
import org.teavm.jso.typedarrays.Float32Array;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.client.render.IVertexOutput;
import org.zeith.hammeranims.core.client.render.vertex.RenderVertex;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;
import org.zeith.hammeranims.standalone.hammeranims.CountingVertexOutput;

public class GeometryState
		implements HAGeoBuffers, IVertexOutput
{
	private RenderData state = new RenderData();
	private IGeometricModel model;
	
	private int vertexCount;
	
	private final int vertexSize = 3, uvSize = 2, normalSize = 3;
	private Float32Array vertices, uvs, normals;
	
	private Vector3f min, max;
	
	public GeometryState(IGeometryContainer geo)
	{
		model = geo.createModel();
		
		var out = new CountingVertexOutput();
		state.renderer = out;
		state.pose.reset();
		model.renderModel(state);
		state.renderer = this;
		this.vertexCount = out.getVertexCount();
		this.min = out.getMin();
		this.max = out.getMax();

		this.vertices = new Float32Array(vertexCount * vertexSize);
		this.uvs = new Float32Array(vertexCount * uvSize);
		this.normals = new Float32Array(vertexCount * normalSize);
	}
	
	@Override
	public Float32Array getVertices()
	{
		return vertices;
	}
	
	@Override
	public Float32Array getUvs()
	{
		return uvs;
	}
	
	@Override
	public Float32Array getNormals()
	{
		return normals;
	}
	
	@Override
	public JSArray<JSNumber> getMinBound()
	{
		return JS.wrap(new float[] { min.x, min.y, min.z });
	}
	
	@Override
	public JSArray<JSNumber> getMaxBound()
	{
		return JS.wrap(new float[] { max.x, max.y, max.z });
	}
	
	@Override
	public int getVertexCount()
	{
		return vertexCount;
	}
	
	@Override
	public void resetPose()
	{
		model.resetPose();
	}
	
	@Override
	public void updatePose(Object pose)
	{
		model.applyPose((GeometryPose) pose);
	}
	
	private int currentVertex;
	
	@Override
	public void flush()
	{
		currentVertex = 0;
		state.pose.reset();
		model.renderModel(state);
		currentVertex = 0;
	}
	
	@Override
	public void vertex(VertexType type, RenderVertex... vertex)
	{
		for(int i = 0; i < vertex.length; i++)
		{
			var t = vertex[i];
			int j = currentVertex + i;
			
			int v = j * vertexSize;
			vertices.set(v, t.x);
			vertices.set(v + 1, t.y);
			vertices.set(v + 2, t.z);
			
			int u = j * uvSize;
			uvs.set(u, t.u);
			uvs.set(u + 1, 1 - t.v);
			
			int n = j * normalSize;
			normals.set(n, t.nx);
			normals.set(n + 1, t.ny);
			normals.set(n + 2, t.nz);
		}
		currentVertex += vertex.length;
	}
}