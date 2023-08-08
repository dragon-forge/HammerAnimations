package org.zeith.hammeranims.core.utils.reg;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.AnimationSourceType;
import org.zeith.hammeranims.api.annotations.Key;

import java.lang.reflect.*;

import static org.zeith.hammeranims.HammerAnimations.LOG;

public class AnimationSourceTypesRegistrar
{
	final String className;
	final ModContainer container;
	
	public AnimationSourceTypesRegistrar(String className, ModContainer container)
	{
		this.className = className;
		this.container = container;
	}
	
	@SubscribeEvent
	public void performRegister(RegistryEvent.Register<AnimationSourceType> evt)
	{
		IForgeRegistry<AnimationSourceType> reg = evt.getRegistry();
		
		if(reg == HammerAnimationsApi.animationSources())
		{
			evt.setModContainer(container);
			
			try
			{
				for(Field f : Class.forName(className).getDeclaredFields())
				{
					if(!AnimationSourceType.class.isAssignableFrom(f.getType())
							|| !Modifier.isStatic(f.getModifiers()))
						continue;
					
					Key key = f.getAnnotation(Key.class);
					if(key == null)
					{
						LOG.warn("Found AnimationSourceType field without @Key: {}", f);
						continue;
					}
					
					f.setAccessible(true);
					AnimationSourceType ctr = Cast.cast(f.get(null), AnimationSourceType.class);
					if(ctr == null)
					{
						LOG.warn("Found NULL AnimationSourceType container field: {}", f);
						continue;
					}
					
					ResourceLocation regKey = new ResourceLocation(container.getModId(), key.value());
					
					reg.register(ctr.setRegistryName(regKey)); // This varies between versions...
					
					LOG.debug("Registered AnimationSourceType from {}", f);
				}
			} catch(Exception e)
			{
				LOG.error("Failed to register AnimationSourceTypes from class " + className, e);
			}
		}
	}
}