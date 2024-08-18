package org.zeith.hammeranims.core.js.parsers;

import org.zeith.hammeranims.core.js.converters.IJsConverter;

import javax.script.ScriptEngine;
import java.util.List;

public interface IJsParser
{
	List<IJsConverter> getConverters();
	
	ScriptEngine create();
}