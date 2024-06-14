package org.zeith.hammeranims.api.animation.event;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.zeith.hammeranims.api.animation.Animation;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.shaded.json.JSONArray;
import org.zeith.hammerlib.util.shaded.json.JSONObject;

import java.util.Objects;
import java.util.Optional;

public class DecodeAnimationEvent
		extends Event
		implements ICancellableEvent
{
	public final IResourceProvider resources;
	public final IAnimationContainer container;
	public final JSONObject rootJson;
	public final String formatVersion;
	
	public final String key;
	public final Object json;
	
	protected Animation decoded;
	
	public DecodeAnimationEvent(IResourceProvider resources, IAnimationContainer container, JSONObject rootJson, String formatVersion, String key, Object json)
	{
		this.resources = resources;
		this.container = container;
		this.rootJson = rootJson;
		this.formatVersion = formatVersion;
		this.key = key;
		this.json = json;
	}
	
	public void setDecoded(Animation decoded)
	{
		this.decoded = decoded;
		try
		{
			ICancellableEvent.super.setCanceled(true);
		} catch(UnsupportedOperationException e)
		{
		}
	}
	
	public Animation getDecoded()
	{
		return decoded;
	}
	
	@Override
	public void setCanceled(boolean cancel)
	{
	}
	
	public Optional<JSONObject> asObject()
	{
		return Cast.optionally(json, JSONObject.class);
	}
	
	public Optional<JSONArray> asArray()
	{
		return Cast.optionally(json, JSONArray.class);
	}
	
	public Optional<Number> asNumber()
	{
		return Cast.optionally(json, Number.class);
	}
	
	public String asString()
	{
		return Objects.toString(json);
	}
}