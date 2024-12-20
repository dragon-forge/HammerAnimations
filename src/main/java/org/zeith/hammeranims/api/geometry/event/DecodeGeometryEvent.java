package org.zeith.hammeranims.api.geometry.event;

import org.zeith.hammeranims.standalone.utils.base.Cast;
import lombok.Getter;
import shaded.util.ResourceLocation;
import shaded.event.Cancelable;
import shaded.event.Event;
import shaded.json.JSONArray;
import shaded.json.JSONObject;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.data.IGeometryData;
import org.zeith.hammeranims.api.utils.IResourceProvider;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class DecodeGeometryEvent
		extends Event
		implements Cancelable
{
	public final ResourceLocation path;
	public final IResourceProvider resources;
	public final IGeometryContainer container;
	
	public final Supplier<JSONObject> rootJson;
	public final Supplier<String> formatVersion;
	public final Supplier<Object> json;
	public final String text;
	
	@Getter
	protected IGeometryData decoded;
	
	public DecodeGeometryEvent(ResourceLocation path, IResourceProvider resources, IGeometryContainer container, Supplier<JSONObject> rootJson, Supplier<String> formatVersion, Supplier<Object> json, String text)
	{
		this.path = path;
		this.resources = resources;
		this.container = container;
		this.rootJson = rootJson;
		this.formatVersion = formatVersion;
		this.text = text;
		this.json = json;
	}
	
	public void setDecoded(IGeometryData decoded)
	{
		this.decoded = decoded;
		try
		{
			super.setCanceled(true);
		} catch(UnsupportedOperationException e)
		{
		}
	}
	
	@Override
	public void setCanceled(boolean cancel)
	{
	}
	
	public Optional<JSONObject> asObject()
	{
		return Cast.optionally(json.get(), JSONObject.class);
	}
	
	public Optional<JSONArray> asArray()
	{
		return Cast.optionally(json.get(), JSONArray.class);
	}
	
	public Optional<Number> asNumber()
	{
		return Cast.optionally(json.get(), Number.class);
	}
	
	public String asString()
	{
		return Objects.toString(json.get());
	}
}
