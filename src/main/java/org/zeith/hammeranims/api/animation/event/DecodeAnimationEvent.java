package org.zeith.hammeranims.api.animation.event;

import com.zeitheron.hammercore.lib.zlib.json.*;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraftforge.fml.common.eventhandler.*;
import org.zeith.hammeranims.api.animation.*;

import java.util.*;

@Cancelable
public class DecodeAnimationEvent
		extends Event
{
	public final IAnimationContainer container;
	public final JSONObject rootJson;
	public final String formatVersion;
	
	public final String key;
	public final Object json;
	
	protected Animation decoded;
	
	public DecodeAnimationEvent(IAnimationContainer container, JSONObject rootJson, String formatVersion, String key, Object json)
	{
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