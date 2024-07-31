package org.zeith.hammeranims.api.animation.data.effects;

import com.zeitheron.hammercore.lib.zlib.json.JSONArray;
import com.zeitheron.hammercore.lib.zlib.json.JSONObject;
import lombok.Value;
import lombok.val;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

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
			for(Object value : ((JSONArray) object).values())
				lst.addAll(decode(value));
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