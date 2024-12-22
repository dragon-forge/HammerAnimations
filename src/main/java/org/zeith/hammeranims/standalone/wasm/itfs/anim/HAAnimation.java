package org.zeith.hammeranims.standalone.wasm.itfs.anim;

import org.teavm.jso.*;
import org.teavm.jso.core.JSString;

@JSClass(name = "AnimationState")
public interface HAAnimation
		extends JSObject
{
	@JSExport
	HAAnimationPose poseAt(double seconds);
	
	@JSExport
	JSObject mix(JSObject other);
	
	@JSExport
	HAAnimation reconfigure(JSObject options);
	
	@JSProperty
	@JSExport
	double getExpectedDuration();
	
	@JSProperty
	@JSExport
	JSString getLoopMode();
}