package org.zeith.hammeranims.net;

import com.zeitheron.hammercore.net.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.core.client.ClientHammerHooks;

import java.io.IOException;

@MainThreaded
public class PacketSyncAnimationSystem
		implements IPacket
{
	protected NBTTagCompound tag;
	protected AnimationSource source;
	
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
		buf.writeCompoundTag(source.writeSource());
		buf.writeResourceLocation(source.getType().getRegistryKey());
	}
	
	@Override
	public void read(PacketBuffer buf)
			throws IOException
	{
		tag = buf.readCompoundTag();
		
		NBTTagCompound src = buf.readCompoundTag();
		ResourceLocation typeKey = buf.readResourceLocation();
		AnimationSourceType type = HammerAnimationsApi.animationSources().getValue(typeKey);
		if(type != null) source = type.readSource(src);
		else HammerAnimations.LOG.warn("Unable to find animation source {} sent by server.", typeKey);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void executeOnClient2(PacketContext net)
	{
		ClientHammerHooks.applySystem(source, tag, 100);
	}
}