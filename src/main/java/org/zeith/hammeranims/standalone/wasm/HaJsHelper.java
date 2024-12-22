package org.zeith.hammeranims.standalone.wasm;

import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSBoolean;
import org.teavm.jso.core.JSNumber;
import org.teavm.jso.impl.JS;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;

import java.time.Duration;

public class HaJsHelper
{
	public static void configure(ConfiguredAnimation ca, JSObject entry)
	{
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
		else ca.transitionTime(Duration.ZERO);
	}
}
