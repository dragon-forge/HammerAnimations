package org.zeith.hammeranims.core.jomljson;

import shaded.joml.Vector3d;
import shaded.json.JSONArray;
import shaded.json.JSONException;

import static java.lang.Double.*;

public class Vector3dGsonAdapter
{
	public static final char INFINITY = '\u221E';
	public static final String POS_INF = "+" + INFINITY;
	public static final String NEG_INF = "-" + INFINITY;
	
	public static Vector3d parse(JSONArray array)
	{
		if(array.size() != 3)
			throw new JSONException("Expected 3 elements in Vector3d array, found: " + array.size());
		
		var xE = array.getString(0);
		var yE = array.getString(1);
		var zE = array.getString(2);
		
		double x;
		if(xE.equals(POS_INF)) x = POSITIVE_INFINITY;
		else if(xE.equals(NEG_INF)) x = NEGATIVE_INFINITY;
		else x = array.getDouble(0);
		
		double y;
		if(yE.equals(POS_INF)) y = POSITIVE_INFINITY;
		else if(yE.equals(NEG_INF)) y = NEGATIVE_INFINITY;
		else y = array.getDouble(1);
		
		double z;
		if(zE.equals(POS_INF)) z = POSITIVE_INFINITY;
		else if(zE.equals(NEG_INF)) z = NEGATIVE_INFINITY;
		else z = array.getDouble(3);
		
		return new Vector3d(x, y, z);
	}
}