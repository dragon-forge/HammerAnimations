package org.zeith.hammeranims.core.jomljson;

import com.google.gson.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.particles.ParticleEffect;
import org.zeith.hammeranims.api.particles.ParticleMaterial;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.IParticleComponentType;
import org.zeith.hammeranims.api.particles.curve.ParticleCurve;
import org.zeith.hammeranims.core.utils.GsonHelper;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import java.lang.reflect.Type;
import java.util.Map;

public class ParticleEffectAdapter
		implements JsonDeserializer<ParticleEffect.Builder>
{
	@Override
	public ParticleEffect.Builder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		ParticleEffect.Builder peb = ParticleEffect.builder();
		
		if(!json.isJsonObject())
			throw new JsonParseException("JsonElement " + json + " is not a json object.");
		
		/* Skip format_version check to avoid breaking semi-compatible particles */
		JsonObject root = json.getAsJsonObject();
		
		JsonObject effect = GsonHelper.getAsJsonObject(root, "particle_effect");
		
		this.parseDescription(peb, GsonHelper.getAsJsonObject(effect, "description"));
		
		if(effect.has("curves"))
		{
			JsonElement curves = effect.get("curves");
			
			if(curves.isJsonObject())
			{
				this.gatherCurves(peb, curves.getAsJsonObject());
			}
		}
		
		this.gatherComponents(peb, GsonHelper.getAsJsonObject(effect, "components"));
		
		return peb;
	}
	
	private void parseDescription(ParticleEffect.Builder peb, JsonObject description)
			throws JsonParseException
	{
		JsonObject parameters = GsonHelper.getAsJsonObject(description, "basic_render_parameters");
		
		if(parameters.has("material"))
			peb.material(ParticleMaterial.fromString(parameters.get("material").getAsString()));
		
		if(parameters.has("texture"))
		{
			String texture = parameters.get("texture").getAsString() + ".png";
			peb.texture(InstanceHelpers.tryParseLocation(texture));
		}
	}
	
	private void gatherCurves(ParticleEffect.Builder peb, JsonObject curves)
	{
		for(Map.Entry<String, JsonElement> entry : curves.entrySet())
		{
			JsonElement element = entry.getValue();
			if(!element.isJsonObject()) continue;
			peb.curve(new ParticleCurve(entry.getKey(), element.getAsJsonObject()));
		}
	}
	
	private void gatherComponents(ParticleEffect.Builder peb, JsonObject components)
	{
		for(Map.Entry<String, JsonElement> entry : components.entrySet())
		{
			String key = entry.getKey();
			
			IParticleComponentType comType = IParticleComponentType.byId(InstanceHelpers.tryParseLocation(key));
			if(comType == null)
			{
				HammerAnimations.LOG.warn("Unable to find unknown particle component by key {}", key);
				continue;
			}
			
			IParticleComponent com = comType.fromJson(entry.getValue());
			if(com == null)
			{
				HammerAnimations.LOG.warn("Unable to parse particle component {} ({})", key, entry.getValue());
				continue;
			}
			
			peb.component(comType, com);
		}
	}
	
	public static boolean isEmpty(JsonElement element)
	{
		if(element == null) return true;
		if(element.isJsonArray()) return element.getAsJsonArray().size() == 0;
		if(element.isJsonObject()) return element.getAsJsonObject().size() == 0;
		if(!element.isJsonPrimitive()) return element.isJsonNull();
		JsonPrimitive primitive = element.getAsJsonPrimitive();
		if(primitive.isString()) return primitive.getAsString().isEmpty();
		if(primitive.isNumber()) return equals(primitive.getAsDouble(), 0);
		return false;
	}
	
	public static boolean equals(double a, double b)
	{
		return Math.abs(a - b) < 0.00001;
	}
}