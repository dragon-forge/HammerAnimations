package org.zeith.hammeranims.core.impl.api.particles.components.shape;

import com.google.gson.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.itf.IParticleInitialize;

public abstract class ParcomShapeBase
		implements IParticleInitialize
{
	@SuppressWarnings("rawtypes")
	public InterpolatedDouble[] offset = { InterpolatedDouble.zero(), InterpolatedDouble.zero(), InterpolatedDouble.zero() };
	public ShapeDirection direction = ShapeDirection.OUTWARDS;
	public boolean surface = false;
	
	public ParcomShapeBase(JsonElement elem)
	{
		if(!elem.isJsonObject()) return;
		
		JsonObject element = elem.getAsJsonObject();
		
		if(element.has("offset"))
		{
			JsonArray array = element.getAsJsonArray("offset");
			
			if(array.size() >= 3)
			{
				this.offset[0] = InterpolatedDouble.parse(array.get(0));
				this.offset[1] = InterpolatedDouble.parse(array.get(1));
				this.offset[2] = InterpolatedDouble.parse(array.get(2));
			}
		}
		
		if(element.has("direction"))
		{
			JsonElement direction = element.get("direction");
			
			if(direction.isJsonPrimitive())
			{
				String name = direction.getAsString();
				
				if(name.equals("inwards")) this.direction = ShapeDirection.INWARDS;
				else this.direction = ShapeDirection.OUTWARDS;
			} else if(direction.isJsonArray())
			{
				JsonArray array = direction.getAsJsonArray();
				
				if(array.size() >= 3)
				{
					this.direction = new ShapeDirection.Vector(
							InterpolatedDouble.parse(array.get(0)),
							InterpolatedDouble.parse(array.get(1)),
							InterpolatedDouble.parse(array.get(2))
					);
				}
			}
		}
		
		if(element.has("surface_only"))
		{
			this.surface = element.get("surface_only").getAsBoolean();
		}
	}
}