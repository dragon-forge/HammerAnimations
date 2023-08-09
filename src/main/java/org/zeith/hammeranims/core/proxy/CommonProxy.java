package org.zeith.hammeranims.core.proxy;

import com.google.common.base.Stopwatch;
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.IForgeRegistry;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.event.RefreshStaleModelsEvent;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.util.mcf.LogicalSidePredictor;

import java.io.*;
import java.util.Optional;
import java.util.concurrent.*;

public class CommonProxy
{
	public void construct()
	{
	}
	
	public IGeometricModel createGeometryData(GeometryDataImpl data)
	{
		return IGeometricModel.EMPTY;
	}
	
	public Level getClientWorld()
	{
		return null;
	}
	
	protected void reloadRegistries(IResourceProvider provider, boolean clientSide)
	{
		Stopwatch sw = Stopwatch.createStarted();
		
		HammerAnimations.LOG.info("Reloading {} registries...", HammerAnimations.MOD_NAME);
		
		IForgeRegistry<IAnimationContainer> reg1 = HammerAnimationsApi.animations();
		HammerAnimations.LOG.info("Reloading {} animations.", reg1.getKeys().size());
		for(IAnimationContainer container : reg1)
			container.reload(provider);
		
		IForgeRegistry<IGeometryContainer> reg2 = HammerAnimationsApi.geometries();
		HammerAnimations.LOG.info("Reloading {} models.", reg2.getKeys().size());
		for(IGeometryContainer container : reg2)
			container.reload(provider);
		
		if(clientSide)
			HammerAnimationsApi.EVENT_BUS.post(new RefreshStaleModelsEvent());
		
		HammerAnimations.LOG.info("{} registries reloaded in {} ms",
				HammerAnimations.MOD_NAME,
				sw.stop().elapsed(TimeUnit.MILLISECONDS)
		);
	}
	
	public static IResourceProvider wrapVanillaResources(ResourceManager manager)
	{
		IResourceProvider aux = IResourceProvider.or(HammerAnimationsApi.getAuxiliaryResourceProviders());
		return path ->
		{
			Optional<Resource> res0 = manager.getResource(path);
			var res = res0.orElse(null);
			if(res != null)
				try(var in = res.open())
				{
					return Optional.of(in.readAllBytes());
				} catch(IOException ignored)
				{
				}
			return aux.read(path);
		};
	}
}