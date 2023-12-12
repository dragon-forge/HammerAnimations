package org.zeith.hammeranims.api.animsys;

import com.zeitheron.hammercore.net.*;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.geometry.model.GeometryPose;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.net.*;

import javax.annotation.*;
import java.util.*;
import java.util.function.Predicate;

import static org.zeith.hammeranims.core.utils.InstanceHelpers.*;

public class AnimationSystem
		implements ICompoundSerializable
{
	@Nonnull
	public final IAnimatedObject owner;
	
	protected boolean hasTicked = false;
	
	protected double time;
	
	public boolean canSync = true, autoSync = false;
	
	protected final AnimationLayer[] layers;
	protected final Map<String, AnimationLayer> layerMap;
	
	public AnimationSystem(@Nonnull IAnimatedObject owner, AnimationLayer[] layers, Map<String, AnimationLayer> layerMap)
	{
		this.owner = owner;
		this.layers = layers;
		this.layerMap = Collections.unmodifiableMap(layerMap);
	}
	
	public IPacket createSyncPacket()
	{
		return new PacketSyncAnimationSystem(this);
	}
	
	public void sync()
	{
		if(!owner.getAnimatedObjectWorld().isRemote && canSync) // if on server
			HCNet.INSTANCE.sendToAllAroundTracking(
					createSyncPacket(),
					HCNet.point(
							owner.getAnimatedObjectWorld(),
							owner.getAnimatedObjectPosition(),
							256
					)
			);
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
	
	public Set<String> getLayerNames()
	{
		return layerMap.keySet();
	}
	
	public AnimationLayer[] getLayers()
	{
		return layers;
	}
	
	public Set<Map.Entry<String, AnimationLayer>> entrySet()
	{
		return layerMap.entrySet();
	}
	
	public void tick()
	{
		if(!hasTicked)
		{
			hasTicked = true;
			if(canSync && owner.getAnimatedObjectWorld().isRemote) // Request animations from server on load
				HCNet.INSTANCE.sendToServer(new PacketRequestAnimationSystemSync(this));
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
	
	public void applyAnimation(float partialTicks, GeometryPose pose, Predicate<AnimationLayer> layerMask)
	{
		double sysTime = getTime(partialTicks);
		for(AnimationLayer layer : layers)
			if(layerMask.test(layer))
				layer.applyAnimation(sysTime, partialTicks, pose);
	}
	
	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound comp = newNBTCompound();
		comp.setDouble("Time", time);
		
		NBTTagList layers = newNBTList();
		for(AnimationLayer layer : this.layers)
			if(layer.persistent) // save only persistent layers
				layers.appendTag(layer.serializeNBT());
		comp.setTag("Layers", layers);
		
		return comp;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		time = nbt.getDouble("Time");
		
		NBTTagList layers = nbt.getTagList("Layers", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < layers.tagCount(); i++)
		{
			NBTTagCompound tag = layers.getCompoundTagAt(i);
			AnimationLayer l = layerMap.get(tag.getString("Name"));
			if(l != null && l.persistent) l.deserializeNBT(tag);
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