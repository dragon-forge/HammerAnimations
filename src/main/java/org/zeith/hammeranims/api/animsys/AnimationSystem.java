package org.zeith.hammeranims.api.animsys;

import lombok.*;
import org.jetbrains.annotations.*;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.interp.Query;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.model.GeometryPose;
import org.zeith.hammeranims.core.init.DefaultsHA;

import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.*;

public class AnimationSystem
{
	@NotNull
	public final IAnimatedObject owner;
	
	@NotNull
	public final Query query;
	
	@Setter
	protected double time;
	
	public boolean canSync = true, autoSync = false, syncTime = true;
	
	@Getter
	protected final AnimationLayer[] layers;
	protected final Map<String, AnimationLayer> layerMap;
	
	@Setter
	@Getter
	@Nullable
	protected IGeometryContainer geometry;
	
	public AnimationSystem(@NotNull IAnimatedObject owner, AnimationLayer[] layers, Map<String, AnimationLayer> layerMap)
	{
		this.owner = owner;
		this.layers = layers;
		this.layerMap = Collections.unmodifiableMap(layerMap);
		this.query = Objects.requireNonNull(owner.createQuery(), "owner.createQuery()");
	}
	
	@Nullable
	public AnimationLayer getLayer(String name)
	{
		return layerMap.get(name);
	}
	
	public AnimationLocation activeAnimationLocation(String layer)
	{
		AnimationLayer l = getLayer(layer);
		return l == null ? DefaultsHA.NULL_ANIM.getLocation() : l.activeAnimationLocation();
	}
	
	public boolean isActiveAnimation(String layer, IAnimationSource source)
	{
		AnimationLocation al = activeAnimationLocation(layer);
		return Objects.equals(al, source.getLocation());
	}
	
	public boolean startAnimationAt(String layer, IAnimationSource animation)
	{
		return startAnimationAt(layer, animation.configure());
	}
	
	public boolean startAnimationAt(String layer, ConfiguredAnimation animation)
	{
		AnimationLayer l = layerMap.get(layer);
		if(l != null)
			return l.startAnimation(animation);
		return false;
	}
	
	public boolean stopAnimation(String layer)
	{
		return startAnimationAt(layer, ConfiguredAnimation.noAnimation());
	}
	
	public boolean stopAnimation(String layer, float transitionTime)
	{
		return startAnimationAt(layer, ConfiguredAnimation.noAnimation().transitionTime(transitionTime));
	}
	
	public boolean stopAnimation(String layer, Duration transitionTime)
	{
		return startAnimationAt(layer, ConfiguredAnimation.noAnimation().transitionTime(transitionTime));
	}
	
	public Set<String> getLayerNames()
	{
		return layerMap.keySet();
	}
	
	public Set<Map.Entry<String, AnimationLayer>> entrySet()
	{
		return layerMap.entrySet();
	}
	
	public void tick()
	{
		time += 0.05; // add a tick
		for(AnimationLayer layer : layers)
			layer.tick(time);
	}
	
	public void resetTime()
	{
		time = 0;
		for(AnimationLayer layer : layers)
			layer.startTime = 0;
	}
	
	public double getTime(float partialTicks)
	{
		return time + 0.05 * partialTicks;
	}
	
	public void applyAnimation(float partialTicks, GeometryPose pose)
	{
		double sysTime = getTime(partialTicks);
		for(AnimationLayer layer : layers)
			layer.applyAnimation(sysTime, partialTicks, pose);
	}
	
	public void applyAnimation(float partialTicks, GeometryPose pose, Predicate<AnimationLayer> layerMask)
	{
		double sysTime = getTime(partialTicks);
		for(AnimationLayer layer : layers)
			if(layerMask.test(layer))
				layer.applyAnimation(sysTime, partialTicks, pose);
	}
	
	public static AnimationSystem create(IAnimatedObject obj)
	{
		Builder builder = AnimationSystem.builder(obj);
		obj.setupSystem(builder);
		return builder.build();
	}
	
	public static Builder builder(@NotNull IAnimatedObject owner)
	{
		return new Builder(Objects.requireNonNull(owner));
	}
	
	public static class Builder
	{
		@NotNull
		protected final IAnimatedObject owner;
		protected boolean canSync = true;
		protected boolean autoSync = false;
		protected boolean syncTime = true;
		protected final List<AnimationLayer.Builder> layers = new ArrayList<>();
		protected IGeometryContainer geometry = DefaultsHA.NULL_GEOMETRY;
		
		public Builder(@NotNull IAnimatedObject owner)
		{
			this.owner = owner;
		}
		
		public Builder addLayers(AnimationLayer.Builder... layers)
		{
			this.layers.addAll(Arrays.asList(layers));
			return this;
		}
		
		public Builder addLayers(String... names)
		{
			this.layers.addAll(Stream.of(names).map(AnimationLayer::builder).collect(Collectors.toList()));
			return this;
		}
		
		public Builder disableSync()
		{
			canSync = false;
			autoSync = false;
			return this;
		}
		
		public Builder autoSync()
		{
			autoSync = true;
			return this;
		}
		
		public Builder canSync(boolean canSync)
		{
			this.canSync = canSync;
			return this;
		}
		
		public Builder syncTime(boolean syncTime)
		{
			this.syncTime = syncTime;
			return this;
		}
		
		public Builder autoSync(boolean autoSync)
		{
			this.autoSync = autoSync;
			return this;
		}
		
		public Builder geometry(IGeometryContainer geometry)
		{
			this.geometry = geometry;
			return this;
		}
		
		public AnimationSystem build()
		{
			AnimationLayer[] layers = new AnimationLayer[this.layers.size()];
			Map<String, AnimationLayer> layerMap = new HashMap<>();
			AnimationSystem sys = new AnimationSystem(owner, layers, layerMap);
			sys.canSync = canSync;
			sys.autoSync = autoSync;
			sys.syncTime = syncTime;
			for(int i = 0; i < layers.length; i++)
			{
				AnimationLayer al = layers[i] = this.layers.get(i).build(sys);
				layerMap.put(al.name, al);
			}
			return sys;
		}
	}
}