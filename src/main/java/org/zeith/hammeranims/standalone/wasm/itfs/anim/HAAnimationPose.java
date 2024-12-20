package org.zeith.hammeranims.standalone.wasm.itfs.anim;

import org.teavm.jso.*;

@JSClass(name = "HammerAnimsPose")
public interface HAAnimationPose
		extends JSObject
{
	@JSProperty
	@JSExport
	JSObject getBones();
}