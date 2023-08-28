package org.zeith.hammeranims.net;

import com.zeitheron.hammercore.net.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.*;

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
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setTag("Src", source.writeSource());
		nbt.setString("Type", source.getType().getRegistryKey().toString());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		AnimationSourceType type = HammerAnimationsApi.animationSources()
				.getValue(new ResourceLocation(nbt.getString("Type")));
		if(type != null)
			source = type.readSource(nbt.getCompoundTag("Src"));
		else
			HammerAnimations.LOG.warn("Unable to find animation source {}", nbt.getString("Type"));
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