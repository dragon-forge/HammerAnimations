package org.zeith.hammeranims.core.impl.api.geometry;

import com.zeitheron.hammercore.lib.zlib.json.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.data.IGeometryData;
import org.zeith.hammeranims.api.geometry.event.DecodeGeometryEvent;
import org.zeith.hammeranims.api.utils.IResourceProvider;

import java.util.Optional;

public class GeometryContainerImpl
		extends IForgeRegistryEntry.Impl<IGeometryContainer>
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
	
	public static Optional<IGeometryData> defaultReadGeometry(IResourceProvider resources, IGeometryContainer container, Optional<String> text)
	{
		return text.map(JSONTokener::new)
				.flatMap(JSONTokener::nextValueOBJ)
				.map(json ->
				{
					ResourceLocation key = container.getRegistryKey();
					
					try
					{
						JSONArray geometry = json.getJSONArray("minecraft:geometry");
						String fmt = json.getString("format_version");
						
						DecodeGeometryEvent evt = new DecodeGeometryEvent(resources, container, json, fmt, geometry);
						GeometryDecoder.decodeGeometry(evt);
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
		
		geometry = Optional.ofNullable(defaultReadGeometry(resources, this, resources.readAsString(path)).orElseGet(() ->
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