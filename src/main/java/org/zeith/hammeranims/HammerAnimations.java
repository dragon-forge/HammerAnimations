package org.zeith.hammeranims;

import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammeranims.standalone.wasm.Log;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class HammerAnimations
{
	public static final String VERSION = "1.0.56";
	
	public static final Log LOG = new Log();
	
	private static boolean initialized = false;
	
	public static void init()
	{
		if(initialized) return;
		long start = System.currentTimeMillis();
		initialized = true;
		
		HammerAnimations.loadGeo("null", """
			{
				"format_version": "1.12.0",
				"minecraft:geometry": [
					{
						"description": {
							"identifier": "geometry.null",
							"texture_width": 1,
							"texture_height": 1,
							"visible_bounds_width": 0,
							"visible_bounds_height": 0,
							"visible_bounds_offset": [0, 0, 0]
						},
						"bones": []
					}
				]
			}"""
		);
		
		LOG.info("Register {}", DefaultsHA.NULL_GEOMETRY.getRegistryKey());
		
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