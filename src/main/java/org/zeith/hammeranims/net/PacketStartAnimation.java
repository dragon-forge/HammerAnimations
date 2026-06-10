package org.zeith.hammeranims.net;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
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
	protected CompoundTag animation;
	protected IObjectSource<?> source;
	
	public PacketStartAnimation()
	{
	}
	
	public PacketStartAnimation(HolderLookup.Provider provider, AnimationLayer layer, ConfiguredAnimation animation)
	{
		this.layer = layer.name;
		this.source = layer.system.getAnimationSource();
		this.animation = animation.serializeNBT(provider);
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeUtf(layer);
		
		CompoundTag tag = new CompoundTag();
		tag.put("src", IObjectSource.writeSource(source));
		tag.put("anim", animation);
		buf.writeNbt(tag);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		layer = buf.readUtf();
		
		CompoundTag tag = buf.readNbt();
		assert tag != null;
		
		source = IObjectSource.readSource(tag.getCompound("src")).orElse(null);
		animation = tag.getCompound("anim");
	}
	
	@Override
	public void clientExecute(PacketContext ctx)
	{
		ClientHammerHooks.startAnimation(source, DEFAULT_TIMEOUT, layer, animation);
	}
}
