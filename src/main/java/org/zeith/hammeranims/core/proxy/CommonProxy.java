package org.zeith.hammeranims.core.proxy;

import com.google.common.base.Stopwatch;
import net.minecraft.server.packs.resources.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.IForgeRegistry;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.event.ReloadHammerAnimationsEvent;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.event.RefreshStaleModelsEvent;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class CommonProxy
{
	public void construct()
	{
	}
	
	public IGeometricModel createGeometryData(GeometryDataImpl def)
	{
		return IGeometricModel.EMPTY;
	}
	
	public Level getClientWorld()
	{
		return null;
	}
	
	protected CompletableFuture<Void> reloadRegistries(PreparableReloadListener.PreparationBarrier pStage, IResourceProvider provider, boolean clientSide, Executor gameExecutor, Executor backgroundExecutor)
	{
		Stopwatch sw = Stopwatch.createStarted();
		
		HammerAnimations.LOG.info("Reloading {} registries...", HammerAnimations.MOD_NAME);
		
		Stream.Builder<CompletableFuture<?>> tasks = Stream.builder();
		ReloadHammerAnimationsEvent.EnqueueReloads queues = new ReloadHammerAnimationsEvent.EnqueueReloads(
				provider, clientSide, tasks::add,
				gameExecutor, backgroundExecutor
		);
		
		Collection<IAnimationContainer> animations = HammerAnimationsApi.animations().getValues();
		HammerAnimations.LOG.info("Reloading {} animations.", animations.size());
		queues.enqueue(animations.stream().map((ctr) -> (Runnable) () -> ctr.reload(provider)), CompletableFuture::runAsync);
		
		Collection<IGeometryContainer> geometries = HammerAnimationsApi.geometries().getValues();
		HammerAnimations.LOG.info("Reloading {} models.", geometries.size());
		queues.enqueue(geometries.stream().map((ctr) -> (Runnable) () -> ctr.reload(provider)), CompletableFuture::runAsync);
		
		Collection<IParticleContainer> particles = HammerAnimationsApi.particleContainers().getValues();
		HammerAnimations.LOG.info("Reloading {} particles.", particles.size());
		queues.enqueue(geometries.stream().map((ctr) -> (Runnable) () -> ctr.reload(provider)), CompletableFuture::runAsync);
		
		HammerAnimationsApi.EVENT_BUS.post(queues);
		
		return CompletableFuture.allOf(tasks.build().toArray(CompletableFuture[]::new))
				.thenCompose(pStage::wait)
				.thenRunAsync(() ->
				{
					if(clientSide)
						HammerAnimationsApi.EVENT_BUS.post(new RefreshStaleModelsEvent());
					
					HammerAnimationsApi.EVENT_BUS.post(new ReloadHammerAnimationsEvent.Post(provider, clientSide));
					HammerAnimations.LOG.info("{} registries reloaded in {} ms",
							HammerAnimations.MOD_NAME,
							sw.stop().elapsed(TimeUnit.MILLISECONDS)
					);
				}, gameExecutor);
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