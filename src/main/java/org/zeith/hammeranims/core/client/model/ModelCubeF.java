package org.zeith.hammeranims.core.client.model;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.*;

public class ModelCubeF
{
	
	/** An array of 6 TexturedQuads, one for each face of a cube */
	private final TexturedQuadF[] quadList;
	
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
	
	public ModelCubeF(ModelBoneF r, float texU, float texV, float x, float y, float z, float dx, float dy, float dz, float delta)
	{
		this(r, texU, texV, x, y, z, dx, dy, dz, delta, r.mirror);
	}
	
	public ModelCubeF(ModelBoneF r, float texU, float texV, float x, float y, float z, float dx, float dy, float dz, float delta, boolean mirror)
	{
		this.posX1 = x;
		this.posY1 = y;
		this.posZ1 = z;
		this.posX2 = x + dx;
		this.posY2 = y + dy;
		this.posZ2 = z + dz;
		this.quadList = new TexturedQuadF[6];
		float f = x + dx;
		float f1 = y + dy;
		float f2 = z + dz;
		x = x - delta;
		y = y - delta;
		z = z - delta;
		f = f + delta;
		f1 = f1 + delta;
		f2 = f2 + delta;
		
		if(mirror)
		{
			float f3 = f;
			f = x;
			x = f3;
		}
		
		VertexF v1 = new VertexF(x, y, z, 0.0F, 0.0F);
		VertexF v2 = new VertexF(f, y, z, 0.0F, 8.0F);
		VertexF v3 = new VertexF(f, f1, z, 8.0F, 8.0F);
		VertexF v4 = new VertexF(x, f1, z, 8.0F, 0.0F);
		VertexF v5 = new VertexF(x, y, f2, 0.0F, 0.0F);
		VertexF v6 = new VertexF(f, y, f2, 0.0F, 8.0F);
		VertexF v7 = new VertexF(f, f1, f2, 8.0F, 8.0F);
		VertexF v8 = new VertexF(x, f1, f2, 8.0F, 0.0F);
		
		this.quadList[0] = new TexturedQuadF(new VertexF[] {v6, v2, v3, v7},
				texU + dz + dx,
				texV + dz,
				texU + dz + dx + dz,
				texV + dz + dy,
				r.textureWidth, r.textureHeight
		);
		
		this.quadList[1] = new TexturedQuadF(new VertexF[] {v1, v5, v8, v4},
				texU,
				texV + dz,
				texU + dz,
				texV + dz + dy,
				r.textureWidth, r.textureHeight
		);
		
		this.quadList[2] = new TexturedQuadF(new VertexF[] {v6, v5, v1, v2},
				texU + dz,
				texV,
				texU + dz + dx,
				texV + dz,
				r.textureWidth, r.textureHeight
		);
		
		this.quadList[3] = new TexturedQuadF(new VertexF[] {v3, v4, v8, v7},
				texU + dz + dx,
				texV + dz,
				texU + dz + dx + dx,
				texV,
				r.textureWidth, r.textureHeight
		);
		
		this.quadList[4] = new TexturedQuadF(new VertexF[] {v2, v1, v4, v3},
				texU + dz,
				texV + dz,
				texU + dz + dx,
				texV + dz + dy,
				r.textureWidth, r.textureHeight
		);
		
		this.quadList[5] = new TexturedQuadF(new VertexF[] {v5, v6, v7, v8},
				texU + dz + dx + dz,
				texV + dz,
				texU + dz + dx + dz + dx,
				texV + dz + dy,
				r.textureWidth, r.textureHeight
		);
		
		if(mirror)
			for(TexturedQuadF q : this.quadList)
				q.flipFace();
	}
	
	@SideOnly(Side.CLIENT)
	public void bake(BufferBuilder renderer)
	{
		for(TexturedQuadF q : this.quadList)
			q.bake(renderer);
	}
}