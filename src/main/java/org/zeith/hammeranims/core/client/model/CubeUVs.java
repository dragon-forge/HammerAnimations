package org.zeith.hammeranims.core.client.model;

import net.minecraft.util.math.Vec3d;
import org.zeith.hammeranims.core.utils.EnumFacing;

import java.util.*;
import java.util.function.Consumer;

public class CubeUVs
{
	public final Map<EnumFacing, float[]> uvMap = new HashMap<>();
	
	public CubeUVs()
	{
	}
	
	public CubeUVs(Vec3d size, float texU, float texV)
	{
		float dx = (float) size.x, dy = (float) size.y, dz = (float) size.z;
		
		uvMap.put(EnumFacing.DOWN, new float[] {
				texU + dz + dx, texV + dz, texU + dz + dx + dx, texV,
		});
		
		uvMap.put(EnumFacing.UP, new float[] {
				texU + dz, texV, texU + dz + dx, texV + dz
		});
		
		uvMap.put(EnumFacing.NORTH, new float[] {
				texU + dz, texV + dz, texU + dz + dx, texV + dz + dy,
		});
		
		uvMap.put(EnumFacing.SOUTH, new float[] {
				texU + dz + dx + dz, texV + dz, texU + dz + dx + dz + dx, texV + dz + dy,
		});
		
		uvMap.put(EnumFacing.WEST, new float[] {
				texU, texV + dz, texU + dz, texV + dz + dy,
		});
		
		uvMap.put(EnumFacing.EAST, new float[] {
				texU + dz + dx, texV + dz, texU + dz + dx + dz, texV + dz + dy,
		});
	}
	
	public void generateQuads(Consumer<TexturedQuadF> quadConsumer,
							  float textureWidth, float textureHeight,
							  VertexF v1, VertexF v2, VertexF v3, VertexF v4,
							  VertexF v5, VertexF v6, VertexF v7, VertexF v8)
	{
		// Y- DOWN
		EnumFacing face = EnumFacing.DOWN;
		float[] uvs = uvMap.get(face);
		if(uvs != null && uvs.length == 4)
			quadConsumer.accept(new TexturedQuadF(new VertexF[] {v6, v5, v1, v2},
					uvs[0], uvs[1], uvs[2], uvs[3],
					textureWidth, textureHeight
			));
		
		// Y+ UP
		face = EnumFacing.UP;
		uvs = uvMap.get(face);
		if(uvs != null && uvs.length == 4)
			quadConsumer.accept(new TexturedQuadF(new VertexF[] {v3, v4, v8, v7},
					uvs[0], uvs[1], uvs[2], uvs[3],
					textureWidth, textureHeight
			));
		
		// Z- NORTH
		face = EnumFacing.NORTH;
		uvs = uvMap.get(face);
		if(uvs != null && uvs.length == 4)
			quadConsumer.accept(new TexturedQuadF(new VertexF[] {v2, v1, v4, v3},
					uvs[0], uvs[1], uvs[2], uvs[3],
					textureWidth, textureHeight
			));
		
		// Z+ SOUTH
		face = EnumFacing.SOUTH;
		uvs = uvMap.get(face);
		if(uvs != null && uvs.length == 4)
			quadConsumer.accept(new TexturedQuadF(new VertexF[] {v5, v6, v7, v8},
					uvs[0], uvs[1], uvs[2], uvs[3],
					textureWidth, textureHeight
			));
		
		// X+ EAST
		face = EnumFacing.EAST;
		uvs = uvMap.get(face);
		if(uvs != null && uvs.length == 4)
			quadConsumer.accept(new TexturedQuadF(new VertexF[] {v1, v5, v8, v4},
					uvs[0], uvs[1], uvs[2], uvs[3],
					textureWidth, textureHeight
			));
		
		// X- WEST
		face = EnumFacing.WEST;
		uvs = uvMap.get(face);
		if(uvs != null && uvs.length == 4)
			quadConsumer.accept(new TexturedQuadF(new VertexF[] {v6, v2, v3, v7},
					uvs[0], uvs[1], uvs[2], uvs[3],
					textureWidth, textureHeight
			));
	}
}