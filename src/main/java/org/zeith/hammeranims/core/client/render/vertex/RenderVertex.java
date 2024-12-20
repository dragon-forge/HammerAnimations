package org.zeith.hammeranims.core.client.render.vertex;

import lombok.ToString;
import shaded.joml.Vector3f;
import shaded.joml.Vector3fc;

@ToString
public class RenderVertex
{
	public float x, y, z;
	public float red, green, blue, alpha;
	public float u, v;
	public int packedOverlay, packedLight;
	public float nx, ny, nz;
	
	public RenderVertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int packedOverlay, int packedLight, float nx, float ny, float nz)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		this.u = u;
		this.v = v;
		this.packedOverlay = packedOverlay;
		this.packedLight = packedLight;
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
	}
	
	/**
	 * Triangulates a quad (array of 4 RenderVertex) into two triangles.
	 *
	 * @param quad
	 * 		An array of exactly 4 RenderVertex.
	 *
	 * @return An array of 6 RenderVertex forming two triangles.
	 *
	 * @throws IllegalArgumentException
	 * 		if the input array is not of length 4.
	 */
	public static RenderVertex[] triangulateQuad(RenderVertex[] quad)
	{
		if(quad.length != 4)
			throw new IllegalArgumentException("Input array must have exactly 4 vertices.");
		
		// Define two triangles: (0, 1, 2) and (0, 2, 3)
		return new RenderVertex[] {
				quad[0], quad[1], quad[2], // First triangle
				quad[0], quad[2], quad[3]  // Second triangle
		};
	}
	
	public Vector3fc pos()
	{
		return new Vector3f(x, y, z);
	}
}