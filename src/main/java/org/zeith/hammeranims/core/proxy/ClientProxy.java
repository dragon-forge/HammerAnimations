package org.zeith.hammeranims.core.proxy;

import com.google.common.base.Suppliers;
import com.zeitheron.hammercore.client.HammerCoreClient;
import com.zeitheron.hammercore.lib.zlib.io.IOUtils;
import com.zeitheron.hammercore.net.HCNet;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.resource.*;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.McUtil;
import org.zeith.hammeranims.api.animation.data.effects.AnimatedParticleEffect;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.api.geometry.model.RenderData;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.emitter.IParticleRotationUpdater;
import org.zeith.hammeranims.api.utils.IExtendedResourceProvider;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.client.BuiltInResourcePack;
import org.zeith.hammeranims.core.client.CommandReloadHA;
import org.zeith.hammeranims.core.client.model.GeometricModelImpl;
import org.zeith.hammeranims.core.client.particle.ParticleWithEmitter;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammeranims.core.client.render.TessellatorVertexRenderer;
import org.zeith.hammeranims.core.client.render.entity.RenderEntityBilly;
import org.zeith.hammeranims.core.client.render.tile.RenderTileBilly;
import org.zeith.hammeranims.core.contents.blocks.TileBilly;
import org.zeith.hammeranims.core.contents.entity.EntityBilly;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;
import org.zeith.hammeranims.core.impl.api.particles.ExtraParticleEffects;
import org.zeith.hammeranims.joml.Matrix3f;
import org.zeith.hammeranims.net.PacketProvideCustomParticleEffectList;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
	
	private boolean inWorld;
	
	@SubscribeEvent
	public void clientTick(TickEvent.ClientTickEvent e)
	{
		if(e.phase != TickEvent.Phase.END) return;
		
		if(!disposeModels.isEmpty())
		{
			HammerAnimations.LOG.info("Disposing {} OpenGL models.", disposeModels.size());
			while(!disposeModels.isEmpty())
				disposeModels.remove(0).dispose();
			HammerAnimations.LOG.info("All previous models disposed.");
		}
		
		Minecraft mc = Minecraft.getMinecraft();
		boolean inWorldRN = mc.world != null && mc.getConnection() != null;
		if(inWorldRN != inWorld)
		{
			inWorld = inWorldRN;
			if(inWorldRN) PacketProvideCustomParticleEffectList.toServer();
		}
	}
	
	@Override
	public IGeometricModel createGeometryData(GeometryDataImpl def)
	{
		GeometricModelImpl model = new GeometricModelImpl(def);
		createdModels.add(model);
		return model;
	}
	
	public World getClientWorld()
	{
		return Minecraft.getMinecraft().world;
	}
	
	@Override
	public void construct()
	{
		if("@VERSION@".contains("@VERSION"))
			HammerCoreClient.injectResourcePack(new BuiltInResourcePack(HammerAnimations.class, HammerAnimations.MOD_ID));
		super.construct();
	}
	
	@Override
	public void preInit()
	{
		super.preInit();
		RenderingRegistry.registerEntityRenderingHandler(EntityBilly.class, RenderEntityBilly::new);
	}
	
	@Override
	public void init()
	{
		super.init();
		
		ClientCommandHandler.instance.registerCommand(new CommandReloadHA());
		
		new RenderTileBilly().bindTo(TileBilly.class);
		
		IResourceManager resources = Minecraft.getMinecraft().getResourceManager();
		
		((IReloadableResourceManager) resources).registerReloadListener(new ISelectiveResourceReloadListener()
		{
			@Override
			public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate)
			{
				if(resourcePredicate.test(VanillaResourceType.MODELS))
				{
					performReload();
				}
			}
			
			@Override
			public String toString()
			{
				return HammerAnimations.MOD_NAME;
			}
		});
	}
	
	@Override
	public IParticleRotationUpdater createParticle(AnimatedParticleEffect effect, Matrix3f rotation, Vec3d pos)
	{
		val world = Minecraft.getMinecraft().world;
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
			pwe.getEmitter().lastWorldTick = world.getTotalWorldTime();
		}
		pwe.spawn();
		return pwe.getEmitter();
	}
	
	/**
	 * The one and only reference to the tessellator which also sorts all translucent vertices right after the upload method is called.
	 */
	public static Supplier<IVertexRenderer> SHARED_TESS_RENDERER = Suppliers.memoize(() -> new TessellatorVertexRenderer(Tessellator.getInstance()));
	
	@Override
	public void initRD(RenderData data)
	{
		data.renderer = SHARED_TESS_RENDERER.get();
	}
	
	public static CompletableFuture<?> performReload()
	{
		disposeModels.addAll(createdModels);
		createdModels.clear();
		
		IExtendedResourceProvider res = wrapVanillaResources(Minecraft.getMinecraft().getResourceManager());
		
		return CompletableFuture.allOf(
				ExtraParticleEffects.load(res, McUtil.backgroundExecutor()).thenAccept(fx -> extraEffects = fx),
				HammerAnimations.PROXY.reloadRegistries(
						res,
						Minecraft.getMinecraft()::addScheduledTask, McUtil.backgroundExecutor(),
						true
				)
		).thenRun(() ->
				Minecraft.getMinecraft().addScheduledTask(() ->
				{
					NetHandlerPlayClient net = Minecraft.getMinecraft().getConnection();
					if(net == null || Minecraft.getMinecraft().world == null) return;
					PacketProvideCustomParticleEffectList.toServer();
				})
		);
	}
	
	public static IExtendedResourceProvider wrapVanillaResources(IResourceManager manager)
	{
		IResourceProvider aux = IResourceProvider.or(HammerAnimationsApi.getAuxiliaryResourceProviders());
		return new IExtendedResourceProvider()
		{
			@Override
			public Optional<byte[]> read(ResourceLocation path)
			{
				try(IResource res = manager.getResource(path))
				{
					return Optional.of(IOUtils.pipeOut(res.getInputStream()));
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
					all.addAll(manager.getAllResources(path).stream().map(r ->
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
}