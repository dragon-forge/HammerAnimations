package org.zeith.hammeranims.core.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.core.client.CommandReloadHA;
import org.zeith.hammeranims.core.client.model.GeometricModelImpl;
import org.zeith.hammeranims.core.impl.api.animation.AnimationDecoder;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;

import java.util.*;

public class ClientProxy
		extends CommonProxy
{
	protected static final List<IGeometricModel> createdModels = new ArrayList<>();
	protected static final List<IGeometricModel> disposeModels = new ArrayList<>();
	
	@Override
	public void construct()
	{
		super.construct();
		
		var modBus = FMLJavaModLoadingContext.get().getModEventBus();
		var forgeBus = MinecraftForge.EVENT_BUS;
		
		modBus.addListener(this::registerReloaders);
		forgeBus.addListener(this::clientTick);
		forgeBus.addListener(this::registerClientCommand);
	}
	
	private void clientTick(TickEvent.ClientTickEvent e)
	{
		if(e.phase == TickEvent.Phase.END && !disposeModels.isEmpty())
		{
			HammerAnimations.LOG.info("Disposing {} OpenGL models.", disposeModels.size());
			while(!disposeModels.isEmpty())
				disposeModels.remove(0).dispose();
			HammerAnimations.LOG.info("All previous models disposed.");
		}
	}
	
	public void registerClientCommand(RegisterClientCommandsEvent e)
	{
		e.getDispatcher().register(CommandReloadHA.command());
	}
	
	public void registerReloaders(RegisterClientReloadListenersEvent e)
	{
		e.registerReloadListener(new SimplePreparableReloadListener<Void>()
		{
			@Override
			protected Void prepare(ResourceManager resources, ProfilerFiller profiler)
			{
				return null;
			}
			
			@Override
			protected void apply(Void nothing, ResourceManager resources, ProfilerFiller profiler)
			{
				disposeModels.addAll(createdModels);
				createdModels.clear();
				reloadRegistries(wrapVanillaResources(resources), true);
			}
		});
	}
	
	@Override
	public IGeometricModel createGeometryData(GeometryDataImpl def)
	{
		GeometricModelImpl model = new GeometricModelImpl(def);
		createdModels.add(model);
		return model;
	}
	
	@Override
	public Level getClientWorld()
	{
		return Minecraft.getInstance().level;
	}
	
	public static void performReload()
	{
		disposeModels.addAll(createdModels);
		createdModels.clear();
		HammerAnimations.PROXY.reloadRegistries(wrapVanillaResources(Minecraft.getInstance()
				.getResourceManager()), true);
	}
}