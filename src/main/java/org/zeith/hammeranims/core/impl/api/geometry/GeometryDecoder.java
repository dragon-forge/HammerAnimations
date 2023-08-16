package org.zeith.hammeranims.core.impl.api.geometry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.geometry.event.DecodeGeometryEvent;
import org.zeith.hammeranims.core.impl.api.geometry.decoder.GsonGeometryDecoder;
import org.zeith.hammerlib.util.java.tuples.Tuple2;

public class GeometryDecoder
{
	static
	{
		HammerAnimationsApi.EVENT_BUS.register(GeometryDecoder.class);
	}
	
	public static void init()
	{
	}
	
	@SubscribeEvent
	public static void decodeGeometry(DecodeGeometryEvent e)
	{
		try
		{
			for(var tup : GsonGeometryDecoder.readGeometryFile(e.container, e.path, e.text))
			{
				e.setDecoded(tup.b());
				return;
			}
		} catch(Throwable err)
		{
			err.printStackTrace();
		}
	}
}
