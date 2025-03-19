package org.zeith.hammeranims.core.client.render;

import com.zeitheron.hammercore.client.utils.UtilsFX;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.zeith.hammeranims.api.geometry.model.RenderData;
import org.zeith.hammeranims.core.client.render.vertex.*;

import java.util.function.UnaryOperator;

public interface IVertexRenderer
		extends IVertexOutput
{
	IVertexRenderer DUMMY = new IVertexRenderer()
	{
		@Override
		public void bind(RenderData data)
		{
		}
		
		@Override
		public void begin(int glMode, VertexFormat format)
		{
		}
		
		@Override
		public void vertex(VertexType type, RenderVertex... vertex)
		{
		}
		
		@Override
		public void upload()
		{
		}
		
		@Override
		public String toString()
		{
			return "IVertexRenderer.DUMMY";
		}
	};
	
	void begin(int glMode, VertexFormat format);
	
	default void bind(RenderData data)
	{
		UtilsFX.bindTexture(data.texture);
	}
	
	void upload();
	
	default IVertexRenderer transform(UnaryOperator<RenderVertex> out)
	{
		IVertexRenderer deez = this;
		
		return new IVertexRenderer()
		{
			@Override
			public void bind(RenderData data)
			{
				deez.bind(data);
			}
			
			@Override
			public void begin(int glMode, VertexFormat format)
			{
				deez.begin(glMode, format);
			}
			
			@Override
			public void vertex(VertexType type, RenderVertex... vertex)
			{
				for(int i = 0; i < vertex.length; i++)
					vertex[i] = out.apply(vertex[i]);
				deez.vertex(type, vertex);
			}
			
			@Override
			public void upload()
			{
				deez.upload();
			}
		};
	}
	
	default IVertexRenderer apply(IVertexOperator op)
	{
		return op.apply(this);
	}
	
	default IVertexRenderer apply(IVertexOperator... ops)
	{
		IVertexRenderer r = this;
		for(IVertexOperator op : ops) r = op.apply(r);
		return r;
	}
}