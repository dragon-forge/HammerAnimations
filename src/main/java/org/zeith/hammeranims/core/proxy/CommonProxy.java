package org.zeith.hammeranims.core.proxy;

import com.google.common.base.Stopwatch;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.event.ReloadHammerAnimationsEvent;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.event.RefreshStaleModelsEvent;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class CommonProxy
{
	public void construct()
	{
	}
	
	public void preInit()
	{
	}
	
	public void init()
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
	
	public void serverAboutToStart(MinecraftServer server)
	{
	}
	
	protected CompletableFuture<Void> reloadRegistries(IResourceProvider provider, Executor gameExecutor, Executor backgroundExecutor, boolean clientSide)
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
		
		HammerAnimationsApi.EVENT_BUS.post(queues);
		
		return CompletableFuture.allOf(tasks.build().toArray(CompletableFuture[]::new))
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
	
	public void initRD(RenderData data)
	{
	}
}