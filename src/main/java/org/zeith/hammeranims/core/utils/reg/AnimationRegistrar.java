package org.zeith.hammeranims.core.utils.reg;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.IAnimationContainer;

public class AnimationRegistrar
		extends BaseRegistrar<IAnimationContainer>
{
	public AnimationRegistrar(String className, ModContainer container)
	{
		super(IAnimationContainer.class, className, container);
	}
	
	@SubscribeEvent
	public void performRegister(RegistryEvent.Register<IAnimationContainer> evt)
	{
		if(evt.getRegistry() == HammerAnimationsApi.animations())
			registerFromContext(evt);
	}
}