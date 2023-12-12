package org.zeith.hammeranims.core.proxy;

import com.google.common.base.Stopwatch;
import net.minecraft.resources.*;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.event.RefreshStaleModelsEvent;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;
import org.zeith.hammerlib.util.java.IOUtils;

import java.io.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class CommonProxy
{
	public void construct()
	{
	}
	
	public IGeometricModel createGeometryData(GeometryDataImpl def)
	{
		return IGeometricModel.EMPTY;
	}
	
	public World getClientWorld()
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
	
	public static IResourceProvider wrapVanillaResources(IResourceManager manager)
	{
		IResourceProvider aux = IResourceProvider.or(HammerAnimationsApi.getAuxiliaryResourceProviders());
		return path ->
		{
			try(IResource res0 = manager.getResource(path); InputStream in = res0.getInputStream())
			{
				return Optional.of(IOUtils.pipeOut(in));
			} catch(IOException ignored)
			{
			}
			return aux.read(path);
		};
	}
}