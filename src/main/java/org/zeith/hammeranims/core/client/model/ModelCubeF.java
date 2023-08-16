package org.zeith.hammeranims.core.client.model;

import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammeranims.core.utils.*;
import org.zeith.hammeranims.joml.*;

import java.lang.Math;
import java.util.*;

public class ModelCubeF
{
	private final TexturedQuadF[] quads;
	
	private ModelCubeF(TexturedQuadF[] quads)
	{
		this.quads = quads;
	}
	
	public static ModelCubeF make(Vector3f origin, Vector3f size, CubeUVs uvResolver, float inflate, boolean mirror, int textureWidth, int textureHeight)
	{
		float width = size.x();
		float height = size.y();
		float depth = size.z();
		
		float x1 = origin.x();
		float y1 = origin.y();
		float z1 = origin.z();
		
		List<TexturedQuadF> quads = new ArrayList<>(6);
		
		float x2 = x1 + Math.max(width, 0.008F);
		float y2 = y1 + Math.max(height, 0.008F);
		float z2 = z1 + Math.max(depth, 0.008F);
		
		x2 += inflate;
		y2 += inflate;
		z2 += inflate;
		x1 -= inflate;
		y1 -= inflate;
		z1 -= inflate;
		
		Vector3f v1 = new Vector3f(x1, y1, z1);
		Vector3f v2 = new Vector3f(x2, y1, z1);
		Vector3f v3 = new Vector3f(x2, y2, z1);
		Vector3f v4 = new Vector3f(x1, y2, z1);
		Vector3f v5 = new Vector3f(x1, y1, z2);
		Vector3f v6 = new Vector3f(x1, y2, z2);
		Vector3f v7 = new Vector3f(x2, y2, z2);
		Vector3f v8 = new Vector3f(x2, y1, z2);
		
		if(width != 0 && height != 0)
		{
			quads.add(makeQuad(v3, v2, v1, v4, uvResolver, textureWidth, textureHeight, mirror, EnumFacing.NORTH));
			quads.add(makeQuad(v6, v5, v8, v7, uvResolver, textureWidth, textureHeight, mirror, EnumFacing.SOUTH));
		}
		
		if(depth != 0 && height != 0)
		{
			quads.add(makeQuad(v4, v1, v5, v6, uvResolver, textureWidth, textureHeight, mirror, mirror ? EnumFacing.EAST : EnumFacing.WEST));
			quads.add(makeQuad(v7, v8, v2, v3, uvResolver, textureWidth, textureHeight, mirror, mirror ? EnumFacing.WEST : EnumFacing.EAST));
		}
		
		if(width != 0 && depth != 0)
		{
			quads.add(makeQuad(v2, v8, v5, v1, uvResolver, textureWidth, textureHeight, mirror, EnumFacing.DOWN));
			quads.add(makeQuad(v7, v3, v4, v6, uvResolver, textureWidth, textureHeight, mirror, EnumFacing.UP));
		}
		
		return new ModelCubeF(quads.toArray(new TexturedQuadF[0]));
	}
	
	private static TexturedQuadF makeQuad(Vector3f pos1, Vector3f pos2, Vector3f pos3, Vector3f pos4, CubeUVs uvResolver, int textureWidth, int textureHeight, boolean mirror, EnumFacing direction)
	{
		CubeUVs.SizedUV uv = uvResolver.get(direction);
		
		float u1 = uv.u1() / (float) textureWidth;
		float u2 = uv.u2() / (float) textureWidth;
		float v1 = uv.v1() / (float) textureHeight;
		float v2 = uv.v2() / (float) textureHeight;
		
		if(mirror)
		{
			float temp = u1;
			u1 = u2;
			u2 = temp;
		}
		
		return new TexturedQuadF(new VertexF[] {
				makeVertex(pos1, u1, v1),
				makeVertex(pos2, u1, v2),
				makeVertex(pos3, u2, v2),
				makeVertex(pos4, u2, v1),
		}, mirror, direction);
	}
	
	private static VertexF makeVertex(Vector3f pos, float u, float v)
	{
		return new VertexF(pos, u, v);
	}
	
	public void render(IPoseEntry pose, IVertexRenderer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		Matrix4f po = pose.getPose();
		Matrix3f no = pose.getNormal();
		
		for(TexturedQuadF quad : quads)
		{
			Vector3f normal = no.transform(quad.normal, new Vector3f());
			
			for(VertexF vertex : quad.vertices)
			{
				float x = vertex.getPos().x() / 16.0F;
				float y = vertex.getPos().y() / 16.0F;
				float z = vertex.getPos().z() / 16.0F;
				Vector3f pos = new Vector3f(x, y, z);
				po.transformPosition(pos);
				
				vertexConsumer.vertex(
						pos.x(), pos.y(), pos.z(),
						red, green, blue, alpha,
						vertex.getU(), vertex.getV(),
						packedOverlay, packedLight,
						normal.x(), normal.y(), normal.z()
				);
			}
		}
	}
}