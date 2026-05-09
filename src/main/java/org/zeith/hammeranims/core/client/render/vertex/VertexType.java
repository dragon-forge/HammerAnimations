package org.zeith.hammeranims.core.client.render.vertex;

import com.google.common.base.Suppliers;
import lombok.Getter;

import java.util.*;
import java.util.function.Supplier;

@Getter
public enum VertexType
{
	DEFAULT(""),
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
		return TYPE_MAP.get().getOrDefault(id, DEFAULT);
	}
	
	public static VertexType byId(String id, VertexType orDefault)
	{
		return TYPE_MAP.get().getOrDefault(id, orDefault);
	}
	
	public VertexType max(VertexType type)
	{
		return ordinal() > type.ordinal() ? this : type;
	}
	
	public static VertexType ofAlpha(int alpha)
	{
		return alpha == 0 ? CUTOUT : alpha < 255 ? TRANSLUCENT : SOLID;
	}
}