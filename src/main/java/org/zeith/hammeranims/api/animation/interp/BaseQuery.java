package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.exception.LzVMOperationNotSupportedException;
import dev.zeith.lzvm.op.*;
import lombok.Setter;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.*;
import org.teavm.jso.impl.JS;

import java.util.*;

public abstract class BaseQuery
		implements IVariableAccess
{
	protected final Map<String, LzVarOp> vars = new HashMap<>();
	
	public BaseQuery()
	{
		registerVariables(IVariableRegistrar.of(this));
	}
	
	protected void registerVariables(IVariableRegistrar reg)
	{
	}
	
	public ReadWriteVariable getOrCreateRWVar(String name, double defaultValue)
	{
		return (ReadWriteVariable) vars.computeIfAbsent(name, l ->
				{
					ReadWriteVariable v = new ReadWriteVariable();
					v.val = defaultValue;
					return v;
				}
		);
	}
	
	@Override
	public LzVarOp findVar(String name)
	{
		return vars.computeIfAbsent(name, l -> new ReadWriteVariable());
	}
	
	@Override
	public void setVariable(String name, LzVarOp value)
	{
		vars.put(name, value);
	}
	
	@Override
	public JSObject toJS()
	{
		JSObject map = JSObjects.create();
		for(Map.Entry<String, LzVarOp> e : vars.entrySet())
			JS.set(map, JSString.valueOf(e.getKey()), JSNumber.valueOf(e.getValue().get()));
		return map;
	}
}