package org.zeith.hammeranims.core.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.api.utils.IExtendedResourceProvider;
import org.zeith.hammeranims.core.client.model.GeometricModelImpl;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;
import org.zeith.hammeranims.core.impl.api.particles.ExtraParticleEffects;
import org.zeith.hammeranims.net.PacketProvideCustomParticleEffectList;
import org.zeith.hammerlib.net.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClientProxy
		extends CommonProxy
{
	protected static final List<IGeometricModel> createdModels = new ArrayList<>();
	protected static final List<IGeometricModel> disposeModels = new ArrayList<>();
	
	protected static ExtraParticleEffects extraEffects;
	
	@Override
	public ExtraParticleEffects getExtraParticles()
	{
		return extraEffects;
	}
	
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
	
	@Override
	public World getClientWorld()
	{
		return Minecraft.getInstance().level;
	}
	
	public static CompletableFuture<Void> performReload()
	{
		return performReload(CompletableFuture::completedFuture);
	}
	
	public static CompletableFuture<Void> performReload(IFutureReloadListener.IStage b)
	{
		disposeModels.addAll(createdModels);
		createdModels.clear();
		
		Minecraft mc = Minecraft.getInstance();
		IExtendedResourceProvider res = wrapVanillaResources(mc.getResourceManager());
		
		return CompletableFuture.allOf(
				ExtraParticleEffects.load(res, Util.backgroundExecutor()).thenAccept(fx -> extraEffects = fx),
				HammerAnimations.PROXY.reloadRegistries(b, res, true,
						mc,
						Util.backgroundExecutor()
				)
		).thenRun(() ->
				Minecraft.getInstance().execute(() ->
				{
					ClientPlayNetHandler net = Minecraft.getInstance().getConnection();
					if(net == null || Minecraft.getInstance().level == null) return;
					Network.sendToServer(new PacketProvideCustomParticleEffectList.PacketResetList());
				})
		);
	}
}