package org.zeith.hammeranims.core.js.parsers;

import jdk.nashorn.api.scripting.ClassFilter;

class NoJSClassesJ8
		implements ClassFilter
{
	@Override
	public boolean exposeToScripts(String s)
	{
		return false;
	}
}
