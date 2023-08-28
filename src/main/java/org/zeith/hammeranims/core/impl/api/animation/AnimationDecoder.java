package org.zeith.hammeranims.core.impl.api.animation;

import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.*;
import org.zeith.hammeranims.api.animation.event.DecodeAnimationEvent;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammerlib.util.shaded.json.JSONObject;

import java.time.Duration;
import java.util.*;

public class AnimationDecoder
{
	static
	{
		HammerAnimationsApi.EVENT_BUS.addListener(AnimationDecoder::decodeAnimation);
	}
	
	public static void init()
	{
	}
	
	public static void decodeAnimation(DecodeAnimationEvent e)
	{
		if(e.container == DefaultsHA.NULL_ANIMATION)
		{
			e.setDecoded(DefaultsHA.NULL_ANIMATION_SYNTETIC);
			return;
		}
		
		JSONObject obj = e.asObject().orElse(null);
		if(obj == null) return;
		if(!obj.has("bones"))
			return;
		
		AnimationLocation loc = new AnimationLocation(e.container.getRegistryKey(), e.key);
		
		if(!"1.8.0".equals(e.formatVersion))
			loc.warn("Potentially unsupported version of animation: " + e.formatVersion +
					"; We support 1.8.0. Potential incompatibility may arise!");
		
		Object o = obj.opt("loop");
		
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
				HammerAnimations.LOG.warn(
						"Unable to parse " + e.container.getRegistryKey() + "#" + e.key + " animation's bone " +
								boneKey + ", skipping.");
				continue;
			}
			bones.put(boneKey, parse);
		}
		
		final LoopMode mode = modeRaw;
		final Map<String, BoneAnimation> bonesView = Collections.unmodifiableMap(bones);
		e.setDecoded(new Animation(e.container, e.key, new IAnimationData()
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
			public String toString()
			{
				return "IAnimationData{loop_mode=" + mode + ",duration=" + time.toMillis() / 1000F + ",bones=" +
						bonesView + "}";
			}
		}));
	}
}