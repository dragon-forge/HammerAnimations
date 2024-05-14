package org.zeith.hammeranims.core.proxy;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.core.client.CommandReloadHA;
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
		
		var modBus = FMLJavaModLoadingContext.get().getModEventBus();
		var forgeBus = MinecraftForge.EVENT_BUS;
		
		modBus.addListener(this::registerReloaders);
		forgeBus.addListener(this::clientTick);
		forgeBus.addListener(this::registerClientCommand);
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
	
	@Override
	public Level getClientWorld()
	{
		return Minecraft.getInstance().level;
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
					Network.sendToServer(new PacketProvideCustomParticleEffectList.PacketResetList());
				})
		);
	}
}