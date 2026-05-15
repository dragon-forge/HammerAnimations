package org.zeith.hammeranims.standalone.wasm;

import org.teavm.jso.*;
import org.teavm.jso.core.*;
import org.teavm.jso.impl.JS;
import org.teavm.jso.json.JSON;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.standalone.wasm.itfs.anim.*;
import org.zeith.hammeranims.standalone.wasm.itfs.geom.*;

import java.util.*;

public class Main
{
	@JSExport
	public static JSString core_getVersion()
	{
		return JSString.valueOf(HammerAnimations.VERSION);
	}
	
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
	public static HAGeoState anim_parseGeometry(String id, String src)
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
			
			HaJsHelper.configure(ca, entry);
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
			HaJsHelper.configure(ca, entry);
			
			anims.add(ca);
		}
		
		return new AnimationState(anims);
	}
}