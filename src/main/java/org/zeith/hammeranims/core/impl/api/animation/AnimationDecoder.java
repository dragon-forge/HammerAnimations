package org.zeith.hammeranims.core.impl.api.animation;

import shaded.json.JSONObject;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.BoneAnimation;
import org.zeith.hammeranims.api.animation.data.IAnimationData;
import org.zeith.hammeranims.core.init.DefaultsHA;

import java.time.Duration;
import java.util.*;

public class AnimationDecoder
{
	public static Animation decodeAnimation(JSONObject obj, IAnimationContainer container, String key, String formatVersion)
	{
		if(container == DefaultsHA.NULL_ANIMATION)
		{
			return DefaultsHA.NULL_ANIMATION_SYNTETIC;
		}
		
		if(obj == null) return null;
		if(!obj.has("bones"))
			return null;
		
		AnimationLocation loc = new AnimationLocation(container.getRegistryKey(), key);
		
		if(!"1.8.0".equals(formatVersion))
			loc.warn("Potentially unsupported version of animation: {}; We support 1.8.0. Potential incompatibility may arise!", formatVersion);
		
		Object o = obj.opt("loop");
		
		float weight = 1F;
		String blend_weight = obj.optString("blend_weight", "");
		if(blend_weight != null && !blend_weight.isEmpty())
			try
			{
				weight = (float) Double.parseDouble(blend_weight);
			} catch(Exception err)
			{
				loc.warn("Found blend_weight: \"{}\", but it's unable to be parsed as a number.", JSONObject.quote(blend_weight));
			}
		final float fweight = weight;
		
		LoopMode modeRaw = o instanceof Boolean && ((Boolean) o) ? LoopMode.LOOP : LoopMode.ONCE;
		if(o instanceof String)
		{
			String modeStr = (String) o;
			if(modeStr.equalsIgnoreCase("hold_on_last_frame"))
				modeRaw = LoopMode.HOLD_ON_LAST_FRAME;
		}
		
		Duration time = Duration.ofMillis(Math.round(obj.optDouble("animation_length") * 1000));
		Map<String, BoneAnimation> bones = new HashMap<>();
		
		JSONObject bonesObj = obj.getJSONObject("bones");
		for(String boneKey : bonesObj.keySet())
		{
			BoneAnimation parse = BoneAnimation.parse(loc, bonesObj.getJSONObject(boneKey));
			if(parse == null)
			{
				loc.warn("Unable to parse bone {}, skipping.", boneKey);
				continue;
			}
			bones.put(boneKey, parse);
		}
		
		final LoopMode mode = modeRaw;
		final Map<String, BoneAnimation> bonesView = Collections.unmodifiableMap(bones);
		
		return new Animation(container, key, new IAnimationData()
		{
			@Override
			public LoopMode getLoopMode()
			{
				return mode;
			}
			
			@Override
			public Duration getLength()
			{
				return time;
			}
			
			@Override
			public Map<String, BoneAnimation> getBoneAnimations()
			{
				return bonesView;
			}
			
			@Override
			public float getWeight()
			{
				return fweight;
			}
			
			@Override
			public String toString()
			{
				return "IAnimationData{loop_mode=" + mode + ",duration=" + time.toMillis() / 1000F + ",bones=" +
					   bonesView + ",weight=" + fweight + "}";
			}
		}
		);
	}
}