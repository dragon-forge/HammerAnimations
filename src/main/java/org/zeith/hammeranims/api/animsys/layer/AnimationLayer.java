package org.zeith.hammeranims.api.animsys.layer;

import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants;
import org.zeith.hammeranims.api.animation.AnimationLocation;
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
	public final Query query;
	public final String name;
	public final BlendMode mode;
	
	public ActiveAnimation lastAnimation;
	
	public double startTime;
	public ActiveAnimation currentAnimation;
	
	public float weight = 1F;
	
	public AnimationLayer(AnimationSystem sys, Query query, String name, BlendMode mode)
	{
		this.system = sys;
		this.query = query;
		this.name = name;
		this.mode = mode;
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
	
	public boolean startAnimation(@Nonnull ConfiguredAnimation animation)
	{
		check:
		{
			if(animation.important) break check;
			
			if(currentAnimation != null)
			{
				ConfiguredAnimation cur = currentAnimation.config;
				
				if(cur.speed == animation.speed && cur.weight == animation.weight
						&& cur.loopMode == animation.loopMode
						&& cur.transitionTime == animation.transitionTime
						&& cur.timeFunction == animation.timeFunction
						&& cur.reverse == animation.reverse
						&& cur.animation == animation.animation
				) return false;
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
		
		return true;
	}
	
	public void applyAnimation(double sysTime, float partialTicks, GeometryPose pose)
	{
		if(lastAnimation != null)
		{
			float transitionTime = currentAnimation != null ? currentAnimation.config.transitionTime : 0.25F;
			float weight = transitionTime <= 0 ? 0F :
						   (float) (1.0 - Math.min(sysTime - startTime, transitionTime) / transitionTime) *
								   this.weight *
								   lastAnimation.config.weight;
			query.setTime(system, sysTime, partialTicks, lastAnimation);
			pose.apply(lastAnimation.config.animation.getData(), mode, weight, query);
		}
		
		if(currentAnimation != null)
		{
			float transitionTime = currentAnimation.config.transitionTime;
			float weight = transitionTime <= 0 ? 1F :
						   (float) Math.min(sysTime - startTime, transitionTime) / transitionTime * this.weight *
								   currentAnimation.config.weight;
			query.setTime(system, sysTime, partialTicks, currentAnimation);
			pose.apply(currentAnimation.config.animation.getData(), mode, weight, query);
		}
	}
	
	public void setWeight(float weight)
	{
		this.weight = weight;
	}
	
	public void tick(double sysTime)
	{
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
	
	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = InstanceHelpers.newNBTCompound();
		tag.setFloat("Weight", weight);
		tag.setString("Name", name);
		tag.setDouble("StartTime", startTime);
		if(lastAnimation != null) tag.setTag("Last", lastAnimation.serializeNBT());
		if(currentAnimation != null) tag.setTag("Current", currentAnimation.serializeNBT());
		return tag;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound tag)
	{
		weight = tag.getFloat("Weight");
		startTime = tag.getDouble("StartTime");
		if(tag.hasKey("Last", Constants.NBT.TAG_COMPOUND))
			lastAnimation = new ActiveAnimation(tag.getCompoundTag("Last"));
		if(tag.hasKey("Current", Constants.NBT.TAG_COMPOUND))
			currentAnimation = new ActiveAnimation(tag.getCompoundTag("Current"));
	}
	
	public static Builder builder(String name)
	{
		return new Builder(name);
	}
	
	public static class Builder
	{
		protected final String name;
		
		protected float weight = 1F;
		protected Query query = new Query();
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
		
		public AnimationLayer build(AnimationSystem sys)
		{
			AnimationLayer layer = new AnimationLayer(sys, query, name, blendMode);
			layer.weight = weight;
			return layer;
		}
	}
}