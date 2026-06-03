package org.zeith.hammeranims.core.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import org.zeith.hammeranims.core.client.render.vertex.*;

public class TessellatorVertexRenderer
		implements IVertexRenderer
{
	protected final Tessellator tess;
	protected final BufferBuilder buffer;
	protected final BufferBuilder translucencyBuffer;
	protected IVertexEmitter emitter;
	
	protected boolean hasTranslucency;
	
	public TessellatorVertexRenderer(Tessellator tess)
	{
		this.tess = tess;
		this.buffer = tess.getBuffer();
		this.translucencyBuffer = new BufferBuilder(32768);
	}
	
	@Override
	public void begin(int glMode, IVertexEmitter format)
	{
		buffer.begin(glMode, format.getFormat());
		this.emitter = format;
		hasTranslucency = false;
	}
	
	@Override
	public void vertex(VertexType type, RenderVertex... vertex)
	{
		boolean hasTranslucency = type == VertexType.TRANSLUCENT && vertex.length > 0;
		
		if(!this.hasTranslucency && hasTranslucency)
		{
			translucencyBuffer.begin(buffer.getDrawMode(), buffer.getVertexFormat());
			this.hasTranslucency = true;
		}
		
		BufferBuilder bb = hasTranslucency ? translucencyBuffer : buffer;
		for(RenderVertex v : vertex)
			emitter.emit(bb, v);
	}
	
	@Override
	public void upload()
	{
		tess.draw();
		
		if(!hasTranslucency)
			return;
		
		Entity rve = Minecraft.getMinecraft().getRenderViewEntity();
		if(rve != null)
		{
			float x = (float) rve.posX;
			float y = (float) (rve.posY + rve.getEyeHeight());
			float z = (float) rve.posZ;
			translucencyBuffer.sortVertexData(x, y, z);
		}
		BufferBuilder.State translucencyState = translucencyBuffer.getVertexState();
		int glMode = translucencyBuffer.getDrawMode();
		translucencyBuffer.finishDrawing();
		
		GlStateManager.enableBlend();
		buffer.begin(glMode, translucencyState.getVertexFormat());
		buffer.setVertexState(translucencyState);
		tess.draw();
	}
}