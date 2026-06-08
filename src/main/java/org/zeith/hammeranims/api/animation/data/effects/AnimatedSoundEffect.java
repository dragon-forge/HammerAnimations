package org.zeith.hammeranims.api.animation.data.effects;

import lombok.Value;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammerlib.util.shaded.json.JSONArray;
import org.zeith.hammerlib.util.shaded.json.JSONObject;

import java.util.*;

@Value
public class AnimatedSoundEffect
{
	ResourceLocation effect;
	
	public static List<AnimatedSoundEffect> decode(Object object)
	{
		if(object instanceof JSONArray)
		{
			List<AnimatedSoundEffect> lst = new ArrayList<>();
			var arr = (JSONArray) object;
			for(int i = 0, len = arr.size(); i < len; i++) lst.addAll(decode(arr.get(i)));
			return Collections.unmodifiableList(lst);
		} else if(object instanceof JSONObject)
		{
			return Collections.singletonList(
					new AnimatedSoundEffect(
							InstanceHelpers.tryParseLocation(((JSONObject) object).getString("effect"))
					)
			);
		}
		
		return Collections.emptyList();
	}
}