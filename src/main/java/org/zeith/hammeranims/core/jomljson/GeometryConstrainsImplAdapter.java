package org.zeith.hammeranims.core.jomljson;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import org.zeith.hammeranims.core.impl.api.geometry.constrains.*;

import java.lang.reflect.Type;
import java.util.Map;

public class GeometryConstrainsImplAdapter
		implements JsonDeserializer<GeometryConstrainsImpl>, JsonSerializer<GeometryConstrainsImpl>
{
	@Override
	public GeometryConstrainsImpl deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		JsonObject object = json.getAsJsonObject();
		ImmutableMap.Builder<String, BoneConstraintsImpl> builder = ImmutableMap.builder();
		for(Map.Entry<String, JsonElement> e : object.entrySet())
			builder.put(e.getKey(), context.deserialize(e.getValue(), BoneConstraintsImpl.class));
		return new GeometryConstrainsImpl(builder.build());
	}
	
	@Override
	public JsonElement serialize(GeometryConstrainsImpl src, Type typeOfSrc, JsonSerializationContext context)
	{
		JsonObject object = new JsonObject();
		for(Map.Entry<String, BoneConstraintsImpl> e : src.getBones().entrySet())
			object.add(e.getKey(), context.serialize(e.getValue(), BoneConstraintsImpl.class));
		return object;
	}
}
