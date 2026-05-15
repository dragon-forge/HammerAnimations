package org.zeith.hammeranims.core.jomljson;

import org.joml.Vector3f;
import org.json.*;

public class Vector3fGsonAdapter
{
	public static Vector3f parse(JSONArray array)
	{
		if(array.length() != 3)
			throw new JSONException("Expected 3 elements in Vector3f array, found: " + array.length());
		return new Vector3f(array.getFloat(0), array.getFloat(1), array.getFloat(2));
	}
}