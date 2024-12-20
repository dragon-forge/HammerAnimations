package org.zeith.hammeranims.standalone.wasm.itfs.anim;

import org.teavm.jso.*;

@JSClass(name = "HammerAnimsAnimation")
public interface HAAnimation
	extends JSObject
{
	@JSExport
	HAAnimationPose poseAt(double seconds);
	
	@JSProperty
	@JSExport
	double getExpectedDuration();
}