package org.zeith.hammeranims.core.impl.api.geometry;

import net.minecraft.world.phys.Vec3;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.geometry.event.DecodeGeometryEvent;
import org.zeith.hammeranims.core.client.model.CubeUVs;
import org.zeith.hammeranims.core.impl.api.animation.AnimationDecoder;
import org.zeith.hammeranims.core.utils.EnumFacing;
import org.zeith.hammerlib.util.shaded.json.*;

public class GeometryDecoder
{
	static
	{
		HammerAnimationsApi.EVENT_BUS.addListener(AnimationDecoder::decodeAnimation);
	}
	
	public static void init()
	{
	}
	
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
			
			GeometryDataImpl.BoneConfig cfg = new GeometryDataImpl.BoneConfig(boneId, boneObj.optString("parent"), new Vec3(
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
					Object uvRaw = cubeObj.opt("uv");
					
					CubeUVs uv = null;
					
					Vec3 sizev = new Vec3(size.getDouble(0), size.getDouble(1), size.getDouble(2));
					
					if(uvRaw instanceof JSONArray)
					{
						JSONArray simple = (JSONArray) uvRaw;
						uv = new CubeUVs(sizev, (float) simple.getDouble(0), (float) simple.getDouble(1));
					} else if(uvRaw instanceof JSONObject)
					{
						JSONObject faces = (JSONObject) uvRaw;
						uv = new CubeUVs();
						for(EnumFacing face : EnumFacing.values())
							if(faces.has(face.name))
							{
								JSONObject faceRaw = faces.getJSONObject(face.name);
								JSONArray uvs = faceRaw.getJSONArray("uv");
								JSONArray uvd = faceRaw.getJSONArray("uv_size");
								uv.uvMap.put(face, new float[] {
										(float) uvs.getDouble(0),
										(float) uvs.getDouble(1),
										(float) (uvs.getDouble(0) + uvd.getDouble(0)),
										(float) (uvs.getDouble(1) + uvd.getDouble(1))
								});
							}
					}
					
					cfg.cubes.add(new GeometryDataImpl.CubeConfig(
							new Vec3(origin.getDouble(0), origin.getDouble(1), origin.getDouble(2)),
							sizev,
							uv, (float) cubeObj.optDouble("inflate", 0),
							cubeObj.optBoolean("mirror", false)
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
