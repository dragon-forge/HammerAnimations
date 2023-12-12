package org.zeith.hammeranims.core.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.core.client.model.GeometricModelImpl;
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
		
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		
		forgeBus.addListener(this::clientTick);
		
		registerReloaders((IReloadableResourceManager) Minecraft.getInstance().getResourceManager());
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
	
	public void registerReloaders(IReloadableResourceManager e)
	{
		e.registerReloadListener(new ReloadListener<Void>()
		{
			@Override
			protected Void prepare(IResourceManager pResourceManager, IProfiler pProfiler)
			{
				return null;
			}
			
			@Override
			protected void apply(Void pObject, IResourceManager pResourceManager, IProfiler pProfiler)
			{
				disposeModels.addAll(createdModels);
				createdModels.clear();
				reloadRegistries(wrapVanillaResources(pResourceManager), true);
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
	public World getClientWorld()
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