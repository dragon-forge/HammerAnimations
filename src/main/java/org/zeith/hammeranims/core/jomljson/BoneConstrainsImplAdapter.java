package org.zeith.hammeranims.core.jomljson;

import com.google.gson.*;
import org.zeith.hammeranims.core.impl.api.geometry.constrains.BoneConstraintsImpl;
import org.zeith.hammeranims.joml.Vector3d;

import java.lang.reflect.Type;

public class BoneConstrainsImplAdapter
		implements JsonDeserializer<BoneConstraintsImpl>, JsonSerializer<BoneConstraintsImpl>
{
	@Override
	public BoneConstraintsImpl deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		JsonObject object = json.getAsJsonObject();
		BoneConstraintsImpl c = new BoneConstraintsImpl();
		if(object.has("position"))
		{
			JsonObject o = object.getAsJsonObject("position");
			//noinspection AssignmentUsedAsCondition
			if(c.hasTranslation = o.size() > 0)
			{
				if(o.has("min"))
					c.minTranslation = context.deserialize(o.get("min"), Vector3d.class);
				else c.minTranslation = new Vector3d(Double.NEGATIVE_INFINITY);
				
				if(o.has("max"))
					c.maxTranslation = context.deserialize(o.get("max"), Vector3d.class);
				else c.maxTranslation = new Vector3d(Double.POSITIVE_INFINITY);
			}
		}
		if(object.has("rotation"))
		{
			JsonObject o = object.getAsJsonObject("rotation");
			//noinspection AssignmentUsedAsCondition
			if(c.hasRotation = o.size() > 0)
			{
				if(o.has("min"))
					c.minRotation = context.deserialize(o.get("min"), Vector3d.class);
				else c.minRotation = new Vector3d(Double.NEGATIVE_INFINITY);
				
				if(o.has("max"))
					c.maxRotation = context.deserialize(o.get("max"), Vector3d.class);
				else c.maxRotation = new Vector3d(Double.POSITIVE_INFINITY);
			}
		}
		if(object.has("scale"))
		{
			JsonObject o = object.getAsJsonObject("scale");
			//noinspection AssignmentUsedAsCondition
			if(c.hasScale = o.size() > 0)
			{
				if(o.has("min"))
					c.minScale = context.deserialize(o.get("min"), Vector3d.class);
				else c.minScale = new Vector3d(Double.NEGATIVE_INFINITY);
				
				if(o.has("max"))
					c.maxScale = context.deserialize(o.get("max"), Vector3d.class);
				else c.maxScale = new Vector3d(Double.POSITIVE_INFINITY);
			}
		}
		return c;
	}
	
	@Override
	public JsonElement serialize(BoneConstraintsImpl src, Type typeOfSrc, JsonSerializationContext context)
	{
		JsonObject obj = new JsonObject();
		
		if(src.hasTranslation())
		{
			JsonObject translation = new JsonObject();
			translation.add("min", context.serialize(src.minTranslation, Vector3d.class));
			translation.add("max", context.serialize(src.maxTranslation, Vector3d.class));
			obj.add("position", translation);
		}
		
		if(src.hasRotation())
		{
			JsonObject rotation = new JsonObject();
			rotation.add("min", context.serialize(src.minRotation, Vector3d.class));
			rotation.add("max", context.serialize(src.maxRotation, Vector3d.class));
			obj.add("rotation", rotation);
		}
		
		if(src.hasScale())
		{
			JsonObject scale = new JsonObject();
			scale.add("min", context.serialize(src.minScale, Vector3d.class));
			scale.add("max", context.serialize(src.maxScale, Vector3d.class));
			obj.add("scale", scale);
		}
		
		return obj;
	}
}
