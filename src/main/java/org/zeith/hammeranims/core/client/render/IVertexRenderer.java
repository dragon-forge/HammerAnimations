package org.zeith.hammeranims.core.client.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.*;

public interface IVertexRenderer
{
	IVertexRenderer DUMMY = (x, y, z, red, green, blue, alpha, u, v, packedOverlay, packedLight, nx, ny, nz) -> {};
	
	void vertex(float x, float y, float z,// position
				float red, float green, float blue, float alpha, // color
				float u, float v, // tex
				int packedOverlay, int packedLight, //
				float nx, float ny, float nz // normal
	);
	
	@SideOnly(Side.CLIENT)
	static IVertexRenderer wrap(BufferBuilder bb)
	{
		return (x, y, z, red, green, blue, alpha, u, v, packedOverlay, packedLight, nx, ny, nz) ->
		{
			int k3 = packedLight >> 16 & 65535;
			int l3 = packedLight & 65535;
			
			bb
					.pos(x, y, z)
//					.normal(nx, ny, nz)
					.tex(u, v)
					.lightmap(k3, l3)
					.color(red, green, blue, alpha)
					.endVertex();
		};
	}
	
	@SideOnly(Side.CLIENT)
	static IVertexRenderer wrapWithNormals(BufferBuilder bb)
	{
		return (x, y, z, red, green, blue, alpha, u, v, packedOverlay, packedLight, nx, ny, nz) ->
		{
			int k3 = packedLight >> 16 & 65535;
			int l3 = packedLight & 65535;
			
			bb
					.pos(x, y, z)
					.normal(nx, ny, nz)
					.tex(u, v)
					.lightmap(k3, l3)
					.color(red, green, blue, alpha)
					.endVertex();
		};
	}
}