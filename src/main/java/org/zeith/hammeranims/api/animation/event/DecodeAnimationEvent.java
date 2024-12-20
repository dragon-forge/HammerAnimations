package org.zeith.hammeranims.api.animation.event;

import org.zeith.hammeranims.standalone.utils.base.Cast;
import lombok.Getter;
import shaded.json.JSONArray;
import shaded.json.JSONObject;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import shaded.event.Cancelable;
import shaded.event.Event;

import java.util.*;

public class DecodeAnimationEvent
		extends Event
		implements Cancelable
{
	public final IResourceProvider resources;
	public final IAnimationContainer container;
	public final JSONObject rootJson;
	public final String formatVersion;
	
	public final String key;
	public final Object json;
	
	@Getter
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