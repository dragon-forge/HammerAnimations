package org.zeith.hammeranims.net;

import com.zeitheron.hammercore.net.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.*;

import java.io.IOException;

public class PacketRequestAnimationSystemSync
		implements IPacket
{
	protected AnimationSource source;
	
	public PacketRequestAnimationSystemSync()
	{
	}
	
	public PacketRequestAnimationSystemSync(AnimationSystem system)
	{
		this.source = system.owner.getAnimationSource();
	}
	
	@Override
	public void write(PacketBuffer buf)
	{
		buf.writeCompoundTag(source.writeSource());
		buf.writeResourceLocation(source.getType().getRegistryKey());
	}
	
	@Override
	public void read(PacketBuffer buf)
			throws IOException
	{
		NBTTagCompound src = buf.readCompoundTag();
		ResourceLocation typeKey = buf.readResourceLocation();
		AnimationSourceType type = HammerAnimationsApi.animationSources().getValue(typeKey);
		if(type != null) source = type.readSource(src);
		else HammerAnimations.LOG.warn("Unable to find animation source {} sent by server.", typeKey);
	}
	
	@Override
	public void executeOnServer2(PacketContext net)
	{
		World world = net.getSender().getServerWorld();
		IAnimatedObject object = source.get(world);
		if(object != null)
			net.withReply(new PacketSyncAnimationSystem(object.getAnimationSystem()));
	}
}