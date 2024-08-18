package org.zeith.hammeranims.core.js.parsers;

import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.core.js.converters.IJsConverter;
import org.zeith.hammeranims.core.js.converters.fb.BindingsFallbackJsConverter;
import org.zeith.hammeranims.core.js.converters.fb.CastingFallbackJsConverter;
import org.zeith.hammeranims.core.js.converters.j8.*;
import org.zeith.hammeranims.core.utils.MinecraftHelper;
import org.zeith.hammerlib.util.java.Cast;

import javax.script.ScriptEngine;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class Jdk8JsFactory
		implements IJsParser
{
	private boolean checked = false;
	private Supplier<ScriptEngine> theFactory = Cast.constant(null);
	
	@Override
	public List<IJsConverter> getConverters()
	{
		return Arrays.asList(
				new CastingFallbackJsConverter(),
				new DirectJ8ScriptObjectMirrorConverter(),
				new DirectJ8ScriptObjectMirrorConverterWithProxy(),
				new BindingsFallbackJsConverter(),
				new BindingsJ8ScriptObjectMirrorConverter()
		);
	}
	
	@Override
	public ScriptEngine create()
	{
		if(checked) return theFactory.get();
		checked = true;
		
		try
		{
			Class<?> ClassFilter = MinecraftHelper.fetchClass("jdk.nashorn.api.scripting.ClassFilter");
			Class<?> NashornScriptEngineFactory = MinecraftHelper.fetchClass("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
			Class<?> NoJSClasses = MinecraftHelper.fetchClass("org.zeith.hammeranims.core.js.parsers.NoJSClassesJ8");
			
			Constructor<?> c = NoJSClasses.getDeclaredConstructor();
			c.setAccessible(true);
			
			Constructor<?> sefCt = NashornScriptEngineFactory.getDeclaredConstructor();
			
			Method getScriptEngine = NashornScriptEngineFactory.getMethod("getScriptEngine", ClassFilter);
			
			Object sef = sefCt.newInstance();
			Object filter = c.newInstance();
			
			theFactory = () ->
			{
				try
				{
					return (ScriptEngine) getScriptEngine.invoke(sef, filter);
				} catch(IllegalAccessException | InvocationTargetException e)
				{
					throw new RuntimeException(e);
				}
			};
			
			return theFactory.get(); // verify we can create it at least once without crashing the game down the line!
		} catch(Throwable e)
		{
			HammerAnimations.LOG.warn("Failed to use \"jdk.nashorn.api.scripting.NashornScriptEngineFactory\".", e);
			theFactory = Cast.constant(null);
		}
		
		return theFactory.get();
	}
}
