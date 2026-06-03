package org.zeith.hammeranims.core.client.render;

import net.minecraft.client.renderer.vertex.VertexFormat;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.*;

public class VertexFormatsHA
{
	public static final VertexFormat FULL = new VertexFormat();
	
	static
	{
		FULL.addElement(POSITION_3F);
		FULL.addElement(NORMAL_3B);
		FULL.addElement(TEX_2F);
		FULL.addElement(TEX_2S);
		FULL.addElement(PADDING_1B);
		FULL.addElement(COLOR_4UB);
	}
}