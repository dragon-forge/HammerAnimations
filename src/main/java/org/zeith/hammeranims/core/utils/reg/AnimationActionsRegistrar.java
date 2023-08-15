package org.zeith.hammeranims.core.utils.reg;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.actions.AnimationAction;

public class AnimationActionsRegistrar
		extends BaseRegistrar<AnimationAction>
{
	public AnimationActionsRegistrar(String className, ModContainer container)
	{
		super(AnimationAction.class, className, container);
	}
	
	@SubscribeEvent
	public void performRegister(RegistryEvent.Register<AnimationAction> evt)
	{
		if(evt.getRegistry() == HammerAnimationsApi.animationActions())
			registerFromContext(evt);
	}
}