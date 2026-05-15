package org.zeith.hammeranims.core.contents.particles.components.shape;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.itf.IParticleInitialize;

import java.util.function.Function;

public abstract class ParcomShapeBase
		implements IParticleComponent
{
	public LzFactory[] offset = {InterpolatedDouble.zero(), InterpolatedDouble.zero(), InterpolatedDouble.zero()};
	public Function<LzVariableStore, ShapeDirection> direction;
	public boolean surface = false;
	
	public ParcomShapeBase(JsonElement elem)
	{
		direction = f0 -> ShapeDirection.OUTWARDS;
		
		if(!elem.isJsonObject())
			return;
		
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
				
				if(name.equals("inwards")) this.direction = f1 -> ShapeDirection.INWARDS;
				else this.direction = f2 -> ShapeDirection.OUTWARDS;
			} else if(direction.isJsonArray())
			{
				JsonArray array = direction.getAsJsonArray();
				
				if(array.size() >= 3)
				{
					LzFactory x = InterpolatedDouble.parse(array.get(0));
					LzFactory y = InterpolatedDouble.parse(array.get(1));
					LzFactory z = InterpolatedDouble.parse(array.get(2));
					
					this.direction = vars -> new ShapeDirection.Vector(
							x.instantiate(vars),
							y.instantiate(vars),
							z.instantiate(vars)
					);
				}
			}
		}
		
		if(element.has("surface_only"))
		{
			this.surface = element.get("surface_only").getAsBoolean();
		}
	}
	
	@Override
	public abstract ParcomShapeBaseInstance createInstance(LzVariableStore vars);
	
	public static abstract class ParcomShapeBaseInstance
			implements IParticleInitialize
	{
		protected final LzExpression[] offset;
		protected final ShapeDirection direction;
		public final boolean surface;
		
		public ParcomShapeBaseInstance(LzExpression[] offset, ShapeDirection direction, boolean surface)
		{
			this.offset = offset;
			this.direction = direction;
			this.surface = surface;
		}
	}
}