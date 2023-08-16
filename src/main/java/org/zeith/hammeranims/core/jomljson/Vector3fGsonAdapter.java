package org.zeith.hammeranims.core.jomljson;

import com.google.gson.*;
import org.joml.*;

import java.lang.reflect.Type;

public class Vector3fGsonAdapter
		implements JsonSerializer<Vector3f>, JsonDeserializer<Vector3f>
{
	@Override
	public Vector3f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		JsonArray array = json.getAsJsonArray();
		if(array.size() != 3)
			throw new JsonSyntaxException("Expected 3 elements in Vector3f array, found: " + array.size());
		
		return new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
	}
	
	@Override
	public JsonElement serialize(Vector3f src, Type typeOfSrc, JsonSerializationContext context)
	{
		JsonArray array = new JsonArray();
		array.add(src.x);
		array.add(src.y);
		array.add(src.z);
		return array;
	}
}