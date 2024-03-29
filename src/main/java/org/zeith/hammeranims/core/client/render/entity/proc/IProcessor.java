package org.zeith.hammeranims.core.client.render.entity.proc;

import net.minecraft.world.entity.Entity;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;

public interface IProcessor<T extends Entity & IAnimatedEntity>
{
	void process(T t, IGeometricModel model, float partialTick);
}