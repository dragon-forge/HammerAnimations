package org.zeith.hammeranims.api.animsys;

import lombok.val;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.data.effects.AnimatedParticleEffect;
import org.zeith.hammeranims.api.animation.data.effects.AnimatedSoundEffect;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.model.IPositionalModel;
import org.zeith.hammeranims.api.particles.emitter.IParticleRotationUpdater;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;

public interface IAnimatedObject
{
	default float getAnimatedObjectScale()
	{
		return 1F;
	}
	
	default float getAnimatedObjectWidth()
	{
		return 1F;
	}
	
	default float getAnimatedObjectHeight()
	{
		return 1F;
	}
	
	default float getAnimatedObjectDepth()
	{
		return getAnimatedObjectWidth();
	}
	
	default float getAnimationObjectVolume()
	{
		return 1F;
	}
	
	default SoundSource getAnimationObjectSoundCategory()
	{
		if(this instanceof Enemy) return SoundSource.HOSTILE;
		if(this instanceof BlockEntity) return SoundSource.BLOCKS;
		return SoundSource.NEUTRAL;
	}
	
	void setupSystem(AnimationSystem.Builder builder);
	
	AnimationSystem getAnimationSystem();
	
	IObjectSource<?> getAnimationSource();
	
	Level getAnimatedObjectWorld();
	
	Vec3 getAnimatedObjectPosition();
	
	default Query createQuery()
	{
		return new QueryWorld(getAnimatedObjectWorld());
	}
	
	default IGeometryContainer getObjectModel()
	{
		return DefaultsHA.NULL_GEOMETRY;
	}
	
	default Matrix4d getAnimatedLocatorMatrix(String locator, float partialTicks)
	{
		val pos = getAnimatedObjectPosition();
		if(pos == null) return null;
		val mat = new Matrix4d()
				.identity()
				.translate(pos.x + 0.5F, pos.y, pos.z + 0.5F)
				.rotateY((float) (Mth.DEG_TO_RAD * 0));
		if(locator == null || locator.isEmpty())
			return mat;
		IPositionalModel posMod = getObjectModel().getPositionalModel();
		posMod.applySystem(partialTicks, getAnimationSystem());
		if(posMod.applyLocatorTransforms(mat, locator))
			return mat;
		return null;
	}
	
	default Vec3 getAnimatedLocatorPosition(String locator, float partialTicks)
	{
		val mat = getAnimatedLocatorMatrix(locator, partialTicks);
		if(mat != null)
		{
			Vector3d vpos = mat.transformPosition(new Vector3d(0));
			return new Vec3(vpos.x, vpos.y, vpos.z);
		}
		return null;
	}
	
	default void playSound(AnimatedSoundEffect effect)
	{
		val world = getAnimatedObjectWorld();
		val pos = getAnimatedObjectPosition();
		if(world == null || pos == null || !world.isClientSide()) return;
		val vol = getAnimationObjectVolume();
		if(vol <= 0F) return;
		world.playLocalSound(pos.x, pos.y, pos.z, BuiltInRegistries.SOUND_EVENT.get(effect.getEffect()), getAnimationObjectSoundCategory(), 1F, 1F, false);
	}
	
	default Matrix3f getParticleEffectRotation(AnimatedParticleEffect effect)
	{
		val mat = getAnimatedLocatorMatrix(effect.getLocator(), 1F);
		if(mat == null) return null;
		Matrix3f rot = new Matrix3f();
		rot.rotate(mat.getNormalizedRotation(new Quaternionf()));
		return rot;
	}
	
	default IParticleRotationUpdater playParticle(AnimatedParticleEffect effect)
	{
		val world = getAnimatedObjectWorld();
		val mat = getAnimatedLocatorMatrix(effect.getLocator(), 1F);
		if(world == null || mat == null || !world.isClientSide) return null;
		Vector3d vpos = mat.transformPosition(new Vector3d(0));
		Matrix3f rot = getParticleEffectRotation(effect);
		return HammerAnimations.PROXY.createParticle(effect, rot, new Vec3(vpos.x, vpos.y, vpos.z));
	}
}