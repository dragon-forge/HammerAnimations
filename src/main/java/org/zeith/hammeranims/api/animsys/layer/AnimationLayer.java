package org.zeith.hammeranims.api.animsys.layer;

import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.api.animsys.actions.*;
import org.zeith.hammeranims.api.geometry.model.GeometryPose;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import javax.annotation.*;
import java.util.Objects;

public class AnimationLayer
		implements ICompoundSerializable
{
	public final AnimationSystem system;
	public final boolean allowAutoSync;
	public final boolean persistent;
	public final ILayerMask mask;
	public final Query query;
	public final String name;
	public final BlendMode mode;
	
	public ActiveAnimation lastAnimation;
	
	public double startTime;
	public ActiveAnimation currentAnimation;
	
	public float weight = 1F;
	
	public boolean frozen;
	
	public AnimationLayer(AnimationSystem sys, ILayerMask mask, Query query, String name, BlendMode mode, boolean allowAutoSync, boolean persistent)
	{
		this.system = sys;
		this.mask = mask;
		this.query = query;
		this.name = name;
		this.mode = mode;
		this.allowAutoSync = allowAutoSync;
		this.persistent = persistent;
	}
	
	@Nullable
	public ActiveAnimation getCurrentAnimation()
	{
		return currentAnimation;
	}
	
	public AnimationLocation activeAnimationLocation()
	{
		return currentAnimation != null ? currentAnimation.getLocation() : DefaultsHA.NULL_ANIM.get().getLocation();
	}
	
	public Animation activeAnimation()
	{
		return currentAnimation != null ? currentAnimation.config.animation : DefaultsHA.NULL_ANIM.get();
	}
	
	public boolean startAnimation(@Nonnull IAnimationSource animation)
	{
		return startAnimation(animation.configure());
	}
	
	public boolean startAnimation(@Nonnull ConfiguredAnimation animation)
	{
		check:
		{
			if(animation.important) break check;
			
			if(currentAnimation != null)
			{
				ConfiguredAnimation cur = currentAnimation.config;
				if(cur.same(animation)) return false;
				else break check;
			}
			
			// Same animation as previously -- skip.
			if(Objects.equals(animation.getLocation(),
					currentAnimation != null ? currentAnimation.getLocation() : null
			)) return false;
		}
		
		lastAnimation = currentAnimation;
		startTime = system.getTime(0);
		currentAnimation = animation.activate(this);
		
		if(system.autoSync && allowAutoSync) system.sync();
		
		return true;
	}
	
	public void applyAnimation(double sysTime, float partialTicks, GeometryPose pose)
	{
		if(frozen)
		{
			sysTime -= partialTicks * 0.05;
			partialTicks = 0;
		}
		
		if(lastAnimation != null)
		{
			float transitionTime = currentAnimation != null ? currentAnimation.config.transitionTime : 0.25F;
			float weight = (transitionTime <= 0
							? 0F
							: (float) (1.0 - Math.min(sysTime - startTime, transitionTime) / transitionTime)
			) * this.weight * lastAnimation.getWeight();
			query.setTime(system, sysTime, partialTicks, lastAnimation);
			pose.apply(lastAnimation.config.getAnimation().getData(), mask, mode, weight, query);
		}
		
		if(currentAnimation != null)
		{
			float transitionTime = currentAnimation.config.transitionTime;
			float weight = (transitionTime <= 0
							? 1F
							: (float) Math.min(sysTime - startTime, transitionTime) / transitionTime
			) * this.weight * currentAnimation.getWeight();
			query.setTime(system, sysTime, partialTicks, currentAnimation);
			pose.apply(currentAnimation.config.getAnimation().getData(), mask, mode, weight, query);
		}
	}
	
	public void setWeight(float weight)
	{
		this.weight = weight;
	}
	
	public void tick(double sysTime)
	{
		if(frozen)
		{
			startTime += 0.05;
			if(currentAnimation != null)
				currentAnimation.activationTime += 0.05;
			if(lastAnimation != null)
				lastAnimation.activationTime += 0.05;
		}
		
		if(lastAnimation != null)
		{
			float transitionTime = currentAnimation != null ? currentAnimation.config.transitionTime : 0.25F;
			float weight = transitionTime <= 0 ? 0F :
						   (float) (1.0 - Math.min(sysTime - startTime, transitionTime) / transitionTime) *
								   this.weight *
								   lastAnimation.config.weight;
			if(weight <= 0)
				lastAnimation = null;
		}
		
		if(currentAnimation != null && currentAnimation.isDone(sysTime))
		{
			if(!currentAnimation.firedActions)
			{
				currentAnimation.firedActions = true;
				for(AnimationActionInstance action : currentAnimation.config.onFinish)
					action.execute(this);
			}
			
			if(currentAnimation.config.next != null)
				startAnimation(currentAnimation.config.next);
		}
	}
	
	public boolean stopAnimation()
	{
		return startAnimation(ConfiguredAnimation.noAnimation());
	}
	
	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = InstanceHelpers.newNBTCompound();
		tag.setFloat("Weight", weight);
		tag.setString("Name", name);
		tag.setDouble("StartTime", startTime);
		tag.setBoolean("Frozen", frozen);
		if(lastAnimation != null) tag.setTag("Last", lastAnimation.serializeNBT());
		if(currentAnimation != null) tag.setTag("Current", currentAnimation.serializeNBT());
		return tag;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound tag)
	{
		weight = tag.getFloat("Weight");
		startTime = tag.getDouble("StartTime");
		frozen = tag.getBoolean("Frozen");
		
		if(tag.hasKey("Last", Constants.NBT.TAG_COMPOUND))
			lastAnimation = new ActiveAnimation(tag.getCompoundTag("Last"));
		else lastAnimation = null;
		
		if(tag.hasKey("Current", Constants.NBT.TAG_COMPOUND))
			currentAnimation = new ActiveAnimation(tag.getCompoundTag("Current"));
		else currentAnimation = null;
	}
	
	public static Builder builder(String name)
	{
		return new Builder(name);
	}
	
	public void freeze(boolean b)
	{
		if(frozen != b)
		{
			frozen = b;
			system.sync();
		}
	}
	
	public static class Builder
	{
		protected final String name;
		
		protected float weight = 1F;
		protected boolean allowAutoSync = true;
		protected boolean persistent = true;
		protected Query query = new Query();
		protected ILayerMask mask = ILayerMask.TRUE;
		protected BlendMode blendMode = BlendMode.ADD;
		
		public Builder(String name)
		{
			this.name = name;
		}
		
		public Builder query(Query query)
		{
			this.query = query;
			return this;
		}
		
		public Builder mask(ILayerMask mask)
		{
			this.mask = mask;
			return this;
		}
		
		public Builder weight(float weight)
		{
			this.weight = weight;
			return this;
		}
		
		public Builder blendMode(BlendMode blendMode)
		{
			this.blendMode = blendMode;
			return this;
		}
		
		public Builder allowAutoSync(boolean allowAutoSync)
		{
			this.allowAutoSync = allowAutoSync;
			return this;
		}
		
		public Builder preventAutoSync()
		{
			this.allowAutoSync = false;
			return this;
		}
		
		public Builder persistent(boolean persistent)
		{
			this.persistent = persistent;
			return this;
		}
		
		public Builder nonPersistent()
		{
			this.persistent = false;
			return this;
		}
		
		public AnimationLayer build(AnimationSystem sys)
		{
			AnimationLayer layer = new AnimationLayer(sys, mask, query, name, blendMode, allowAutoSync, persistent);
			layer.weight = weight;
			return layer;
		}
	}
}