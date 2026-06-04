package org.zeith.hammeranims.core.jomljson;

import org.json.JSONObject;
import org.zeith.hammeranims.core.impl.api.geometry.constrains.BoneConstraintsImpl;
import org.zeith.hammeranims.core.impl.api.geometry.constrains.GeometryConstrainsImpl;
import org.zeith.hammeranims.core.utils.GsonHelper;

import java.util.HashMap;
import java.util.*;

public class GeometryConstrainsImplAdapter
{
	public static GeometryConstrainsImpl parse(JSONObject object)
	{
		Map<String, BoneConstraintsImpl> builder = new HashMap<>();
		for(var e : object.keySet())
			builder.put(e, BoneConstrainsImplAdapter.parse(GsonHelper.getAsJsonObject(object, e)));
		return new GeometryConstrainsImpl(Map.copyOf(builder));
		JsonObject object = json.getAsJsonObject();
		ImmutableMap.Builder<String, BoneConstraintsImpl> builder = ImmutableMap.builder();
		for(Map.Entry<String, JsonElement> e : object.entrySet())
			builder.put(e.getKey().toLowerCase(Locale.ROOT), context.deserialize(e.getValue(), BoneConstraintsImpl.class));
		return new GeometryConstrainsImpl(builder.build());
	}
	
	@Override
	public JsonElement serialize(GeometryConstrainsImpl src, Type typeOfSrc, JsonSerializationContext context)
	{
		JsonObject object = new JsonObject();
		for(Map.Entry<String, BoneConstraintsImpl> e : src.bones().entrySet())
			object.add(e.getKey(), context.serialize(e.getValue(), BoneConstraintsImpl.class));
		return object;
	}
}