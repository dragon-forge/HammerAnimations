package org.zeith.hammeranims.api.animsys;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.geometry.model.GeometryPose;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.net.*;
import org.zeith.hammerlib.net.Network;

import javax.annotation.*;
import java.util.*;

import static org.zeith.hammeranims.core.utils.InstanceHelpers.*;

public class AnimationSystem
		implements ICompoundSerializable
{
	@Nonnull
	public final IAnimatedObject owner;
	
	protected double time;
	
	protected boolean hasTicked = false;
	
	public boolean canSync = true, autoSync = false;
	
	protected final AnimationLayer[] layers;
	protected final Map<String, AnimationLayer> layerMap;
	
	public AnimationSystem(@Nonnull IAnimatedObject owner, AnimationLayer[] layers, Map<String, AnimationLayer> layerMap)
	{
		this.owner = owner;
		this.layers = layers;
		this.layerMap = Collections.unmodifiableMap(layerMap);
	}
	
	public void sync()
	{
		if(!owner.getAnimatedObjectWorld().isClientSide && canSync) // if on server
			Network.sendToTracking(
					new PacketSyncAnimationSystem(this),
					owner.getAnimatedObjectWorld().getChunkAt(BlockPos.containing(owner.getAnimatedObjectPosition()))
			);
	}
	
	@Nullable
	public AnimationLayer getLayer(String name)
	{
		return layerMap.get(name);
	}
	
	public AnimationLocation activeAnimationLocation(String layer)
	{
		var l = getLayer(layer);
		return l == null ? DefaultsHA.NULL_ANIM.getLocation() : l.activeAnimationLocation();
	}
	
	public boolean isActiveAnimation(String layer, IAnimationSource source)
	{
		var al = activeAnimationLocation(layer);
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
	
	public Set<String> getLayerNames()
	{
		return layerMap.keySet();
	}
	
	public AnimationLayer[] getLayers()
	{
		return layers;
	}
	
	public void tick()
	{
		if(!hasTicked)
		{
			hasTicked = true;
			if(canSync && owner.getAnimatedObjectWorld().isClientSide()) // Request animations from server on load
				Network.sendToServer(new PacketRequestAnimationSystemSync(this));
		}
		
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
	
	@Override
	public CompoundTag serializeNBT()
	{
		var comp = newNBTCompound();
		comp.putDouble("Time", time);
		
		var layers = newNBTList();
		for(AnimationLayer layer : this.layers) layers.add(layer.serializeNBT());
		comp.put("Layers", layers);
		
		return comp;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		time = nbt.getDouble("Time");
		
		var layers = nbt.getList("Layers", Tag.TAG_COMPOUND);
		for(int i = 0; i < layers.size(); i++)
		{
			var tag = layers.getCompound(i);
			AnimationLayer l = layerMap.get(tag.getString("Name"));
			if(l != null) l.deserializeNBT(tag);
		}
	}
	
	public static AnimationSystem create(IAnimatedObject obj)
	{
		Builder builder = AnimationSystem.builder(obj);
		obj.setupSystem(builder);
		return builder.build();
	}
	
	public static Builder builder(@Nonnull IAnimatedObject owner)
	{
		return new Builder(Objects.requireNonNull(owner));
	}
	
	public static class Builder
	{
		@Nonnull
		protected final IAnimatedObject owner;
		protected boolean canSync = true;
		protected boolean autoSync = false;
		protected final List<AnimationLayer.Builder> layers = new ArrayList<>();
		
		public Builder(@Nonnull IAnimatedObject owner)
		{
			this.owner = owner;
		}
		
		public Builder addLayers(AnimationLayer.Builder... layers)
		{
			this.layers.addAll(Arrays.asList(layers));
			return this;
		}
		
		public Builder disableSync()
		{
			canSync = false;
			return this;
		}
		
		public Builder autoSync()
		{
			autoSync = true;
			return this;
		}
		
		public Builder autoSync(boolean autoSync)
		{
			this.autoSync = autoSync;
			return this;
		}
		
		public AnimationSystem build()
		{
			AnimationLayer[] layers = new AnimationLayer[this.layers.size()];
			Map<String, AnimationLayer> layerMap = new HashMap<>();
			AnimationSystem sys = new AnimationSystem(owner, layers, layerMap);
			for(int i = 0; i < layers.length; i++)
			{
				AnimationLayer al = layers[i] = this.layers.get(i).build(sys);
				layerMap.put(al.name, al);
			}
			sys.canSync = canSync;
			sys.autoSync = autoSync;
			return sys;
		}
	}
}