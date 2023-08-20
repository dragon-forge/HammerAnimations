package org.zeith.hammeranims.core.jomljson;

import com.google.gson.*;
import com.zeitheron.hammercore.utils.Chars;
import org.zeith.hammeranims.joml.Vector3d;

import java.lang.reflect.Type;

import static java.lang.Double.*;

public class Vector3dGsonAdapter
		implements JsonSerializer<Vector3d>, JsonDeserializer<Vector3d>
{
	public static final char INFINITY = '\u221E';
	public static final String POS_INF = "+" + INFINITY;
	public static final String NEG_INF = "-" + INFINITY;
	
	@Override
	public Vector3d deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		JsonArray array = json.getAsJsonArray();
		
		if(array.size() != 3)
			throw new JsonSyntaxException("Expected 3 elements in Vector3d array, found: " + array.size());
		
		JsonElement xE = array.get(0);
		JsonElement yE = array.get(1);
		JsonElement zE = array.get(2);
		
		double x;
		if(xE.getAsString().equals(POS_INF)) x = POSITIVE_INFINITY;
		else if(xE.getAsString().equals(NEG_INF)) x = NEGATIVE_INFINITY;
		else x = xE.getAsDouble();
		
		double y;
		if(yE.getAsString().equals(POS_INF)) y = POSITIVE_INFINITY;
		else if(yE.getAsString().equals(NEG_INF)) y = NEGATIVE_INFINITY;
		else y = yE.getAsDouble();
		
		double z;
		if(zE.getAsString().equals(POS_INF)) z = POSITIVE_INFINITY;
		else if(zE.getAsString().equals(NEG_INF)) z = NEGATIVE_INFINITY;
		else z = zE.getAsDouble();
		
		return new Vector3d(x, y, z);
	}
	
	@Override
	public JsonElement serialize(Vector3d src, Type typeOfSrc, JsonSerializationContext context)
	{
		JsonArray array = new JsonArray();
		
		if(src.x == NEGATIVE_INFINITY) array.add(NEG_INF);
		else if(src.x == POSITIVE_INFINITY) array.add(POS_INF);
		else array.add(src.x);
		
		if(src.y == NEGATIVE_INFINITY) array.add(NEG_INF);
		else if(src.y == POSITIVE_INFINITY) array.add(POS_INF);
		else array.add(src.y);
		
		if(src.z == NEGATIVE_INFINITY) array.add(NEG_INF);
		else if(src.z == POSITIVE_INFINITY) array.add(POS_INF);
		else array.add(src.z);
		
		return array;
	}
}