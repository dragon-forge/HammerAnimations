package org.zeith.hammeranims.core.init;

import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.IAnimationData;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.time.TimeFunction;
import org.zeith.hammeranims.core.contents.time.LinearTimeFunction;

public interface DefaultsHA
{
	IGeometryContainer NULL_GEOMETRY = HammerAnimations.loadGeo("null", """
			{
				"format_version": "1.12.0",
				"minecraft:geometry": [
					{
						"description": {
							"identifier": "geometry.null",
							"texture_width": 1,
							"texture_height": 1,
							"visible_bounds_width": 0,
							"visible_bounds_height": 0,
							"visible_bounds_offset": [0, 0, 0]
						},
						"bones": []
					}
				]
			}"""
	);
	
	IAnimationContainer NULL_ANIMATION = IAnimationContainer.create();
	
	TimeFunction LINEAR_TIME = new LinearTimeFunction();
	
	Animation NULL_ANIMATION_SYNTETIC = new Animation(NULL_ANIMATION, "null", IAnimationData.EMPTY);
	AnimationHolder NULL_ANIM = new AnimationHolder(NULL_ANIMATION, "null")
	{
		@Override
		public @NotNull Animation get()
		{
			return NULL_ANIMATION_SYNTETIC;
		}
	};
}