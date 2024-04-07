package org.zeith.hammeranims.core.js;

import jdk.nashorn.api.scripting.ClassFilter;
import org.zeith.hammeranims.HammerAnimations;

import javax.script.ScriptEngine;
import java.lang.reflect.Constructor;

public class NashornRelay
{
	private static boolean disabled = false;
	
	public static ScriptEngine tryCreateNashorn()
	{
		if(disabled) return null;
		
		try
		{
			ClassLoader cl = NashornRelay.class.getClassLoader();
			
			Class<?> ClassFilter = cl.loadClass("jdk.nashorn.api.scripting.ClassFilter");
			Class<?> NashornScriptEngineFactory = cl.loadClass("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
			Class<?> NoJSClasses = cl.loadClass(NashornRelay.class.getName() + "$NoJSClasses");
			
			Constructor<?> c = NoJSClasses.getDeclaredConstructor();
			c.setAccessible(true);
			
			Object sef = NashornScriptEngineFactory.getDeclaredConstructor().newInstance();
			Object filter = c.newInstance();
			
			return (ScriptEngine) NashornScriptEngineFactory.getMethod("getScriptEngine", ClassFilter).invoke(sef, filter);
		} catch(Throwable e)
		{
			HammerAnimations.LOG.warn("Failed to use \"jdk.nashorn.api.scripting.NashornScriptEngineFactory\". Passing off to other built-in factory;");
			e.printStackTrace();
			disabled = true;
			return null;
		}
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