package org.zeith.hammeranims.core.js;

import jdk.nashorn.api.scripting.ClassFilter;

public class NoJSClasses
		implements ClassFilter
{
	@Override
	public boolean exposeToScripts(String s)
	{
		return false;
	}
}