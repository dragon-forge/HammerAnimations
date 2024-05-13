package org.zeith.hammeranims.core.proxy;

import com.google.common.base.Stopwatch;
import net.minecraft.resources.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.event.ReloadHammerAnimationsEvent;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.event.RefreshStaleModelsEvent;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.utils.IExtendedResourceProvider;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;
import org.zeith.hammeranims.core.impl.api.particles.ExtraParticleEffects;
import org.zeith.hammerlib.util.java.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
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
	
	public World getClientWorld()
	{
		return null;
	}
	
	protected CompletableFuture<Void> reloadRegistries(IFutureReloadListener.IStage pStage, IResourceProvider provider, boolean clientSide, Executor gameExecutor, Executor backgroundExecutor)
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
	
	public static IExtendedResourceProvider wrapVanillaResources(IResourceManager manager)
	{
		IResourceProvider aux = IResourceProvider.or(HammerAnimationsApi.getAuxiliaryResourceProviders());
		return new IExtendedResourceProvider()
		{
			@Override
			public Optional<byte[]> read(ResourceLocation path)
			{
				try(IResource res0 = manager.getResource(path); InputStream in = res0.getInputStream())
				{
					return Optional.of(IOUtils.pipeOut(in));
				} catch(IOException ignored)
				{
				}
				return aux.read(path);
			}
			
			@Override
			public List<byte[]> readAll(ResourceLocation path)
			{
				List<byte[]> all = new ArrayList<>();
				
				try
				{
					all.addAll(manager.getResources(path).stream().map(r ->
					{
						try(IResource res = r)
						{
							return IOUtils.pipeOut(res.getInputStream());
						} catch(IOException ignored)
						{
							return null;
						}
					}).filter(Objects::nonNull).collect(Collectors.toList()));
				} catch(IOException e)
				{
				}
				
				for(IResourceProvider provider : HammerAnimationsApi.getAuxiliaryResourceProviders())
					provider.read(path).ifPresent(all::add);
				
				return all;
			}
		};
	}
	
	public ExtraParticleEffects getExtraParticles()
	{
		return null;
	}
}