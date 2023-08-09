package org.zeith.hammeranims.core.client.model;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.*;

public class TexturedQuadF
{
	public VertexF[] vertexPositions;
	public int nVertices;
	public boolean invertNormal;
	
	public TexturedQuadF(VertexF[] vertices)
	{
		this.vertexPositions = vertices;
		this.nVertices = vertices.length;
	}
	
	public TexturedQuadF(VertexF[] vertices, float texcoordU1, float texcoordV1, float texcoordU2, float texcoordV2, float textureWidth, float textureHeight)
	{
		this(vertices);
		
//		texcoordU1 = (int) texcoordU1;
//		texcoordV1 = (int) texcoordV1;
//		texcoordU2 = (int) texcoordU2;
//		texcoordV2 = (int) texcoordV2;
		
		float f = 0.0F / textureWidth;
		float f1 = 0.0F / textureHeight;
		vertices[0] = vertices[0].setTexturePosition(texcoordU2 / textureWidth - f, texcoordV2 / textureHeight + f1);
		vertices[1] = vertices[1].setTexturePosition(texcoordU1 / textureWidth + f, texcoordV2 / textureHeight + f1);
		vertices[2] = vertices[2].setTexturePosition(texcoordU1 / textureWidth + f, texcoordV1 / textureHeight - f1);
		vertices[3] = vertices[3].setTexturePosition(texcoordU2 / textureWidth - f, texcoordV1 / textureHeight - f1);
	}
	
	public void flipFace()
	{
		VertexF[] apositiontexturevertices = new VertexF[this.vertexPositions.length];
		for(int i = 0; i < this.vertexPositions.length; ++i)
			apositiontexturevertices[i] = this.vertexPositions[this.vertexPositions.length - i - 1];
		this.vertexPositions = apositiontexturevertices;
	}
	
	/**
	 * Draw this primitive. This is typically called only once as the generated drawing instructions are saved by the
	 * renderer and reused later.
	 */
	@SideOnly(Side.CLIENT)
	public void bake(BufferBuilder renderer)
	{
		Vec3d vec3d = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[0].vector3D);
		Vec3d vec3d1 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[2].vector3D);
		Vec3d vec3d2 = vec3d1.crossProduct(vec3d).normalize();
		float f = (float) vec3d2.x;
		float f1 = (float) vec3d2.y;
		float f2 = (float) vec3d2.z;
		
		if(this.invertNormal)
		{
			f = -f;
			f1 = -f1;
			f2 = -f2;
		}
		
		renderer.begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
		
		for(int i = 0; i < 4; ++i)
		{
			VertexF v = this.vertexPositions[i];
			renderer.pos(v.vector3D.x, v.vector3D.y, v.vector3D.z)
					.tex(v.texturePositionX, v.texturePositionY)
					.normal(f, f1, f2)
					.endVertex();
		}
		
		Tessellator.getInstance().draw();
	}
}