package org.zeith.hammeranims.core.utils.reg;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.registries.*;
import org.zeith.hammeranims.api.animsys.actions.AnimationAction;
import org.zeith.hammeranims.api.annotations.Key;

import java.lang.reflect.*;
import java.util.function.Consumer;

import static org.zeith.hammeranims.HammerAnimations.LOG;

public class BaseRegistrar<T extends IForgeRegistryEntry<T>>
{
	final Class<T> base;
	final String className;
	final ModContainer container;
	
	public BaseRegistrar(Class<T> base, String className, ModContainer container)
	{
		this.base = base;
		this.className = className;
		this.container = container;
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	protected void registerFromContext(RegistryEvent.Register<T> evt)
	{
		ModContainer old = Loader.instance().activeModContainer();
		Loader.instance().setActiveModContainer(container);
		evt.setModContainer(container);
		
		IForgeRegistry<T> reg = evt.getRegistry();
		
		try
		{
			for(Field f : Class.forName(className).getDeclaredFields())
			{
				if(!base.isAssignableFrom(f.getType()) || !Modifier.isStatic(f.getModifiers()))
					continue;
				
				Key key = f.getAnnotation(Key.class);
				if(key == null)
				{
					LOG.warn("Found {} field without @Key: {}", base.getSimpleName(), f);
					continue;
				}
				
				f.setAccessible(true);
				T ctr = Cast.cast(f.get(null), base);
				if(ctr == null)
				{
					LOG.warn("Found NULL {} field: {}", base.getSimpleName(), f);
					continue;
				}
				
				ResourceLocation regKey = new ResourceLocation(container.getModId(), key.value());
				
				reg.register(ctr.setRegistryName(regKey)); // This varies between versions...
				
				LOG.debug("Registered {} from {}", base.getSimpleName(), f);
			}
		} catch(Exception e)
		{
			LOG.error("Failed to register {} from class {}", base.getSimpleName(), className, e);
		}
		
		Loader.instance().setActiveModContainer(old);
	}
}