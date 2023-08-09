package org.zeith.hammeranims.core.utils;

import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.*;
import org.zeith.hammeranims.api.annotations.Key;

import java.lang.reflect.*;
import java.util.function.Supplier;

import static org.zeith.hammeranims.HammerAnimations.LOG;

public class BasicRegistrar<T>
{
	final Supplier<Class<?>> className;
	final Class<T> baseType;
	final Supplier<IForgeRegistry<T>> registry;
	
	public BasicRegistrar(Class<T> baseType, Supplier<IForgeRegistry<T>> registry, Supplier<Class<?>> className, FMLModContainer container)
	{
		this.baseType = baseType;
		this.registry = registry;
		this.className = className;
		container.getEventBus().addListener(this::performRegister);
	}
	
	public static <T> void perform(Class<T> base, Supplier<IForgeRegistry<T>> registry, Supplier<Class<?>> n, FMLModContainer mod)
	{
		new BasicRegistrar<>(base, registry, n, mod);
	}
	
	public void performRegister(RegisterEvent evt)
	{
		var regKey0 = evt.getRegistryKey();
		var reg = registry.get();
		
		if(regKey0.equals(reg.getRegistryKey()))
		{
			try
			{
				for(Field f : className.get().getDeclaredFields())
				{
					if(!baseType.isAssignableFrom(f.getType())
							|| !Modifier.isStatic(f.getModifiers()))
						continue;
					
					Key key = f.getAnnotation(Key.class);
					if(key == null)
					{
						LOG.warn("Found {} field without @Key: {}", baseType.getSimpleName(), f);
						continue;
					}
					
					f.setAccessible(true);
					T ctr = baseType.cast(f.get(null));
					if(ctr == null)
					{
						LOG.warn("Found NULL {} field: {}", baseType.getSimpleName(), f);
						continue;
					}
					
					reg.register(key.value(), ctr); // This varies between versions...
					
					LOG.debug("Registered {} from {}", baseType.getSimpleName(), f);
				}
			} catch(Exception e)
			{
				LOG.error("Failed to register {} from class {}", baseType.getSimpleName(), className, e);
			}
		}
	}
}