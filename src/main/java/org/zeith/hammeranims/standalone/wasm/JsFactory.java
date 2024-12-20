package org.zeith.hammeranims.standalone.wasm;

import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSFunction;
import org.teavm.jso.impl.JS;
import org.zeith.hammeranims.core.js.IDoubleTest;

public class JsFactory
{
	public static IDoubleTest parse4Args(String fun, Object[] args)
	{
		try
		{
			var func = (JSFunction) Evaljs.eval(fun);
			return () ->
					JS.unwrapDouble((JSObject) func.call(null, args[0], args[1], args[2], args[3]));
		} catch(Throwable e)
		{
			return null;
		}
	}
}