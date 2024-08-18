package org.zeith.hammeranims.core.js.parsers;

import com.zeitheron.hammercore.utils.ReflectionUtil;
import org.zeith.hammeranims.core.js.converters.IJsConverter;
import org.zeith.hammeranims.core.js.converters.fb.BindingsFallbackJsConverter;
import org.zeith.hammeranims.core.js.converters.fb.CastingFallbackJsConverter;

import javax.script.ScriptEngine;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

public class OpenJDKParser
		implements IJsParser
{
	private static final String[] DEFAULT_OPTIONS = new String[] { "-doe" };
	
	private final boolean found;
	private Method method;
	
	private Object engineFactory;
	private Object classFilter;
	
	public OpenJDKParser()
	{
		Class<?> engineFactoryType = ReflectionUtil.fetchClass("org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory");
		Class<?> classFilterType = ReflectionUtil.fetchClass("org.openjdk.nashorn.api.scripting.ClassFilter");
		if(engineFactoryType == null)
		{
			found = false;
			return;
		}
		
		try
		{
			engineFactory = engineFactoryType.getDeclaredConstructor().newInstance();
			method = engineFactoryType.getDeclaredMethod("getScriptEngine", String[].class, ClassLoader.class, classFilterType);
			
			classFilter = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { classFilterType }, (proxy, method, args) ->
			{
				if(method.getName().equals("exposeToScripts")) return false;
				return method.invoke(proxy, args);
			});
		} catch(Throwable e)
		{
			found = false;
			return;
		}
		
		found = true;
	}
	
	@Override
	public List<IJsConverter> getConverters()
	{
		return Arrays.asList(
				new CastingFallbackJsConverter(),
				new BindingsFallbackJsConverter()
		);
	}
	
	@Override
	public ScriptEngine create()
	{
		if(!found) return null;
		try
		{
			return (ScriptEngine) method.invoke(engineFactory, DEFAULT_OPTIONS, getClass().getClassLoader(), classFilter);
		} catch(Exception e)
		{
			return null;
		}
	}
}