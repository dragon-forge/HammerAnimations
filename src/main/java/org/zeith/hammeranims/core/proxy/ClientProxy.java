package org.zeith.hammeranims.core.proxy;

import com.zeitheron.hammercore.lib.zlib.io.IOUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.*;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.resource.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.client.CommandReloadHA;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammeranims.core.client.render.tile.RenderTileBilly;
import org.zeith.hammeranims.core.contents.blocks.TileBilly;
import org.zeith.hammeranims.core.impl.api.animation.AnimationDecoder;
import org.zeith.hammeranims.core.client.model.GeometricModelImpl;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;

import java.io.IOException;
import java.util.*;

public class ClientProxy
		extends CommonProxy
{
	protected static final List<IGeometricModel> createdModels = new ArrayList<>();
	protected static final List<IGeometricModel> disposeModels = new ArrayList<>();
	
	@SubscribeEvent
	public void clientTick(TickEvent.ClientTickEvent e)
	{
		if(e.phase == TickEvent.Phase.END && !disposeModels.isEmpty())
		{
			HammerAnimations.LOG.info("Disposing {} OpenGL models.", disposeModels.size());
			while(!disposeModels.isEmpty())
				disposeModels.remove(0).dispose();
			HammerAnimations.LOG.info("All previous models disposed.");
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
	public void init()
	{
		super.init();
		
		ClientCommandHandler.instance.registerCommand(new CommandReloadHA());
		
		new RenderTileBilly().bindTo(TileBilly.class);
		
		IResourceManager resources = Minecraft.getMinecraft().getResourceManager();
		
		((IReloadableResourceManager) resources).registerReloadListener((ISelectiveResourceReloadListener) (resourceManager, resourcePredicate) ->
		{
			if(resourcePredicate.test(VanillaResourceType.MODELS))
			{
				disposeModels.addAll(createdModels);
				createdModels.clear();
				reloadRegistries(wrapVanillaResources(resourceManager));
			}
		});
	}
	
	@Override
	public void initRD(RenderData data)
	{
		data.renderer = IVertexRenderer.wrap(Tessellator.getInstance().getBuffer());
	}
	
	public static void performReload()
	{
		disposeModels.addAll(createdModels);
		createdModels.clear();
		HammerAnimations.PROXY.reloadRegistries(wrapVanillaResources(Minecraft.getMinecraft().getResourceManager()));
	}
	
	public static IResourceProvider wrapVanillaResources(IResourceManager manager)
	{
		IResourceProvider aux = IResourceProvider.or(HammerAnimationsApi.getAuxiliaryResourceProviders());
		return path ->
		{
			try(IResource res = manager.getResource(path))
			{
				return Optional.of(IOUtils.pipeOut(res.getInputStream()));
			} catch(IOException ignored)
			{
			}
			return aux.read(path);
		};
	}
}