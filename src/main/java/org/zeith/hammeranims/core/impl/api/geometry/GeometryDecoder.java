package org.zeith.hammeranims.core.impl.api.geometry;

import com.zeitheron.hammercore.lib.zlib.json.*;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.geometry.event.DecodeGeometryEvent;
import org.zeith.hammeranims.core.impl.api.animation.AnimationDecoder;

public class GeometryDecoder
{
	static
	{
		HammerAnimationsApi.EVENT_BUS.register(AnimationDecoder.class);
	}
	
	public static void init()
	{
	}
	
	@SubscribeEvent
	public static void decodeGeometry(DecodeGeometryEvent e)
	{
		JSONArray arr = e.asArray().orElse(null);
		if(arr == null || arr.length() < 1) return;
		Object o = arr.get(0);
		if(!(o instanceof JSONObject)) return;
		JSONObject obj = (JSONObject) o;
		if(!obj.has("description") || !obj.has("bones")) return;
		JSONObject description = obj.getJSONObject("description");
		JSONArray bones = obj.getJSONArray("bones");
		
		GeometryDataImpl data = new GeometryDataImpl(e.container.getRegistryKey(), e.container);
		data.textureWidth = description.getInt("texture_width");
		data.textureHeight = description.getInt("texture_height");
		
		for(int i = 0; i < bones.length(); i++)
		{
			JSONObject boneObj = bones.getJSONObject(i);
			String boneId = boneObj.getString("name");
			JSONArray pivot = boneObj.getJSONArray("pivot");
			
			GeometryDataImpl.BoneConfig cfg = new GeometryDataImpl.BoneConfig(boneId, boneObj.optString("parent"), new Vec3d(
					pivot.getDouble(0),
					pivot.getDouble(1),
					pivot.getDouble(2)
			));
			
			JSONArray cubes = boneObj.optJSONArray("cubes");
			if(cubes != null)
				for(int j = 0; j < cubes.length(); j++)
				{
					JSONObject cubeObj = cubes.getJSONObject(j);
					
					JSONArray origin = cubeObj.getJSONArray("origin");
					JSONArray size = cubeObj.getJSONArray("size");
					JSONArray uv = cubeObj.getJSONArray("uv");
					
					cfg.cubes.add(new GeometryDataImpl.CubeConfig(
							new Vec3d(origin.getDouble(0), origin.getDouble(1), origin.getDouble(2)),
							new Vec3d(size.getDouble(0), size.getDouble(1), size.getDouble(2)),
							(float) uv.getDouble(0), (float) uv.getDouble(1),
							(float) cubeObj.optDouble("inflate", 0)
					));
				}
			
			data.bones.put(boneId, cfg);
		}
		
		for(GeometryDataImpl.BoneConfig bone : data.bones.values())
		{
			if(bone.parent == null || bone.parent.isEmpty()) continue;
			GeometryDataImpl.BoneConfig parent = data.bones.get(bone.parent);
			if(parent == null)
			{
				HammerAnimations.LOG.warn("[{}]: Found bone ({}) with unresolved parent ({})", e.container.getRegistryKey(), bone.name, bone.parent);
				bone.parent = null;
			}
		}
		
		e.setDecoded(data);
	}
}
