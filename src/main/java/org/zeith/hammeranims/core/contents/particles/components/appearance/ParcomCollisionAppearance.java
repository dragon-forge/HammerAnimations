package org.zeith.hammeranims.core.contents.particles.components.appearance;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.ParticleMaterial;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

public class ParcomCollisionAppearance
		extends ParcomAppearanceBillboard
{
	public ParticleMaterial material = ParticleMaterial.OPAQUE;
	public ResourceLocation texture = new ResourceLocation("textures/atlas/blocks.png");
	
	public LzFactory enabled = InterpolatedDouble.constant(0);
	
	public boolean lit;
	
	public ParcomCollisionAppearance(JsonElement elem)
	{
		super(elem);
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		
		if(element.has("enabled")) this.enabled = InterpolatedDouble.parse(element.get("enabled"));
		if(element.has("lit")) this.lit = element.get("lit").getAsBoolean();
		if(element.has("material")) this.material = ParticleMaterial.fromString(element.get("material").getAsString());
		
		if(element.has("texture"))
		{
			String texture = element.get("texture").getAsString();
			this.texture = InstanceHelpers.tryParseLocation(texture);
		}
	}
	
	@Override
	public ParcomAppearanceBillboardInstance createInstance(LzVariableStore vars)
	{
		return new ParcomCollisionAppearanceInstance(this, vars);
	}
	
	public static class ParcomCollisionAppearanceInstance
			extends ParcomAppearanceBillboard.ParcomAppearanceBillboardInstance
	{
		public final ParticleMaterial material;
		public final ResourceLocation texture;
		public final boolean lit;
		public final LzExpression enabled;
		
		public ParcomCollisionAppearanceInstance(ParcomCollisionAppearance o, LzVariableStore vars)
		{
			super(o, vars);
			this.material = o.material;
			this.texture = o.texture;
			this.lit = o.lit;
			this.enabled = o.enabled.instantiate(vars);
		}
	}
}