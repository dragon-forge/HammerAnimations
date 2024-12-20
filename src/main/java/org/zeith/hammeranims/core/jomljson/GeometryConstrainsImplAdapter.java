package org.zeith.hammeranims.core.jomljson;

import shaded.json.JSONObject;
import org.zeith.hammeranims.core.impl.api.geometry.constrains.BoneConstraintsImpl;
import org.zeith.hammeranims.core.impl.api.geometry.constrains.GeometryConstrainsImpl;
import org.zeith.hammeranims.core.utils.GsonHelper;

import java.util.HashMap;
import java.util.Map;

public class GeometryConstrainsImplAdapter
{
	public static GeometryConstrainsImpl parse(JSONObject object)
	{
		Map<String, BoneConstraintsImpl> builder = new HashMap<>();
		for(var e : object.keySet())
			builder.put(e, BoneConstrainsImplAdapter.parse(GsonHelper.getAsJsonObject(object, e)));
		return new GeometryConstrainsImpl(Map.copyOf(builder));
	}
}