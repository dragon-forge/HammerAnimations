package org.zeith.hammeranims.core.utils.reg;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.time.TimeFunction;

public class TimeFunctionRegistrar
		extends BaseRegistrar<TimeFunction>
{
	public TimeFunctionRegistrar(String className, ModContainer container)
	{
		super(TimeFunction.class, className, container);
	}
	
	@SubscribeEvent
	public void performRegister(RegistryEvent.Register<TimeFunction> evt)
	{
		if(evt.getRegistry() == HammerAnimationsApi.timeFunctions())
			registerFromContext(evt);
	}
}