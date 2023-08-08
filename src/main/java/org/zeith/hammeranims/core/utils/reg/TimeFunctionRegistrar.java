package org.zeith.hammeranims.core.utils.reg;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.annotations.Key;
import org.zeith.hammeranims.api.time.TimeFunction;

import java.lang.reflect.*;

import static org.zeith.hammeranims.HammerAnimations.LOG;

public class TimeFunctionRegistrar
{
	final String className;
	final ModContainer container;
	
	public TimeFunctionRegistrar(String className, ModContainer container)
	{
		this.className = className;
		this.container = container;
	}
	
	@SubscribeEvent
	public void performRegister(RegistryEvent.Register<TimeFunction> evt)
	{
		IForgeRegistry<TimeFunction> reg = evt.getRegistry();
		
		if(reg == HammerAnimationsApi.timeFunctions())
		{
			evt.setModContainer(container);
			
			try
			{
				for(Field f : Class.forName(className).getDeclaredFields())
				{
					if(!TimeFunction.class.isAssignableFrom(f.getType())
							|| !Modifier.isStatic(f.getModifiers()))
						continue;
					
					Key key = f.getAnnotation(Key.class);
					if(key == null)
					{
						LOG.warn("Found TimeFunction field without @Key: {}", f);
						continue;
					}
					
					f.setAccessible(true);
					TimeFunction ctr = Cast.cast(f.get(null), TimeFunction.class);
					if(ctr == null)
					{
						LOG.warn("Found NULL TimeFunction container field: {}", f);
						continue;
					}
					
					ResourceLocation regKey = new ResourceLocation(container.getModId(), key.value());
					
					reg.register(ctr.setRegistryName(regKey)); // This varies between versions...
					
					LOG.debug("Registered TimeFunction from {}", f);
				}
			} catch(Exception e)
			{
				LOG.error("Failed to register TimeFunctions from class " + className, e);
			}
		}
	}
}