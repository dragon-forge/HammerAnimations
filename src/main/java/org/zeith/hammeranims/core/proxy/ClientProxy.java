package org.zeith.hammeranims.core.proxy;

import net.minecraft.Util;
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
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;

import java.util.*;
import java.util.concurrent.*;

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
		e.registerReloadListener((preparationBarrier, resourceManager, profilerFiller, profilerFiller1, pBackgroundExecutor, pGameExecutor) ->
		{
			disposeModels.addAll(createdModels);
			createdModels.clear();
			return reloadRegistries(preparationBarrier, wrapVanillaResources(resourceManager), true, pGameExecutor, pBackgroundExecutor);
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
		var mc = Minecraft.getInstance();
		
		PreparableReloadListener.PreparationBarrier b = CompletableFuture::completedFuture;
		
		HammerAnimations.PROXY.reloadRegistries(b, wrapVanillaResources(mc.getResourceManager()), true,
				mc,
				Util.backgroundExecutor()
		);
	}
}