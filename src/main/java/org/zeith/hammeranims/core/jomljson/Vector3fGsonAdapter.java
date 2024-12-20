package org.zeith.hammeranims.core.jomljson;

import shaded.joml.Vector3f;
import shaded.json.JSONArray;
import shaded.json.JSONException;

public class Vector3fGsonAdapter
{
	public static Vector3f parse(JSONArray array)
	{
		if(array.size() != 3)
			throw new JSONException("Expected 3 elements in Vector3f array, found: " + array.size());
		return new Vector3f(array.getFloat(0), array.getFloat(1), array.getFloat(2));
	}
}