package org.zeith.hammeranims.core.jomljson;

import org.joml.Vector2i;
import org.json.*;

public class Vector2iGsonAdapter
{
	public static Vector2i parse(JSONArray array)
	{
		if(array.length() != 2)
			throw new JSONException("Expected 2 elements in Vector2i array, found: " + array.length());
		return new Vector2i(array.getInt(0), array.getInt(1));
	}
}