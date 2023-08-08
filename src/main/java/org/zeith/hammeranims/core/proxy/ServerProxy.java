package org.zeith.hammeranims.core.proxy;

import com.zeitheron.hammercore.HammerCore;
import com.zeitheron.hammercore.lib.zlib.io.IOUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.utils.IResourceProvider;

import java.io.*;
import java.util.Optional;

public class ServerProxy
		extends CommonProxy
{
	@Override
	public void serverAboutToStart(MinecraftServer server)
	{
		if(server instanceof DedicatedServer)
		{
			reloadRegistries(wrapClassLoaderResources());
		}
	}
	
	public static IResourceProvider wrapClassLoaderResources()
	{
		IResourceProvider aux = IResourceProvider.or(HammerAnimationsApi.getAuxiliaryResourceProviders());
		return path ->
		{
			try(InputStream res = HammerCore.class.getResourceAsStream(
					"/assets/" + path.getNamespace() + "/" + path.getPath()
			))
			{
				if(res == null) return Optional.empty();
				return Optional.of(IOUtils.pipeOut(res));
			} catch(IOException ignored)
			{
			}
			return aux.read(path);
		};
	}
}