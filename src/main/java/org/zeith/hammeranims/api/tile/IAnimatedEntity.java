package org.zeith.hammeranims.api.tile;

import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;
import org.zeith.hammerlib.util.java.Cast;

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
		return entity.getBbWidth();
	}
	
	@Override
	default float getAnimatedObjectHeight()
	{
		Entity entity = Cast.cast(this);
		return entity.getBbHeight();
	}
	
	@Override
	default Level getAnimatedObjectWorld()
	{
		Entity entity = Cast.cast(this);
		return entity.level();
	}
	
	@Override
	default Vec3 getAnimatedObjectPosition()
	{
		Entity entity = Cast.cast(this);
		return entity.position();
	}
	
	@Override
	default float getBaseAnimatedYRot(float partialTicks)
	{
		Entity entity = Cast.cast(this);
		if(entity instanceof LivingEntity)
		{
			LivingEntity living = (LivingEntity) entity;
			return 180F - QueryEntity.getBodyYaw(living, partialTicks);
		}
		return 180F - entity.getViewYRot(partialTicks);
	}
}