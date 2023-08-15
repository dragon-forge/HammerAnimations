package org.zeith.hammeranims.core.utils.reg;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;

public class GeometryRegistrar
		extends BaseRegistrar<IGeometryContainer>
{
	public GeometryRegistrar(String className, ModContainer container)
	{
		super(IGeometryContainer.class, className, container);
	}
	
	@SubscribeEvent
	public void performRegister(RegistryEvent.Register<IGeometryContainer> evt)
	{
		if(evt.getRegistry() == HammerAnimationsApi.geometries())
			registerFromContext(evt);
	}
}