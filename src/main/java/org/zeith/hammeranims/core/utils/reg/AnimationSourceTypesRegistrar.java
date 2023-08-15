package org.zeith.hammeranims.core.utils.reg;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.AnimationSourceType;

public class AnimationSourceTypesRegistrar
		extends BaseRegistrar<AnimationSourceType>
{
	public AnimationSourceTypesRegistrar(String className, ModContainer container)
	{
		super(AnimationSourceType.class, className, container);
	}
	
	@SubscribeEvent
	public void performRegister(RegistryEvent.Register<AnimationSourceType> evt)
	{
		if(evt.getRegistry() == HammerAnimationsApi.animationSources())
			registerFromContext(evt);
	}
}