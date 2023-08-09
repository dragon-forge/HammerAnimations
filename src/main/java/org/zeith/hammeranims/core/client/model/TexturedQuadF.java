package org.zeith.hammeranims.core.client.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraftforge.api.distmarker.*;
import org.joml.*;
import org.zeith.hammeranims.api.geometry.model.RenderData;

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
	@OnlyIn(Dist.CLIENT)
	public void render(RenderData data, VertexConsumer renderer)
	{
		var vec3d = this.vertexPositions[1].subtractReverse(this.vertexPositions[0].vector3D);
		var vec3d1 = this.vertexPositions[1].subtractReverse(this.vertexPositions[2].vector3D);
		
		var normalVec = vec3d1.cross(vec3d).normalize();
		if(invertNormal) normalVec = normalVec.scale(-1);
		float f = (float) normalVec.x;
		float f1 = (float) normalVec.y;
		float f2 = (float) normalVec.z;
		
		var lp = data.pose.last();
		var pose = lp.pose();
		var norm = lp.normal();
		
		for(int i = 0; i < 4; ++i)
		{
			VertexF v = this.vertexPositions[i];
			var vp = v.vector3D;
			
			renderer.vertex(pose, (float) vp.x, (float) vp.y, (float) vp.z)
					.color(data.red, data.green, data.blue, data.alpha)
					.uv(v.texturePositionX, v.texturePositionY)
					.overlayCoords(data.overlay)
					.uv2(data.lighting)
					.normal(norm, f, f1, f2)
					.endVertex();
		}
	}
}