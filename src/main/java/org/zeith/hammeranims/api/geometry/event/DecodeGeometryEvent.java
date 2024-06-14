package org.zeith.hammeranims.api.geometry.event;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.data.IGeometryData;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.shaded.json.JSONArray;
import org.zeith.hammerlib.util.shaded.json.JSONObject;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class DecodeGeometryEvent
		extends Event
		implements ICancellableEvent
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
			ICancellableEvent.super.setCanceled(true);
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
