package org.zeith.hammeranims.core.impl.api.geometry;

import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.data.IGeometryData;
import org.zeith.hammeranims.api.geometry.event.DecodeGeometryEvent;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammerlib.util.shaded.json.*;

import java.util.Optional;
import java.util.function.Supplier;

public class GeometryContainerImpl
		implements IGeometryContainer
{
	protected IGeometryData geometry = IGeometryData.EMPTY(this);
	
	public final String suffix;
	
	public GeometryContainerImpl(String suffix)
	{
		this.suffix = suffix;
	}
	
	public GeometryContainerImpl()
	{
		this.suffix = ".geo.json";
	}
	
	public static Optional<IGeometryData> defaultReadGeometry(ResourceLocation path, IResourceProvider resources, IGeometryContainer container, Optional<String> text)
	{
		return text.map(txt ->
		{
			ResourceLocation key = container.getRegistryKey();
			
			try
			{
				Supplier<JSONObject> json = Suppliers.memoize(() -> (JSONObject) new JSONTokener(txt).nextValue());
				Supplier<Object> geometry = Suppliers.memoize(() -> json.get().get("minecraft:geometry"));
				Supplier<String> fmt = Suppliers.memoize(() -> json.get().getString("format_version"));
				
				DecodeGeometryEvent evt = new DecodeGeometryEvent(path, resources, container, json, fmt, geometry, txt);
				HammerAnimationsApi.EVENT_BUS.post(evt);
				
				return evt.getDecoded();
			} catch(Exception e)
			{
				HammerAnimations.LOG.error("Failed to load geometry " + key + ", skipping.", e);
				return null;
			}
		});
	}
	
	@Override
	public void reload(IResourceProvider resources)
	{
		ResourceLocation key = getRegistryKey();
		
		ResourceLocation path = new ResourceLocation(key.getNamespace(),
				"bedrock/geometry/" + key.getPath() + suffix
		);
		
		geometry = Optional.ofNullable(defaultReadGeometry(path, resources, this, resources.readAsString(path)).orElseGet(() ->
		{
			HammerAnimations.LOG.warn("Unable to load geometry {} from file {}", key, path);
			return null;
		})).orElse(IGeometryData.EMPTY(this));
		
		HammerAnimations.LOG.debug("Loaded {} geometry with {} bones.", key, geometry.getBones().size());
	}
	
	@Override
	public IGeometryData getGeometry()
	{
		return geometry;
	}
}