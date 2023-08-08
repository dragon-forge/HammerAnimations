package org.zeith.hammer.models.core.proxy;

import com.google.common.base.Stopwatch;
import com.zeitheron.hammercore.lib.zlib.io.IOUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.*;
import net.minecraftforge.client.resource.*;
import net.minecraftforge.registries.IForgeRegistry;
import org.zeith.hammer.models.HammerModels;
import org.zeith.hammer.models.api.animation.IAnimationContainer;
import org.zeith.hammer.models.api.geometry.IGeometryContainer;
import org.zeith.hammer.models.api.HammerModelsApi;
import org.zeith.hammer.models.api.utils.IResourceProvider;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ClientProxy
		extends CommonProxy
{
	@Override
	public void init()
	{
		super.init();
		
		IResourceManager resources = Minecraft.getMinecraft().getResourceManager();
		
		((IReloadableResourceManager) resources).registerReloadListener((ISelectiveResourceReloadListener) (resourceManager, resourcePredicate) ->
		{
			if(resourcePredicate.test(VanillaResourceType.MODELS))
				performReload(resourceManager);
		});
	}
	
	public static void performReload(IResourceManager resources)
	{
		Stopwatch sw = Stopwatch.createStarted();
		HammerModels.LOG.info("Reloading {} registries...", HammerModels.MOD_NAME);
		
		IResourceProvider provider = wrapVanillaResources(resources);
		
		IForgeRegistry<IAnimationContainer> reg1 = HammerModelsApi.animations();
		HammerModels.LOG.info("Reloading {} animations.", reg1.getKeys().size());
		for(IAnimationContainer container : reg1)
			container.reload(provider);
		
		IForgeRegistry<IGeometryContainer> reg2 = HammerModelsApi.geometries();
		HammerModels.LOG.info("Reloading {} models.", reg2.getKeys().size());
		for(IGeometryContainer container : reg2)
			container.reload(provider);
		
		HammerModels.LOG.info("{} registries reloaded in {} ms", HammerModels.MOD_NAME, sw.stop()
				.elapsed(TimeUnit.MILLISECONDS));
	}
	
	public static IResourceProvider wrapVanillaResources(IResourceManager manager)
	{
		return path ->
		{
			try(IResource res = manager.getResource(path))
			{
				return Optional.of(IOUtils.pipeOut(res.getInputStream()));
			} catch(IOException ignored)
			{
			}
			return Optional.empty();
		};
	}
}