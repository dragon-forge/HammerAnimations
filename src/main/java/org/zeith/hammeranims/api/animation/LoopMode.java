package org.zeith.hammeranims.api.animation;

public enum LoopMode
{
	ONCE, // shows the first frame after the animation was completed.
	HOLD_ON_LAST_FRAME, // Holds on last frame after the animation was completed.
	LOOP; // Loops the animation to the first frame after it reaches the last frame.
	
	public static final int VALUE_COUNT = values().length;
}