package org.zeith.hammeranims.standalone.wasm;

import org.teavm.jso.JSExport;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.*;
import org.teavm.jso.impl.JS;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.standalone.procedures.ProcedureRenderAnimation;
import org.zeith.hammeranims.standalone.utils.AnimationState;
import org.zeith.hammeranims.standalone.wasm.itfs.anim.HAAnimation;
import org.zeith.hammeranims.standalone.wasm.itfs.character.GeometryState;
import org.zeith.hammeranims.standalone.wasm.itfs.character.HAGeoBuffers;

import java.util.ArrayList;
import java.util.List;

public class Main
{
	@JSExport
	public static void core_init()
	{
		HammerAnimations.init();
	}

	@JSExport
	public static HAGeoBuffers anim_parseGeometry(String id, String src)
	{
		return new GeometryState(HammerAnimations.loadGeo(id, src));
	}
	
	@JSExport
	public static HAAnimation anim_parseAnimations(JSArray<JSObject> animations)
	{
		List<ConfiguredAnimation> anims = new ArrayList<>();
		for(int i = 0; i < animations.getLength(); i++)
		{
			JSObject entry = animations.get(i);
			
			var id = JS.unwrapString(JS.get(entry, JS.wrap("id")));
			var src = ProcedureRenderAnimation.addAnimationSalt(JS.unwrapString(JS.get(entry, JS.wrap("src"))));
			
			IAnimationContainer ctr = HammerAnimations.loadAnim(id, src);
			
			var ca = ctr.configure();
			
			JSObject w = JS.get(entry, JS.wrap("weight"));
			if(!JS.isNull(w) && w instanceof JSNumber) ca.weight(JS.unwrapFloat(w));
			
			w = JS.get(entry, JS.wrap("speed"));
			if(!JS.isNull(w) && w instanceof JSNumber) ca.speed(JS.unwrapFloat(w));
			
			w = JS.get(entry, JS.wrap("startTime"));
			if(!JS.isNull(w) && w instanceof JSNumber) ca.speed(JS.unwrapFloat(w));
			
			w = JS.get(entry, JS.wrap("freezeAt"));
			if(!JS.isNull(w) && w instanceof JSNumber) ca.freezeAt(JS.unwrapFloat(w));
			
			w = JS.get(entry, JS.wrap("reversed"));
			if(!JS.isNull(w) && w instanceof JSBoolean) ca.reversed(JS.unwrapBoolean(w));
			
			w = JS.get(entry, JS.wrap("transitionTime"));
			if(!JS.isNull(w) && w instanceof JSNumber) ca.transitionTime(JS.unwrapFloat(w));
			
			anims.add(ca);
		}
		
		return new AnimationState(anims);
	}
}