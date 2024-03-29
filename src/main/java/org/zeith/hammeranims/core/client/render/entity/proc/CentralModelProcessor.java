package org.zeith.hammeranims.core.client.render.entity.proc;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;

import java.util.List;

public class CentralModelProcessor<T extends Entity & IAnimatedEntity>
{
	protected final List<IProcessor<? super T>> prc = Lists.newArrayList();
	
	public void add(IProcessor<? super T> processor)
	{
		prc.add(processor);
	}
	
	public void apply(T object, IGeometricModel model, float partialTick, float netHeadYaw, float headPitch)
	{
		for(IProcessor<? super T> p : prc)
			p.process(object, model, partialTick, netHeadYaw, headPitch);
	}
}