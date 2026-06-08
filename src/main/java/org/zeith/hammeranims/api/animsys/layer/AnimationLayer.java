package org.zeith.hammeranims.api.animsys.layer;

import lombok.Setter;
import org.jetbrains.annotations.*;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.api.geometry.model.GeometryPose;
import org.zeith.hammeranims.core.init.DefaultsHA;

import java.time.Duration;
import java.util.Objects;

public class AnimationLayer
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
	
	@Setter
	public float weight = 1F;
	
	@Setter
	public float defaultTransitionTime = 0.25F;
	
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
	
	public boolean startAnimation(@NotNull IAnimationSource animation)
	{
		return startAnimation(animation instanceof ConfiguredAnimation ? (ConfiguredAnimation) animation : animation.configure().transitionTime(defaultTransitionTime));
	}
	
	public boolean startAnimation(@NotNull ConfiguredAnimation animation)
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
	
	public static Builder builder(String name)
	{
		return new Builder(name);
	}
	
	public void freeze(boolean shouldFreeze)
	{
		if(frozen != shouldFreeze)
		{
			frozen = shouldFreeze;
		}
	}
	
	public static class Builder
	{
		protected final String name;
		
		protected float weight = 1F;
		protected boolean allowAutoSync = true;
		protected boolean persistent = true;
		protected Query query;
		protected ILayerMask mask = ILayerMask.TRUE;
		protected BlendMode blendMode = BlendMode.ADD;
		protected float defaultTransitionTime = 0.25F;
		protected ConfiguredAnimation initialAnimation;
		
		public Builder(String name)
		{
			this.name = name;
		}
		
		public Builder initialAnimation(IAnimationSource initialAnimation)
		{
			return initialAnimation(initialAnimation.configure());
		}
		
		public Builder initialAnimation(ConfiguredAnimation initialAnimation)
		{
			this.initialAnimation = new ConfiguredAnimation(initialAnimation).transitionTime(0F);
			return this;
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
		
		public Builder nonPersistent()
		{
			this.persistent = false;
			return this;
		}
		
		public AnimationLayer build(AnimationSystem sys)
		{
			var q = query;
			if(q == null) q = sys.query;
			AnimationLayer layer = new AnimationLayer(sys, mask, q, name, blendMode, allowAutoSync, persistent);
			layer.weight = weight;
			layer.defaultTransitionTime = defaultTransitionTime;
			if(initialAnimation != null) layer.currentAnimation = new ActiveAnimation(layer, new ConfiguredAnimation(initialAnimation), q);
			return layer;
		}
	}
}