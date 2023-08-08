package org.zeith.hammer.models;

import com.zeitheron.hammercore.HammerCore;
import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.*;
import org.zeith.hammer.models.api.animation.IAnimationContainer;
import org.zeith.hammer.models.api.annotations.*;
import org.zeith.hammer.models.api.HammerModelsApi;
import org.zeith.hammer.models.core.impl.api.animation.AnimationDecoder;
import org.zeith.hammer.models.core.proxy.CommonProxy;

import java.lang.reflect.*;

@Mod(modid = HammerModels.MOD_ID, name = HammerModels.MOD_NAME, version = "@VERSION@", certificateFingerprint = "@FINGERPRINT@", dependencies = "required-after:hammercore", updateJSON = "https://api.modrinth.com/updates/C7cTlgwS/forge_updates.json")
public class HammerModels
{
	public static final String ROOT_PACKAGE = "org.zeith.hammer.models";
	public static final String MOD_ID = "hammermodels";
	public static final String MOD_NAME = "Hammer Models";
	
	public static final Logger LOG = LogManager.getLogger();
	
	@SidedProxy(serverSide = ROOT_PACKAGE + ".core.proxy.ServerProxy",
			clientSide = ROOT_PACKAGE + ".core.proxy.ClientProxy")
	public static CommonProxy PROXY;
	
	public HammerModels()
	{
		AnimationDecoder.init();
	}
	
	@Mod.EventHandler
	public void certificateViolation(FMLFingerprintViolationEvent e)
	{
		LOG.warn("*****************************");
		LOG.warn("WARNING: Somebody has been tampering with " + HammerModels.MOD_NAME + " jar!");
		LOG.warn("It is highly recommended that you redownload mod from https://www.curseforge.com/projects/@CF_ID@ !");
		LOG.warn("*****************************");
		HammerCore.invalidCertificates.put(HammerModels.MOD_ID, "https://www.curseforge.com/projects/@CF_ID@");
	}
	
	@Mod.EventHandler
	public void construct(FMLConstructionEvent e)
	{
		LOG.info("{} is constructing.", MOD_NAME);
		PROXY.construct();
		
		for(ASMDataTable.ASMData data : e.getASMHarvestedData().getAll(RegisterAnimations.class.getCanonicalName()))
		{
			ModContainer mod = data.getCandidate().getContainedMods().stream().findFirst().orElse(null);
			if(mod == null)
			{
				LOG.warn("Skipping @RegisterAnimations-annotated class " + data.getClassName() +
						" since it does not belong to any mod.");
				continue;
			}
			
			registerAnimations(data.getClassName(), mod);
			LOG.info("Applied @RegisterAnimations to " + data.getClassName() + ", which belongs to " + mod.getModId() +
					" (" + mod.getName() + ")");
		}
	}
	
	@Mod.EventHandler
	public void commonSetup(FMLInitializationEvent e)
	{
		PROXY.init();
	}
	
	private void registerAnimations(String className, ModContainer container)
	{
		MinecraftForge.EVENT_BUS.register(new ModelRegistrar(className, container));
	}
	
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
	
	public static class ModelRegistrar
	{
		final String className;
		final ModContainer container;
		
		public ModelRegistrar(String className, ModContainer container)
		{
			this.className = className;
			this.container = container;
		}
		
		@SubscribeEvent
		public void performRegister(RegistryEvent.Register<IAnimationContainer> evt)
		{
			IForgeRegistry<IAnimationContainer> reg = evt.getRegistry();
			
			if(reg == HammerModelsApi.animations())
			{
				evt.setModContainer(container);
				
				try
				{
					for(Field f : Class.forName(className).getDeclaredFields())
					{
						if(!IAnimationContainer.class.isAssignableFrom(f.getType())
								|| !Modifier.isStatic(f.getModifiers()))
							continue;
						
						AnimKey key = f.getAnnotation(AnimKey.class);
						if(key == null)
						{
							LOG.warn("Found animation field without @AnimKey: " + f);
							continue;
						}
						
						f.setAccessible(true);
						IAnimationContainer ctr = Cast.cast(f.get(null), IAnimationContainer.class);
						if(ctr == null)
						{
							LOG.warn("Found NULL animation container field: " + f);
							continue;
						}
						
						ResourceLocation regKey = new ResourceLocation(container.getModId(), key.value());
						
						reg.register(ctr.setRegistryName(regKey)); // This varies between versions...
						
						LOG.debug("Registered animation from " + f);
					}
				} catch(Exception e)
				{
					LOG.error("Failed to register animations from class " + className, e);
				}
			}
		}
	}
}