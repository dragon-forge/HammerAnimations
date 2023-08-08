package org.zeith.hammeranims.core.utils.reg;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.annotations.Key;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;

import java.lang.reflect.*;

import static org.zeith.hammeranims.HammerAnimations.LOG;

public class GeometryRegistrar
{
	final String className;
	final ModContainer container;
	
	public GeometryRegistrar(String className, ModContainer container)
	{
		this.className = className;
		this.container = container;
	}
	
	@SubscribeEvent
	public void performRegister(RegistryEvent.Register<IGeometryContainer> evt)
	{
		IForgeRegistry<IGeometryContainer> reg = evt.getRegistry();
		
		if(reg == HammerAnimationsApi.geometries())
		{
			evt.setModContainer(container);
			
			try
			{
				for(Field f : Class.forName(className).getDeclaredFields())
				{
					if(!IGeometryContainer.class.isAssignableFrom(f.getType())
							|| !Modifier.isStatic(f.getModifiers()))
						continue;
					
					Key key = f.getAnnotation(Key.class);
					if(key == null)
					{
						LOG.warn("Found geometry field without @Key: " + f);
						continue;
					}
					
					f.setAccessible(true);
					IGeometryContainer ctr = Cast.cast(f.get(null), IGeometryContainer.class);
					if(ctr == null)
					{
						LOG.warn("Found NULL geometry container field: " + f);
						continue;
					}
					
					ResourceLocation regKey = new ResourceLocation(container.getModId(), key.value());
					
					reg.register(ctr.setRegistryName(regKey)); // This varies between versions...
					
					LOG.debug("Registered geometry from " + f);
				}
			} catch(Exception e)
			{
				LOG.error("Failed to register geometries from class " + className, e);
			}
		}
	}
}