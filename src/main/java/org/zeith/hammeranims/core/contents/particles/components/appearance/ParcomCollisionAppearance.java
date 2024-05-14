package org.zeith.hammeranims.core.contents.particles.components.appearance;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.ParticleMaterial;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

public class ParcomCollisionAppearance
		extends ParcomAppearanceBillboard
{
	public ParticleMaterial material = ParticleMaterial.OPAQUE;
	public ResourceLocation texture = PlayerContainer.BLOCK_ATLAS;
	
	public InterpolatedDouble<ParticleVariables> enabled = InterpolatedDouble.constant(0);
	
	public boolean lit; //gets set from GuiCollisionLighting
	
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
}