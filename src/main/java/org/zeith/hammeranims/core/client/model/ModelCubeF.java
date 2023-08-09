package org.zeith.hammeranims.core.client.model;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.zeith.hammeranims.api.geometry.model.RenderData;

import java.util.List;

public class ModelCubeF
{
	
	/** An array of 6 TexturedQuads, one for each face of a cube */
	private final List<TexturedQuadF> quadList;
	
	/** X vertex coordinate of lower box corner */
	public final float posX1;
	
	/** Y vertex coordinate of lower box corner */
	public final float posY1;
	
	/** Z vertex coordinate of lower box corner */
	public final float posZ1;
	
	/** X vertex coordinate of upper box corner */
	public final float posX2;
	
	/** Y vertex coordinate of upper box corner */
	public final float posY2;
	
	/** Z vertex coordinate of upper box corner */
	public final float posZ2;
	
	public ModelCubeF(ModelBoneF r, CubeUVs uvs, float x, float y, float z, float dx, float dy, float dz, float delta, boolean flipX)
	{
		this.quadList = Lists.newArrayListWithCapacity(6);
		
		this.posX1 = x;
		this.posY1 = y;
		this.posZ1 = z;
		
		this.posX2 = x + dx;
		this.posY2 = y + dy;
		this.posZ2 = z + dz;
		
		float xMax = x + dx;
		float yMax = y + dy;
		float zMax = z + dz;
		
		x -= delta;
		y -= delta;
		z -= delta;
		
		xMax += delta;
		yMax += delta;
		zMax += delta;
		
		if(flipX)
		{
			float f3 = xMax;
			xMax = x;
			x = f3;
		}
		
		VertexF v1 = new VertexF(x, y, z, 0.0F, 0.0F);
		VertexF v2 = new VertexF(xMax, y, z, 0.0F, 8.0F);
		VertexF v3 = new VertexF(xMax, yMax, z, 8.0F, 8.0F);
		VertexF v4 = new VertexF(x, yMax, z, 8.0F, 0.0F);
		VertexF v5 = new VertexF(x, y, zMax, 0.0F, 0.0F);
		VertexF v6 = new VertexF(xMax, y, zMax, 0.0F, 8.0F);
		VertexF v7 = new VertexF(xMax, yMax, zMax, 8.0F, 8.0F);
		VertexF v8 = new VertexF(x, yMax, zMax, 8.0F, 0.0F);
		
		uvs.generateQuads(this.quadList::add, r.textureWidth, r.textureHeight,
				v1, v2, v3, v4, v5, v6, v7, v8
		);
		
		if(!flipX)
			for(TexturedQuadF q : this.quadList)
			{
				q.invertNormal = true;
				q.flipFace();
			}
	}
	
	public void render(RenderData data, VertexConsumer renderer)
	{
		for(TexturedQuadF q : this.quadList)
			q.render(data, renderer);
	}
}