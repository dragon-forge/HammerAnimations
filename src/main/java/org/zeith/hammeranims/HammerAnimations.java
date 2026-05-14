package org.zeith.hammeranims;

import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammeranims.standalone.wasm.Log;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class HammerAnimations
{
	public static final String VERSION = "v47";
	
	public static final Log LOG = new Log();
	
	private static boolean initialized = false;
	
	public static void init()
	{
		if(initialized) return;
		long start = System.currentTimeMillis();
		initialized = true;
		LOG.info("HammerAnimations {} initialized in {} ms.", VERSION, System.currentTimeMillis() - start);
	}
	
	public static IGeometryContainer loadGeo(String id, String json)
	{
		IGeometryContainer ctr = IGeometryContainer.create();
		ctr.setRegistryKey(InstanceHelpers.tryParseLocation(id));
		ctr.reload(path ->
		{
			if(path.getPath().endsWith(".constraints.json"))
				return Optional.empty();
			return Optional.of(json.getBytes(StandardCharsets.UTF_8));
		});
		return ctr;
	}
	
	public static IAnimationContainer loadAnim(String id, String json)
	{
		IAnimationContainer ctr = IAnimationContainer.create();
		ctr.setRegistryKey(InstanceHelpers.tryParseLocation(id));
		ctr.reload(path -> Optional.of(json.getBytes(StandardCharsets.UTF_8)));
		return ctr;
	}
}