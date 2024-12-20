package org.zeith.hammeranims.core.impl.api.geometry;

import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.constrains.IGeometryConstraints;
import org.zeith.hammeranims.api.geometry.data.IGeometryData;
import org.zeith.hammeranims.api.geometry.event.DecodeGeometryEvent;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.impl.api.geometry.constrains.BoneConstraintsImpl;
import org.zeith.hammeranims.core.impl.api.geometry.constrains.GeometryConstrainsImpl;
import org.zeith.hammeranims.core.impl.api.geometry.decoder.GsonGeometryDecoder;
import org.zeith.hammeranims.standalone.utils.Suppliers;
import shaded.json.JSONObject;
import shaded.json.JSONTokener;
import shaded.util.ResourceLocation;

import java.util.*;
import java.util.function.Supplier;

public class GeometryContainerImpl
		implements IGeometryContainer
{
	protected GeometryConstrainsImpl constraints = new GeometryConstrainsImpl(Collections.emptyMap());
	protected IGeometryData geometry = IGeometryData.EMPTY(this);
	
	public final String suffix, constraintsSuffix;
	
	public GeometryContainerImpl(String suffix)
	{
		this.constraintsSuffix = ".constraints.json";
		this.suffix = suffix;
	}
	
	public GeometryContainerImpl()
	{
		this.constraintsSuffix = ".constraints.json";
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
				GeometryDecoder.decodeGeometry(evt);
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
		
		ResourceLocation constraintsPath = new ResourceLocation(key.getNamespace(),
				"bedrock/geometry/" + key.getPath() + constraintsSuffix
		);
		
		geometry = Optional.ofNullable(defaultReadGeometry(path, resources, this, resources.readAsString(path)).orElseGet(() ->
		{
			HammerAnimations.LOG.warn("Unable to load geometry {} from file {}", key, path);
			return null;
		})).orElse(IGeometryData.EMPTY(this));
		
		constraints = resources.readAsString(constraintsPath).map(GsonGeometryDecoder::readGeometryConstraints)
				.orElse(null);
		boolean constraintsLoaded = constraints != null;
		if(constraints == null)
		{
			Map<String, BoneConstraintsImpl> bones = new HashMap<>();
			for(String bone : geometry.getBones())
				bones.put(bone, new BoneConstraintsImpl());
			constraints = new GeometryConstrainsImpl(Collections.unmodifiableMap(bones));
		}
		
		if(HammerAnimationsApi.LOG_RELOADS)
			HammerAnimations.LOG.debug("Loaded {} geometry with {} bones{}.",
					key,
					geometry.getBones().size(),
					constraintsLoaded ? " and " + constraints.getBones().size() + " constraints" : ""
			);
	}
	
	@Override
	public IGeometryData getGeometry()
	{
		return geometry;
	}
	
	@NotNull
	@Override
	public IGeometryConstraints getConstraints()
	{
		return constraints;
	}
	
	ResourceLocation id;
	
	@Override
	public ResourceLocation getRegistryKey()
	{
		return id;
	}
	
	@Override
	public void setRegistryKey(ResourceLocation id)
	{
		this.id = id;
	}
}