package org.zeith.hammeranims.api.tile;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.entity.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;

public interface IAnimatedEntity
		extends IAnimatedObject
{
	default void registerEntityProperties(Query q, IVariableRegistrar reg)
	{
	}
	
	@Override
	default @NotNull Query createQuery()
	{
		Entity entity = Cast.cast(this);
		return new QueryEntity(entity);
	}
	
	@Override
	default IObjectSource<?> getAnimationSource()
	{
		Entity entity = Cast.cast(this);
		return IObjectSource.ofEntity(entity).get();
	}
	
	@Override
	default float getAnimatedObjectWidth()
	{
		Entity entity = Cast.cast(this);
		return entity.width;
	}
	
	@Override
	default float getAnimatedObjectHeight()
	{
		Entity entity = Cast.cast(this);
		return entity.height;
	}
	
	@Override
	default World getAnimatedObjectWorld()
	{
		Entity entity = Cast.cast(this);
		return entity.getEntityWorld();
	}
	
	@Override
	default Vec3d getAnimatedObjectPosition()
	{
		Entity entity = Cast.cast(this);
		return entity.getPositionVector();
	}
	
	@Override
	default float getBaseAnimatedYRot(float partialTicks)
	{
		Entity entity = Cast.cast(this);
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase living = (EntityLivingBase) entity;
			return 180F - QueryEntity.getBodyYaw(living, partialTicks);
		}
		return 180F - (entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks);
	}
}