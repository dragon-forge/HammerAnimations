package org.zeith.hammeranims.net;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.core.client.ClientHammerHooks;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;
import org.zeith.hammerlib.net.*;

import static org.zeith.hammeranims.core.client.ClientHammerHooks.DEFAULT_TIMEOUT;

@MainThreaded
public class PacketStartAnimation
		implements IPacket
{
	protected String layer;
	protected ConfiguredAnimation animation;
	protected IObjectSource<?> source;
	
	public PacketStartAnimation()
	{
	}
	
	public PacketStartAnimation(HolderLookup.Provider provider, AnimationLayer layer, ConfiguredAnimation animation)
	{
		this.layer = layer.name;
		this.source = layer.system.getAnimationSource();
		this.animation = animation;
	}
	
	@Override
	public void write(RegistryFriendlyByteBuf buf)
	{
		buf.writeUtf(layer, 1024);
		buf.writeNbt(IObjectSource.writeSource(source));
		animation.write(buf);
	}
	
	@Override
	public void read(RegistryFriendlyByteBuf buf)
	{
		layer = buf.readUtf(1024);
		
		CompoundTag tag = buf.readNbt();
		assert tag != null;
		
		source = IObjectSource.readSource(tag).orElse(null);
		animation = ConfiguredAnimation.read(buf);
	}
	
	@Override
	public void clientExecute(PacketContext ctx)
	{
		ClientHammerHooks.startAnimation(source, DEFAULT_TIMEOUT, layer, animation);
	}
}
