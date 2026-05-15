package org.zeith.hammeranims.net;

import com.zeitheron.hammercore.net.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.*;
import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.core.client.ClientHammerHooks;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;

import java.io.IOException;

import static org.zeith.hammeranims.core.client.ClientHammerHooks.DEFAULT_TIMEOUT;

@MainThreaded
public class PacketSyncAnimationSystem
		implements IPacket
{
	protected NBTTagCompound tag;
	protected IObjectSource<?> source;
	
	public PacketSyncAnimationSystem()
	{
	}
	
	public PacketSyncAnimationSystem(AnimationSystem system)
	{
		this.tag = system.serializeNBT();
		this.source = system.owner.getAnimationSource();
	}
	
	@Override
	public void write(PacketBuffer buf)
	{
		buf.writeCompoundTag(tag);
		IObjectSource.writeSource(source, buf);
	}
	
	@Override
	public void read(PacketBuffer buf)
			throws IOException
	{
		tag = buf.readCompoundTag();
		source = IObjectSource.readSource(buf).orElse(null);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void executeOnClient2(PacketContext net)
	{
		ClientHammerHooks.applySystem(source, DEFAULT_TIMEOUT, tag);
	}
}