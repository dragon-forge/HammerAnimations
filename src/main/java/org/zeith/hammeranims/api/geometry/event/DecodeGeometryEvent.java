package org.zeith.hammeranims.api.geometry.event;

import com.zeitheron.hammercore.lib.zlib.json.*;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraftforge.fml.common.eventhandler.*;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.data.IGeometryData;
import org.zeith.hammeranims.api.utils.IResourceProvider;

import java.util.*;

@Cancelable
public class DecodeGeometryEvent
		extends Event
{
	public final IResourceProvider resources;
	public final IGeometryContainer container;
	public final JSONObject rootJson;
	public final String formatVersion;
	public final Object json;
	
	protected IGeometryData decoded;
	
	public DecodeGeometryEvent(IResourceProvider resources, IGeometryContainer container, JSONObject rootJson, String formatVersion, Object json)
	{
		this.resources = resources;
		this.container = container;
		this.rootJson = rootJson;
		this.formatVersion = formatVersion;
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
	
	public IGeometryData getDecoded()
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
