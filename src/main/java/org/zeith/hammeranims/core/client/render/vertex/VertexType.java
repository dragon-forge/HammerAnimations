package org.zeith.hammeranims.core.client.render.vertex;

import lombok.Getter;
import org.zeith.hammeranims.standalone.utils.Suppliers;

import java.util.*;
import java.util.function.Supplier;

@Getter
public enum VertexType
{
	DIRECT(""),
	SOLID("solid"),
	CUTOUT("cutout"),
	TRANSLUCENT("translucent");
	
	private final String id;
	
	VertexType(String id)
	{
		this.id = id;
	}
	
	static final Supplier<Map<String, VertexType>> TYPE_MAP = Suppliers.memoize(() ->
	{
		Map<String, VertexType> vts = new HashMap<>();
		for(VertexType value : values()) vts.put(value.getId(), value);
		return Collections.unmodifiableMap(vts);
	});
	
	public static VertexType byId(String id)
	{
		return TYPE_MAP.get().getOrDefault(id, DIRECT);
	}
	
	public static VertexType byId(String id, VertexType orDefault)
	{
		return TYPE_MAP.get().getOrDefault(id, orDefault);
	}
}