package org.zeith.hammeranims.core.jomljson;

import org.joml.Vector3d;
import org.json.JSONObject;
import org.zeith.hammeranims.core.impl.api.geometry.constrains.BoneConstraintsImpl;

public class BoneConstrainsImplAdapter
{
	public static BoneConstraintsImpl parse(JSONObject object)
	{
		BoneConstraintsImpl c = new BoneConstraintsImpl();
		if(object.has("position"))
		{
			JSONObject o = object.getJSONObject("position");
			
			//noinspection AssignmentUsedAsCondition
			if(c.hasTranslation = !o.isEmpty())
			{
				if(o.has("min"))
					c.minTranslation = Vector3dGsonAdapter.parse(o.getJSONArray("min"));
				else c.minTranslation = new Vector3d(Double.NEGATIVE_INFINITY);
				
				if(o.has("max"))
					c.maxTranslation = Vector3dGsonAdapter.parse(o.getJSONArray("max"));
				else c.maxTranslation = new Vector3d(Double.POSITIVE_INFINITY);
			}
		}
		if(object.has("rotation"))
		{
			JSONObject o = object.getJSONObject("rotation");
			
			//noinspection AssignmentUsedAsCondition
			if(c.hasRotation = !o.isEmpty())
			{
				if(o.has("min"))
					c.minTranslation = Vector3dGsonAdapter.parse(o.getJSONArray("min"));
				else c.minRotation = new Vector3d(Double.NEGATIVE_INFINITY);
				
				if(o.has("max"))
					c.maxTranslation = Vector3dGsonAdapter.parse(o.getJSONArray("max"));
				else c.maxRotation = new Vector3d(Double.POSITIVE_INFINITY);
			}
		}
		if(object.has("scale"))
		{
			JSONObject o = object.getJSONObject("scale");
			
			//noinspection AssignmentUsedAsCondition
			if(c.hasScale = !o.isEmpty())
			{
				if(o.has("min"))
					c.minTranslation = Vector3dGsonAdapter.parse(o.getJSONArray("min"));
				else c.minScale = new Vector3d(Double.NEGATIVE_INFINITY);
				
				if(o.has("max"))
					c.maxTranslation = Vector3dGsonAdapter.parse(o.getJSONArray("max"));
				else c.maxScale = new Vector3d(Double.POSITIVE_INFINITY);
			}
		}
		return c;
	}
}