package org.zeith.hammer.models.core.impl.api.animation;

import com.zeitheron.hammercore.lib.zlib.json.JSONObject;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.zeith.hammer.models.HammerModels;
import org.zeith.hammer.models.api.HammerModelsApi;
import org.zeith.hammer.models.api.animation.*;
import org.zeith.hammer.models.api.animation.data.*;
import org.zeith.hammer.models.api.animation.event.DecodeAnimationEvent;

import java.time.Duration;
import java.util.*;

public class AnimationDecoder
{
	static
	{
		HammerModelsApi.EVENT_BUS.register(AnimationDecoder.class);
	}
	
	public static void init()
	{
	}
	
	@SubscribeEvent
	public static void decodeAnimation(DecodeAnimationEvent e)
	{
		JSONObject obj = e.asObject().orElse(null);
		if(obj == null) return;
		if(!obj.has("animation_length") || !obj.has("bones"))
			return;
		
		Object o = obj.opt("loop");
		
		LoopMode modeRaw = o instanceof Boolean && ((Boolean) o) ? LoopMode.LOOP : LoopMode.HOLD_ON_LAST_FRAME;
		if(o instanceof String)
		{
			String modeStr = (String) o;
			if(modeStr.equalsIgnoreCase("hold_on_last_frame"))
				modeRaw = LoopMode.HOLD_ON_LAST_FRAME;
		}
		
		Duration time = Duration.ofMillis(Math.round(obj.getDouble("animation_length") * 1000));
		Map<String, BoneAnimation> bones = new HashMap<>();
		
		JSONObject bonesObj = obj.getJSONObject("bones");
		for(String boneKey : bonesObj.keySet())
		{
			BoneAnimation parse = BoneAnimation.parse(bonesObj.getJSONObject(boneKey));
			if(parse == null)
			{
				HammerModels.LOG.warn(
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