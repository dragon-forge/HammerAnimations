package org.zeith.hammeranims.core.proxy;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.locating.IModFile;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammerlib.util.java.tuples.Tuple2;
import org.zeith.hammerlib.util.java.tuples.Tuples;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServerProxy
		extends CommonProxy
{
	@Override
	public void construct(IEventBus modBus)
	{
		super.construct(modBus);
		NeoForge.EVENT_BUS.addListener(this::reloadResources);
	}
	
	public void reloadResources(AddReloadListenerEvent e)
	{
		e.addListener((preparationBarrier, resourceManager, profilerFiller, profilerFiller1, pBackgroundExecutor, pGameExecutor) ->
				reloadRegistries(preparationBarrier, wrapClassLoaderResources(), false, pGameExecutor, pBackgroundExecutor)
		);
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