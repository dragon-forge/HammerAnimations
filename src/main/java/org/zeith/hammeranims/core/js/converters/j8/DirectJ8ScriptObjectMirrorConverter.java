package org.zeith.hammeranims.core.js.converters.j8;

import com.zeitheron.hammercore.utils.base.Cast;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.zeith.hammeranims.core.js.converters.IJsConverter;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class DirectJ8ScriptObjectMirrorConverter
		implements IJsConverter
{
	@Override
	public <T> T parseAsInterface(ScriptEngine engine, String eval, Class<T> targetItf)
			throws ScriptException
	{
		ScriptObjectMirror mirror = Cast.cast(engine.eval(eval), ScriptObjectMirror.class);
		if(mirror == null) return null;
		return mirror.to(targetItf);
	}
}