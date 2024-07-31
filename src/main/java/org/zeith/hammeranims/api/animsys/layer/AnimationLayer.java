package org.zeith.hammeranims.api.animsys.layer;

import lombok.val;
import net.minecraft.nbt.*;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.effects.AnimatedParticleEffect;
import org.zeith.hammeranims.api.animation.data.effects.AnimatedSoundEffect;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.api.animsys.actions.AnimationActionInstance;
import org.zeith.hammeranims.api.geometry.model.GeometryPose;
import org.zeith.hammeranims.api.particles.emitter.IParticleRotationUpdater;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.ContainersHA;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import javax.annotation.*;
import java.time.Duration;
import java.util.*;

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
	
	private final Map<IParticleRotationUpdater, AnimatedParticleEffect> particles = new HashMap<>();
	
	@Setter
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
			
			SerializableMask sm = lastAnimation.config.mask;
			if(sm != null) pose.apply(sm, lastAnimation.config.getAnimation().getData(), mask, mode, weight, query);
			else pose.apply(lastAnimation.config.getAnimation().getData(), mask, mode, weight, query);
		}
		
		if(currentAnimation != null)
		{
			float transitionTime = currentAnimation.config.transitionTime;
			float weight = (transitionTime <= 0
							? 1F
							: (float) Math.min(sysTime - startTime, transitionTime) / transitionTime
						   ) * this.weight * currentAnimation.getWeight();
			query.setTime(system, sysTime, partialTicks, currentAnimation);
			
			SerializableMask sm = currentAnimation.config.mask;
			if(sm != null) pose.apply(sm, currentAnimation.config.getAnimation().getData(), mask, mode, weight, query);
			else pose.apply(currentAnimation.config.getAnimation().getData(), mask, mode, weight, query);
		}
	}
	
	public void processEffects(double sysTime, @NotNull ActiveAnimation a)
	{
		int ticks = (int) Math.round(a.config.timeFunction.getTime(system, sysTime, 0F, a) * 20);
		
		if(a.lastTick != ticks)
		{
			val prev = a.lastTick;
			a.lastTick = ticks;
			val anim = a.config.getAnimation();
			if(anim == null) return;
			
			val data = anim.getData();
			val particles = data.getParticleEffects();
			val sounds = data.getSoundEffects();
			
			val owner = system.owner;
			
			for(int i = prev; i < ticks; i++)
			{
				val snds = sounds.get(i);
				if(snds != null)
					snds.forEach(owner::playSound);
				
				val fx = particles.get(i);
				if(fx != null) for(AnimatedParticleEffect effect : fx)
				{
					val pp = owner.playParticle(effect);
					if(pp != null) this.particles.put(pp, effect);
				}
			}
		}
	}
	
	public void tick(double sysTime)
	{
		particles.entrySet().removeIf(upd ->
		{
			IParticleRotationUpdater u = upd.getKey();
			val mat = system.owner.getParticleEffectRotation(upd.getValue());
			if(mat != null) u.setMatrix(mat);
			return !u.emittingParticles();
		});
		
		if(frozen)
		{
			startTime += 0.05;
			if(currentAnimation != null)
				currentAnimation.activationTime += 0.05;
			if(lastAnimation != null)
				lastAnimation.activationTime += 0.05;
		} else
		{
			if(currentAnimation != null) processEffects(sysTime, currentAnimation);
			if(lastAnimation != null) processEffects(sysTime, lastAnimation);
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
	
	public boolean stopAnimation(float transitionTime)
	{
		return startAnimation(ConfiguredAnimation.noAnimation().transitionTime(transitionTime));
	}
	
	public boolean stopAnimation(Duration transitionTime)
	{
		return startAnimation(ConfiguredAnimation.noAnimation().transitionTime(transitionTime));
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		var tag = InstanceHelpers.newNBTCompound();
		tag.putFloat("Weight", weight);
		tag.putString("Name", name);
		tag.putDouble("StartTime", startTime);
		tag.putBoolean("Frozen", frozen);
		if(lastAnimation != null) tag.put("Last", lastAnimation.serializeNBT());
		if(currentAnimation != null) tag.put("Current", currentAnimation.serializeNBT());
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag tag)
	{
		weight = tag.getFloat("Weight");
		startTime = tag.getDouble("StartTime");
		frozen = tag.getBoolean("Frozen");
		
		if(tag.contains("Last", Tag.TAG_COMPOUND))
			lastAnimation = new ActiveAnimation(tag.getCompound("Last"));
		else lastAnimation = null;
		
		if(tag.contains("Current", Tag.TAG_COMPOUND))
			currentAnimation = new ActiveAnimation(tag.getCompound("Current"));
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