package org.zeith.hammeranims.molang.runtime;

import com.google.common.collect.Iterators;
import lombok.Getter;
import org.zeith.hammeranims.api.animation.interp.IVariableStorage;
import org.zeith.hammeranims.molang.runtime.struct.*;
import org.zeith.hammeranims.molang.runtime.value.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MoLangEnvironment
		implements MoValue, IVariableStorage
{
	public static final String QUERY = "query";
	public static final String MATH = "math";
	public static final String VARIABLE = "variable";
	public static final String TEMP = "temp";
	public static final String CONTEXT = "context";
	public static final String ARRAY = "array";
	
	public QueryStruct math = null;
	public QueryStruct query = null;
	public VariableStruct variable = null;
	public VariableStruct temp = null;
	public ContextStruct context = null;
	public ArrayStruct array = null;
	
	public final MoParams emptyParams = new MoParams(this, List.of());
	
	@Getter
	protected final Map<String, MoStruct> structs;
	
	public MoLangEnvironment(boolean concurrent)
	{
		this.structs = concurrent ? new ConcurrentHashMap<>() : new HashMap<>();
		
		setStruct(MoLangEnvironment.MATH, MoLangMath.init(new QueryStruct(this, new HashMap<>())));
		setStruct(MoLangEnvironment.TEMP, new VariableStruct(this));
		setStruct(MoLangEnvironment.VARIABLE, new VariableStruct(this));
		setStruct(MoLangEnvironment.ARRAY, new ArrayStruct(this));
		setStruct(MoLangEnvironment.QUERY, new QueryStruct(this, concurrent ? new ConcurrentHashMap<>() : new HashMap<>()));
	}
	
	public void setStruct(String name, MoStruct struct)
	{
		switch(name)
		{
			case MATH:
				this.math = (QueryStruct) struct;
				break;
			case QUERY:
			case "q":
				this.query = (QueryStruct) struct;
				break;
			case VARIABLE:
			case "v":
				this.variable = (VariableStruct) struct;
				break;
			case TEMP:
				this.temp = (VariableStruct) struct;
				break;
			case CONTEXT:
				this.context = (ContextStruct) struct;
				break;
			case ARRAY:
				this.array = (ArrayStruct) struct;
				break;
		}
		structs.put(name, struct);
	}
	
	public MoStruct getStruct(String name)
	{
		switch(name)
		{
			case MATH:
				return this.math;
			case QUERY:
			case "q":
				return this.query;
			case VARIABLE:
			case "v":
				return this.variable;
			case TEMP:
				return this.temp;
			case CONTEXT:
				return this.context;
			case ARRAY:
				return this.array;
			default:
				return this.structs.get(name);
		}
	}
	
	public MoValue getValue(Iterator<String> names)
	{
		return getValue(names, emptyParams);
	}
	
	public MoValue getValue(Iterator<String> names, MoParams params)
	{
		String main = names.next();
		MoStruct struct = this.getStruct(main);
		return struct != null ? struct.get(names, params) : new DoubleValue((double) 0.0F);
	}
	
	public void setValue(Iterator<String> names, MoValue value)
	{
		String main = names.next();
		MoStruct struct = this.getStruct(main);
		if(struct != null) struct.set(names, value);
	}
	
	public void setValue(String name, MoValue value)
	{
		setValue(Iterators.forArray(name.split("\\.")), value);
	}
	
	@Override
	public Object value()
	{
		return this;
	}
	
	@Override
	public void store(String key, double value)
	{
		setValue(key, MoValue.of(value));
	}
	
	@Override
	public void store(String key, String value)
	{
		setValue(key, MoValue.of(value));
	}
	
	@Override
	public void store(String[] key, double value)
	{
		setValue(Iterators.forArray(key), MoValue.of(value));
	}
	
	@Override
	public void store(String[] key, String value)
	{
		setValue(Iterators.forArray(key), MoValue.of(value));
	}
}