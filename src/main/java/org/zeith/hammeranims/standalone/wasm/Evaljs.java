package org.zeith.hammeranims.standalone.wasm;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.impl.JS;

public class Evaljs
{
	public static JSObject eval(String src)
	{
		return eval(JS.wrap(src));
	}
	
	@JSBody(
			params = { "src" },
			script = "return eval(src)"
	)
	private static native JSObject eval(JSObject src);
}