package org.zeith.hammeranims.api.animation.data.effects;

import com.zeitheron.hammercore.lib.zlib.json.JSONArray;
import com.zeitheron.hammercore.lib.zlib.json.JSONObject;
import lombok.Value;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

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
			for(Object value : ((JSONArray) object).values())
				lst.addAll(decode(value));
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