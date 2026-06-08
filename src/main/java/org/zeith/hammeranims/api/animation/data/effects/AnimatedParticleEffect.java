package org.zeith.hammeranims.api.animation.data.effects;

import lombok.*;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammerlib.util.shaded.json.*;

import java.util.*;

@Value
public class AnimatedParticleEffect
{
	ResourceLocation effect;
	String locator;
	String pre_effect_script;
	
	public IParticleContainer getParticle()
	{
		return IParticleContainer.byRegistryKey(effect);
	}
	
	public static List<AnimatedParticleEffect> decode(Object object)
	{
		if(object instanceof JSONArray)
		{
			List<AnimatedParticleEffect> lst = new ArrayList<>();
			var arr = (JSONArray) object;
			for(int i = 0, len = arr.size(); i < len; i++) lst.addAll(decode(arr.get(i)));
			return Collections.unmodifiableList(lst);
		} else if(object instanceof JSONObject)
		{
			val ob = (JSONObject) object;
			return Collections.singletonList(
					new AnimatedParticleEffect(
							InstanceHelpers.tryParseLocation(((JSONObject) object).getString("effect")),
							ob.optString("locator"),
							ob.optString("pre_effect_script")
					)
			);
		}
		
		return Collections.emptyList();
	}
}