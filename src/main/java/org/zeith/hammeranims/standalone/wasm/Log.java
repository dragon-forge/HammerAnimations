package org.zeith.hammeranims.standalone.wasm;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.impl.JS;
import org.zeith.hammeranims.standalone.utils.Log4jStyle;

public class Log
{
	public static boolean disable;
	public static final Log inst = new Log();
	
	public static Object makeError()
	{
		return new Throwable();
	}
	
	public void trace(String msg, Object... e)
	{
		if(disable) return;
		Log4jStyle.print(ln -> ctrace(toJS(ln)), "TRACE", msg, e);
	}
	
	public void debug(String msg, Object... e)
	{
		if(disable) return;
		Log4jStyle.print(ln -> cdebug(toJS(ln)), "DEBUG", msg, e);
	}
	
	public void info(String msg, Object... e)
	{
		if(disable) return;
		Log4jStyle.print(ln -> cinf(toJS(ln)), "INFO", msg, e);
	}
	
	public void warn(String msg, Object... e)
	{
		if(disable) return;
		Log4jStyle.print(ln -> cwarn(toJS(ln)), "WARN", msg, e);
	}
	
	public void error(String msg, Object... e)
	{
		if(disable) return;
		Log4jStyle.print(ln -> cerr(toJS(ln)), "ERROR", msg, e);
	}
	
	private static JSObject toJS(String str)
	{
		return JS.wrap(str);
	}
	
	@JSBody(params = { "msg" }, script = "console.trace(msg)")
	public static native void ctrace(JSObject format);
	
	@JSBody(params = { "msg" }, script = "console.debug(msg)")
	public static native void cdebug(JSObject format);
	
	@JSBody(params = { "msg" }, script = "console.info(msg)")
	public static native void cinf(JSObject format);
	
	@JSBody(params = { "msg" }, script = "console.warn(msg)")
	public static native void cwarn(JSObject format);
	
	@JSBody(params = { "msg" }, script = "console.error(msg)")
	public static native void cerr(JSObject format);
}
