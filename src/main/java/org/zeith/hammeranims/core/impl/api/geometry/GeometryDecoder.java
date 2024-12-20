package org.zeith.hammeranims.core.impl.api.geometry;

import org.zeith.hammeranims.api.geometry.event.DecodeGeometryEvent;
import org.zeith.hammeranims.core.impl.api.geometry.decoder.GsonGeometryDecoder;
import shaded.tuples.Tuple2;

public class GeometryDecoder
{
	public static void decodeGeometry(DecodeGeometryEvent e)
	{
		try
		{
			for(Tuple2<?, GeometryDataImpl> tup : GsonGeometryDecoder.readGeometryFile(e.container, e.path, e.text))
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
