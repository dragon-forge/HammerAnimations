package org.zeith.hammeranims.core.js;

import com.zeitheron.hammercore.utils.ReflectionUtil;
import com.zeitheron.hammercore.utils.base.Cast;
import jdk.nashorn.api.scripting.ClassFilter;
import org.zeith.hammeranims.HammerAnimations;

import javax.script.ScriptEngine;
import java.lang.reflect.*;
import java.util.function.Supplier;

public class NashornRelay
{
	private static boolean checked = false;
	private static Supplier<ScriptEngine> theFactory = Cast.constant(null);
	
	public static ScriptEngine tryCreateNashorn()
	{
		if(checked) return theFactory.get();
		checked = true;
		
		try
		{
			Class<?> ClassFilter = ReflectionUtil.fetchClass("jdk.nashorn.api.scripting.ClassFilter");
			Class<?> NashornScriptEngineFactory = ReflectionUtil.fetchClass("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
			Class<?> NoJSClasses = ReflectionUtil.fetchClass("org.zeith.hammeranims.core.js.NoJSClasses");
			
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
			HammerAnimations.LOG.warn("Failed to use \"jdk.nashorn.api.scripting.NashornScriptEngineFactory\". Passing off to other built-in factory;", e);
			theFactory = Cast.constant(null);
		}
		
		return theFactory.get();
	}
	
	private static class NoJSClasses
			implements ClassFilter
	{
		@Override
		public boolean exposeToScripts(String s)
		{
			return false;
		}
	}
}