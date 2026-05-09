package org.zeith.hammeranims.core.proxy;

import com.google.common.base.Stopwatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.animation.data.effects.AnimatedParticleEffect;
import org.zeith.hammeranims.api.event.ReloadHammerAnimationsEvent;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.event.RefreshStaleModelsEvent;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.emitter.IParticleRotationUpdater;
import org.zeith.hammeranims.api.texture.ITextureAccess;
import org.zeith.hammeranims.api.utils.IExtendedResourceProvider;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;
import org.zeith.hammeranims.core.impl.api.particles.ExtraParticleEffects;
import org.zeith.hammerlib.api.proxy.IProxy;
import org.zeith.hammerlib.util.java.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class CommonProxy
		implements IProxy
{
	public void construct(IEventBus modBus)
	{
	}
	
	public IGeometricModel createGeometryData(GeometryDataImpl def)
	{
		return IGeometricModel.EMPTY;
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
		
		var animations = HammerAnimationsApi.animations().entrySet();
		HammerAnimations.LOG.info("Reloading {} animations.", animations.size());
		queues.enqueue(animations.stream().map((ctr) -> (Runnable) () -> ctr.getValue().reload(provider)), CompletableFuture::runAsync);
		
		var geometries = HammerAnimationsApi.geometries().entrySet();
		HammerAnimations.LOG.info("Reloading {} models.", geometries.size());
		queues.enqueue(geometries.stream().map((ctr) -> (Runnable) () -> ctr.getValue().reload(provider)), CompletableFuture::runAsync);
		
		var particles = HammerAnimationsApi.particleContainers().entrySet();
		HammerAnimations.LOG.info("Reloading {} particles.", particles.size());
		queues.enqueue(particles.stream().map((ctr) -> (Runnable) () -> ctr.getValue().reload(provider)), CompletableFuture::runAsync);
		
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
										}, gameExecutor
								);
	}
	
	public static IExtendedResourceProvider wrapVanillaResources(ResourceManager manager)
	{
		IResourceProvider aux = IResourceProvider.or(HammerAnimationsApi.getAuxiliaryResourceProviders());
		return new IExtendedResourceProvider()
		{
			@Override
			public Optional<byte[]> read(ResourceLocation path)
			{
				try(var in = manager.getResource(path).orElseThrow(FileNotFoundException::new).open())
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
				List<byte[]> all = new ArrayList<>(manager.listPacks().map(pr -> pr.getResource(PackType.CLIENT_RESOURCES, path)).filter(Objects::nonNull).map(r ->
				{
					try(var in = r.get())
					{
						return IOUtils.pipeOut(in);
					} catch(IOException ignored)
					{
						return null;
					}
				}).filter(Objects::nonNull).toList());
				
				for(IResourceProvider provider : HammerAnimationsApi.getAuxiliaryResourceProviders())
					provider.read(path).ifPresent(all::add);
				
				return all;
			}
		};
	}
	
	public IParticleRotationUpdater createParticle(AnimatedParticleEffect effect, Matrix3f rotation, Vec3 pos)
	{
		return null;
	}
	
	public ExtraParticleEffects getExtraParticles()
	{
		return null;
	}
	
	@NotNull
	public ITextureAccess loadTextureAccess(ResourceLocation texture)
	{
		return ITextureAccess.MISSING_TEXTURE;
	}
	
	public void purgeTextureAccessCache()
	{
	}
}