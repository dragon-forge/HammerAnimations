package org.zeith.hammeranims.standalone.wasm;

import org.teavm.jso.JSExport;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.*;
import org.teavm.jso.impl.JS;
import org.teavm.jso.json.JSON;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.Animation;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.standalone.wasm.itfs.anim.AnimationState;
import org.zeith.hammeranims.standalone.wasm.itfs.anim.HAAnimation;
import org.zeith.hammeranims.standalone.wasm.itfs.character.GeometryState;
import org.zeith.hammeranims.standalone.wasm.itfs.character.HAGeoBuffers;

import java.time.Duration;
import java.util.*;

public class Main
{
	@JSExport
	public static void core_init()
	{
		HammerAnimations.init();
	}
	
	@JSExport
	public static void logging_disable()
	{
		Log.disable = true;
	}
	
	@JSExport
	public static void logging_enable()
	{
		Log.disable = false;
	}
	
	@JSExport
	public static HAGeoBuffers anim_parseGeometry(String id, String src)
	{
		return new GeometryState(HammerAnimations.loadGeo(id, src));
	}
	
	@JSExport
	public static JSObject anim_parseAnimationContainer(String id, String src, JSObject configurations)
	{
		var noConf = JS.isNull(configurations) || configurations instanceof JSUndefined;
		
		JSObject map = JSON.parse("{}");
		IAnimationContainer ctr = HammerAnimations.loadAnim(id, src);
		for(Map.Entry<String, Animation> data : ctr.getAnimations().entrySet())
		{
			var ca = data.getValue().configure();
			
			JSString n = JSString.valueOf(data.getKey());
			JSObject entry = noConf ? JSUndefined.instance() : JS.get(configurations, n);
			if(JS.isNull(entry) || entry instanceof JSUndefined)
			{
				JS.set(map, n, new AnimationState(List.of(ca)));
				continue;
			}
			
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
			else ca = ca.transitionTime(Duration.ZERO);
			
			JS.set(map, n, new AnimationState(List.of(ca)));
		}
		return map;
	}
	
	@JSExport
	public static HAAnimation anim_parseAnimations(JSArray<JSObject> animations)
	{
		List<ConfiguredAnimation> anims = new ArrayList<>();
		for(int i = 0; i < animations.getLength(); i++)
		{
			JSObject entry = animations.get(i);
			
			var id = JS.unwrapString(JS.get(entry, JS.wrap("id")));
			var src = JS.unwrapString(JS.get(entry, JS.wrap("src")));
			
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
			else ca = ca.transitionTime(Duration.ZERO);
			
			anims.add(ca);
		}
		
		return new AnimationState(anims);
	}
}