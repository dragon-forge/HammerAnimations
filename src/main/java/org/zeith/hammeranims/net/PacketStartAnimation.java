package org.zeith.hammeranims.net;

import com.zeitheron.hammercore.net.*;
import lombok.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.core.client.ClientHammerHooks;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;

import java.io.IOException;

import static org.zeith.hammeranims.core.client.ClientHammerHooks.DEFAULT_TIMEOUT;

@MainThreaded
public class PacketStartAnimation
		implements IPacket
{
	protected String layer;
	protected NBTTagCompound animation;
	protected IObjectSource<?> source;
	
	public PacketStartAnimation()
	{
	}
	
	public PacketStartAnimation(AnimationLayer layer, ConfiguredAnimation animation)
	{
		this.layer = layer.name;
		this.source = layer.system.getAnimationSource();
		this.animation = animation.serializeNBT();
	}
	
	@Override
	public void write(PacketBuffer buf)
	{
		buf.writeString(layer);
		
		val tag = new NBTTagCompound();
		tag.setTag("src", IObjectSource.writeSource(source));
		tag.setTag("anim", animation);
		buf.writeCompoundTag(tag);
	}
	
	@Override
	public void read(PacketBuffer buf)
			throws IOException
	{
		layer = buf.readString(1024);
		
		var tag = buf.readCompoundTag();
		assert tag != null;
		
		source = IObjectSource.readSource(tag.getCompoundTag("src")).orElse(null);
		animation = tag.getCompoundTag("anim");
	}
	
	@Override
	public void executeOnClient2(PacketContext net)
	{
		ClientHammerHooks.startAnimation(source, DEFAULT_TIMEOUT, layer, animation);
	}
}
