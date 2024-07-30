package org.zeith.hammeranims.core.proxy;

import lombok.val;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Matrix3f;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.data.effects.AnimatedParticleEffect;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.emitter.IParticleRotationUpdater;
import org.zeith.hammeranims.core.client.CommandReloadHA;
import org.zeith.hammeranims.core.client.model.GeometricModelImpl;
import org.zeith.hammeranims.core.client.particle.ParticleWithEmitter;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;
import org.zeith.hammeranims.core.impl.api.particles.ExtraParticleEffects;
import org.zeith.hammeranims.net.PacketProvideCustomParticleEffectList;
import org.zeith.hammerlib.api.proxy.IClientProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClientProxy
		extends CommonProxy
		implements IClientProxy
{
	protected static final List<IGeometricModel> createdModels = new ArrayList<>();
	protected static final List<IGeometricModel> disposeModels = new ArrayList<>();
	
	protected static ExtraParticleEffects extraEffects;
	
	@Override
	public IParticleRotationUpdater createParticle(AnimatedParticleEffect effect, Matrix3f rotation, Vec3 pos)
	{
		val world = Minecraft.getInstance().level;
		IParticleContainer container = effect.getParticle();
		if(world == null || container == null) return null;
		ParticleWithEmitter pwe = new ParticleWithEmitter(
				world,
				pos.x, pos.y, pos.z,
				container
		);
		if(rotation != null)
		{
			pwe.getEmitter().rotation = rotation;
			pwe.getEmitter().lastWorldTick = world.getGameTime();
		}
		pwe.spawn();
		return pwe.getEmitter();
	}
	
	@Override
	public ExtraParticleEffects getExtraParticles()
	{
		return extraEffects;
	}
	
	@Override
	public void construct(IEventBus modBus)
	{
		super.construct(modBus);
		
		var forgeBus = NeoForge.EVENT_BUS;
		
		modBus.addListener(this::registerReloaders);
		forgeBus.addListener(this::clientTick);
		forgeBus.addListener(this::registerClientCommand);
	}
	
	private boolean inWorld;
	
	private void clientTick(ClientTickEvent.Post e)
	{
		if(!disposeModels.isEmpty())
		{
			HammerAnimations.LOG.info("Disposing {} OpenGL models.", disposeModels.size());
			while(!disposeModels.isEmpty())
				disposeModels.remove(0).dispose();
			HammerAnimations.LOG.info("All previous models disposed.");
		}
		
		Minecraft mc = Minecraft.getInstance();
		boolean inWorldRN = mc.level != null && mc.getConnection() != null;
		if(inWorldRN != inWorld)
		{
			inWorld = inWorldRN;
			if(inWorldRN) PacketProvideCustomParticleEffectList.toServer();
		}
	}
	
	public void registerClientCommand(RegisterClientCommandsEvent e)
	{
		e.getDispatcher().register(CommandReloadHA.command());
	}
	
	public void registerReloaders(RegisterClientReloadListenersEvent e)
	{
		e.registerReloadListener((preparationBarrier, resourceManager, profilerFiller, profilerFiller1, pBackgroundExecutor, pGameExecutor) ->
				performReload(preparationBarrier)
		);
	}
	
	@Override
	public IGeometricModel createGeometryData(GeometryDataImpl def)
	{
		GeometricModelImpl model = new GeometricModelImpl(def);
		createdModels.add(model);
		return model;
	}
	
	public static CompletableFuture<Void> performReload()
	{
		return performReload(CompletableFuture::completedFuture);
	}
	
	public static CompletableFuture<Void> performReload(PreparableReloadListener.PreparationBarrier b)
	{
		disposeModels.addAll(createdModels);
		createdModels.clear();
		
		Minecraft mc = Minecraft.getInstance();
		var res = wrapVanillaResources(mc.getResourceManager());
		
		return CompletableFuture.allOf(
				ExtraParticleEffects.load(res, Util.backgroundExecutor()).thenAccept(fx -> extraEffects = fx),
				HammerAnimations.PROXY.reloadRegistries(b, res, true,
						mc,
						Util.backgroundExecutor()
				)
		).thenRun(() ->
				Minecraft.getInstance().execute(() ->
				{
					var net = Minecraft.getInstance().getConnection();
					if(net == null || Minecraft.getInstance().level == null) return;
					PacketProvideCustomParticleEffectList.toServer();
				})
		);
	}
}