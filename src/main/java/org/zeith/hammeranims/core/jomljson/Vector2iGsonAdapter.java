package org.zeith.hammeranims.core.jomljson;

import com.google.gson.*;
import org.zeith.hammeranims.joml.Vector2i;

import java.lang.reflect.Type;

public class Vector2iGsonAdapter
		implements JsonSerializer<Vector2i>, JsonDeserializer<Vector2i>
{
	@Override
	public Vector2i deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		JsonArray array = json.getAsJsonArray();
		if(array.size() != 2)
		{
			throw new JsonSyntaxException("Expected 2 elements in Vector2i array, found: " + array.size());
		}
		
		return new Vector2i(array.get(0).getAsInt(), array.get(1).getAsInt());
	}
	
	@Override
	public JsonElement serialize(Vector2i src, Type typeOfSrc, JsonSerializationContext context)
	{
		JsonArray array = new JsonArray();
		array.add(src.x);
		array.add(src.y);
		return array;
	}
}