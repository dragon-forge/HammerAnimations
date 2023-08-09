package org.zeith.hammeranims.core.impl.api.geometry;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.data.IGeometryData;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.core.client.model.CubeUVs;

import java.util.*;

public class GeometryDataImpl
		implements IGeometryData
{
	protected final ResourceLocation key;
	
	protected final Map<String, BoneConfig> bones = new HashMap<>();
	public final Map<String, BoneConfig> bonesView = Collections.unmodifiableMap(bones);
	
	protected int textureWidth, textureHeight;
	
	protected final IGeometryContainer container;
	
	public GeometryDataImpl(ResourceLocation key, IGeometryContainer container)
	{
		this.key = key;
		this.container = container;
	}
	
	@Override
	public int getTextureWidth()
	{
		return textureWidth;
	}
	
	@Override
	public int getTextureHeight()
	{
		return textureHeight;
	}
	
	@Override
	public Set<String> getBones()
	{
		return bonesView.keySet();
	}
	
	@Override
	public IGeometricModel createModel()
	{
		return HammerAnimations.PROXY.createGeometryData(this);
	}
	
	@Override
	public IGeometryContainer getContainer()
	{
		return container;
	}
	
	public static class BoneConfig
	{
		protected String parent;
		public final String name;
		public final Vec3 pivot;
		
		public final List<CubeConfig> cubes = Lists.newArrayList();
		
		public BoneConfig(String name, String parent, Vec3 pivot)
		{
			this.name = name;
			this.parent = parent;
			this.pivot = pivot;
		}
		
		public String getParent()
		{
			return parent;
		}
	}
	
	public static class CubeConfig
	{
		public final Vec3 origin;
		public final Vec3 size;
		public final CubeUVs uvs;
		public final float inflate;
		public final boolean flipX;
		
		public CubeConfig(Vec3 origin, Vec3 size, CubeUVs uvs, float inflate, boolean flipX)
		{
			this.origin = origin;
			this.size = size;
			this.uvs = uvs;
			this.inflate = inflate;
			this.flipX = flipX;
		}
	}
}