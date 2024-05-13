package org.zeith.hammeranims.core.utils;

import net.minecraft.world.entity.Entity;
import org.zeith.hammeranims.api.particles.IEntityOlderMovement;

public class EntityTransformationUtils
{
	public static double getPrevPrevPosX(Entity entity)
	{
		return ((IEntityOlderMovement) entity).getPrevPrevX();
	}
	
	public static double getPrevPrevPosY(Entity entity)
	{
		return ((IEntityOlderMovement) entity).getPrevPrevY();
	}
	
	public static double getPrevPrevPosZ(Entity entity)
	{
		return ((IEntityOlderMovement) entity).getPrevPrevZ();
	}
}