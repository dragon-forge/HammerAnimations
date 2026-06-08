package org.zeith.hammeranims.api.animsys;

import com.zeitheron.hammercore.utils.math.MathHelper;
import lombok.val;
import net.minecraft.entity.monster.IMob;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.data.effects.*;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.model.IPositionalModel;
import org.zeith.hammeranims.api.particles.emitter.IParticleRotationUpdater;
import org.zeith.hammeranims.joml.*;
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
	
	default SoundCategory getAnimationObjectSoundCategory()
	{
		if(this instanceof IMob) return SoundCategory.HOSTILE;
		if(this instanceof TileEntity) return SoundCategory.BLOCKS;
		return SoundCategory.NEUTRAL;
	}
	
	void setupSystem(AnimationSystem.Builder builder);
	
	AnimationSystem getAnimationSystem();
	
	IObjectSource<?> getAnimationSource();
	
	World getAnimatedObjectWorld();
	
	Vec3d getAnimatedObjectPosition();
	
	default @NotNull Query createQuery()
	{
		return new QueryWorld(getAnimatedObjectWorld());
	}
	
	default IGeometryContainer getObjectModel()
	{
		return getAnimationSystem().getGeometry();
	}
	
	default float getBaseAnimatedYRot(float partialTicks)
	{
		return 0F;
	}
	
	@Nullable
	default Matrix4d getBaseAnimatedMatrix(float partialTicks)
	{
		val pos = getAnimatedObjectPosition();
		if(pos == null) return null;
		return new Matrix4d()
				.translate(pos.x, pos.y, pos.z)
				.rotateY(getBaseAnimatedYRot(partialTicks) * (float) MathHelper.torad);
	}
	
	@Nullable
	default Matrix4d getAnimatedLocatorMatrix(String locator, float partialTicks)
	{
		val mat = getBaseAnimatedMatrix(partialTicks);
		if(mat == null || locator == null || locator.isEmpty()) return mat;
		IPositionalModel posMod = getObjectModel().getPositionalModel();
		posMod.applySystem(partialTicks, getAnimationSystem());
		return posMod.applyLocatorTransforms(mat, locator) ? mat : null;
	}
	
	@Nullable
	default Matrix4d getAnimatedBoneMatrix(String bone, float partialTicks)
	{
		val mat = getBaseAnimatedMatrix(partialTicks);
		if(mat == null || bone == null || bone.isEmpty()) return mat;
		IPositionalModel posMod = getObjectModel().getPositionalModel();
		posMod.applySystem(partialTicks, getAnimationSystem());
		return posMod.applyBoneTransforms(mat, bone) ? mat : null;
	}
	
	@Nullable
	default Vec3d getAnimatedLocatorPosition(String locator, float partialTicks)
	{
		val mat = getAnimatedLocatorMatrix(locator, partialTicks);
		if(mat != null)
		{
			Vector3d vpos = mat.transformPosition(new Vector3d(0));
			return new Vec3d(vpos.x, vpos.y, vpos.z);
		}
		return null;
	}
	
	default void playSound(AnimatedSoundEffect effect)
	{
		val world = getAnimatedObjectWorld();
		val pos = getAnimatedObjectPosition();
		if(world == null || pos == null || !world.isRemote) return;
		val vol = getAnimationObjectVolume();
		if(vol <= 0F) return;
		world.playSound(pos.x, pos.y, pos.z, new SoundEvent(effect.getEffect()), getAnimationObjectSoundCategory(), 1F, 1F, false);
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
		if(world == null || mat == null || !world.isRemote) return null;
		Vector3d vpos = mat.transformPosition(new Vector3d(0));
		Matrix3f rot = getParticleEffectRotation(effect);
		return HammerAnimations.PROXY.createParticle(effect, rot, new Vec3d(vpos.x, vpos.y, vpos.z));
	}
}