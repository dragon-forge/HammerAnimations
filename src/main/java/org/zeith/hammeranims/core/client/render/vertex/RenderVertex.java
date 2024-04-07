package org.zeith.hammeranims.core.client.render.vertex;

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
}