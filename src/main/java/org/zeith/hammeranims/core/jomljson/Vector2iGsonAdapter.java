package org.zeith.hammeranims.core.jomljson;

import shaded.joml.Vector2i;
import shaded.json.JSONArray;
import shaded.json.JSONException;

public class Vector2iGsonAdapter
{
	public static Vector2i parse(JSONArray array)
	{
		if(array.size() != 2)
			throw new JSONException("Expected 2 elements in Vector2i array, found: " + array.size());
		return new Vector2i(array.getInt(0), array.getInt(1));
	}
}