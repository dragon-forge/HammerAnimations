package org.zeith.hammeranims.api.animsys.layer;

import lombok.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.effects.AnimatedParticleEffect;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.api.animsys.actions.AnimationActionInstance;
import org.zeith.hammeranims.api.geometry.model.GeometryPose;
import org.zeith.hammeranims.api.particles.emitter.IParticleRotationUpdater;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammeranims.net.PacketStartAnimation;

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
	
	@Setter
	public float defaultTransitionTime = 0.25F;
	
	protected boolean useNanoTime;
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
		return startAnimation(animation instanceof ConfiguredAnimation ? (ConfiguredAnimation) animation : animation.configure().transitionTime(defaultTransitionTime));
	}
	
	public boolean startAnimation(@Nonnull ConfiguredAnimation animation)
	{
		return startAnimationSync(animation, true);
	}
	
	public boolean startAnimationSync(@Nonnull ConfiguredAnimation animation, boolean doSync)
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
		currentAnimation = animation.activate(this, query);
		
		currentAnimation.useNanoTime = useNanoTime;
		
		if(doSync && system.autoSync && allowAutoSync)
		{
			if(!system.syncTime)
				system.sendPacketToTracking(new PacketStartAnimation(this, animation));
			else
				system.sync();
		}
		
		return true;
	}
	
	protected float getFadeOut(double sysTime, float transitionTime)
	{
		double time = currentAnimation != null ? currentAnimation.elapsedSeconds(sysTime) : (sysTime - startTime);
		return transitionTime <= 0 ? 0F : 1.0F - Math.min(Math.max(0, (float) time), transitionTime) / transitionTime;
	}
	
	protected float getFadeIn(double sysTime, float transitionTime)
	{
		double time = currentAnimation != null ? currentAnimation.elapsedSeconds(sysTime) : (sysTime - startTime);
		return transitionTime <= 0 ? 1F : Math.min(Math.max(0, (float) time), transitionTime) / transitionTime;
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
			ActiveAnimation la = lastAnimation;
			ActiveAnimation aa = currentAnimation;
			float transitionTime = aa != null ? aa.config.transitionTime : defaultTransitionTime;
			float weight = getFadeOut(sysTime, transitionTime) * this.weight * la.getWeight();
			query.setTime(system, sysTime, partialTicks, lastAnimation);
			
			SerializableMask sm = la.config.mask;
			if(sm != null) pose.apply(sm, lastAnimation, mask, mode, weight);
			else pose.apply(lastAnimation, mask, mode, weight);
		}
		
		if(currentAnimation != null)
		{
			float transitionTime = currentAnimation.config.transitionTime;
			float weight = getFadeIn(sysTime, transitionTime) * this.weight * currentAnimation.getWeight();
			query.setTime(system, sysTime, partialTicks, currentAnimation);
			
			SerializableMask sm = currentAnimation.config.mask;
			if(sm != null) pose.apply(sm, currentAnimation, mask, mode, weight);
			else pose.apply(currentAnimation, mask, mode, weight);
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
			
			int maxTicks = Math.min(100, ticks - prev);
			for(int i = 0; i < maxTicks; i++)
			{
				val snds = sounds.get(ticks + i);
				if(snds != null)
					snds.forEach(owner::playSound);
				
				val fx = particles.get(ticks + i);
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
			
			long nt = System.nanoTime();
			
			if(currentAnimation != null)
			{
				currentAnimation.useNanoTime = useNanoTime;
				currentAnimation.activationTime += 0.05;
				if(currentAnimation.freezeRelativeNanoTime <= 0L) currentAnimation.freezeRelativeNanoTime = nt - currentAnimation.activationTimeNanos;
			}
			
			if(lastAnimation != null)
			{
				lastAnimation.useNanoTime = useNanoTime;
				lastAnimation.activationTime += 0.05;
				if(lastAnimation.freezeRelativeNanoTime <= 0L) lastAnimation.freezeRelativeNanoTime = nt - lastAnimation.activationTimeNanos;
			}
		} else
		{
			if(currentAnimation != null)
			{
				currentAnimation.useNanoTime = useNanoTime;
				currentAnimation.freezeRelativeNanoTime = -1L;
				processEffects(sysTime, currentAnimation);
			}
			if(lastAnimation != null)
			{
				lastAnimation.useNanoTime = useNanoTime;
				lastAnimation.freezeRelativeNanoTime = -1L;
				processEffects(sysTime, lastAnimation);
			}
		}
		
		if(lastAnimation != null)
		{
			float transitionTime = currentAnimation != null ? currentAnimation.config.transitionTime : defaultTransitionTime;
			float weight = getFadeOut(sysTime, transitionTime) * this.weight * lastAnimation.config.weight;
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
				startAnimationSync(currentAnimation.config.next, false);
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
			lastAnimation = new ActiveAnimation(tag.getCompoundTag("Last"), query);
		else lastAnimation = null;
		
		if(tag.hasKey("Current", Constants.NBT.TAG_COMPOUND))
			currentAnimation = new ActiveAnimation(tag.getCompoundTag("Current"), query);
		else currentAnimation = null;
	}
	
	public static Builder builder(String name)
	{
		return new Builder(name);
	}
	
	public void freeze(boolean shouldFreeze)
	{
		if(frozen != shouldFreeze)
		{
			setFrozen(shouldFreeze);
			system.sync();
		}
	}
	
	protected void setFrozen(boolean shouldFreeze)
	{
		frozen = shouldFreeze;
		if(frozen)
		{
			long nt = System.nanoTime();
			if(lastAnimation != null) lastAnimation.freezeRelativeNanoTime = nt - lastAnimation.activationTimeNanos;
			if(currentAnimation != null) currentAnimation.freezeRelativeNanoTime = nt - currentAnimation.activationTimeNanos;
		} else
		{
			if(lastAnimation != null) lastAnimation.freezeRelativeNanoTime = -1L;
			if(currentAnimation != null) currentAnimation.freezeRelativeNanoTime = -1L;
		}
	}
	
	public static class Builder
	{
		protected final String name;
		
		protected float weight = 1F;
		protected boolean allowAutoSync = true;
		protected boolean persistent = true;
		protected Boolean useNanoTime;
		protected Query query = new Query();
		protected ILayerMask mask = ILayerMask.TRUE;
		protected BlendMode blendMode = BlendMode.ADD;
		protected float defaultTransitionTime = 0.25F;
		
		public Builder(String name)
		{
			this.name = name;
		}
		
		public Builder defaultQuery(Query query)
		{
			if(this.query == null)
				this.query = query;
			return this;
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
		
		public Builder defaultTransitionTime(float defaultTransitionTime)
		{
			this.defaultTransitionTime = defaultTransitionTime;
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
		
		public Builder useNanoTime(boolean useNanoTime)
		{
			this.useNanoTime = useNanoTime;
			return this;
		}
		
		public Builder nonPersistent()
		{
			this.persistent = false;
			return this;
		}
		
		public AnimationLayer build(AnimationSystem sys)
		{
			if(query == null) query = new Query();
			AnimationLayer layer = new AnimationLayer(sys, mask, query, name, blendMode, allowAutoSync, persistent);
			layer.weight = weight;
			layer.defaultTransitionTime = defaultTransitionTime;
			layer.useNanoTime = useNanoTime != null ? useNanoTime : sys.isDefaultUseNanoTime();
			return layer;
		}
	}
}