package org.zeith.hammeranims.core.proxy;

import net.minecraft.server.packs.resources.*;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammerlib.util.java.tuples.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class ServerProxy
		extends CommonProxy
{
	@Override
	public void construct()
	{
		super.construct();
		MinecraftForge.EVENT_BUS.addListener(this::reloadResources);
	}
	
	public void reloadResources(AddReloadListenerEvent e)
	{
		e.addListener(new SimplePreparableReloadListener<Void>()
		{
			@Override
			protected Void prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler)
			{
				return null;
			}
			
			@Override
			protected void apply(Void pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler)
			{
				reloadRegistries(wrapClassLoaderResources(), false);
			}
		});
	}
	
	public static IResourceProvider wrapClassLoaderResources()
	{
		Map<String, IModFile> namespace2ModFile = ModList.get().getModFiles().stream().flatMap(info ->
				info.getMods().stream().map(IModInfo::getNamespace).map(ns -> Tuples.immutable(ns, info.getFile()))
		).collect(Collectors.toMap(Tuple2::a, Tuple2::b));
		
		IResourceProvider aux = IResourceProvider.or(HammerAnimationsApi.getAuxiliaryResourceProviders());
		return path ->
		{
			IModFile owner = namespace2ModFile.get(path.getNamespace());
			
			var res = owner.findResource("assets", path.getNamespace(), path.getPath());
			
			if(Files.isRegularFile(res))
				try
				{
					return Optional.of(Files.readAllBytes(res));
				} catch(IOException ignored)
				{
				}
			
			return aux.read(path);
		};
	}
}