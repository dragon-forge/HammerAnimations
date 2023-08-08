package org.zeith.hammeranims.net;

import com.zeitheron.hammercore.net.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.*;

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
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setTag("Sys", tag);
		nbt.setTag("Src", source.writeSource());
		nbt.setString("Type", source.getType().getRegistryKey().toString());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		tag = nbt.getCompoundTag("Sys");
		AnimationSourceType type = HammerAnimationsApi.animationSources()
				.getValue(new ResourceLocation(nbt.getString("Type")));
		if(type != null)
			source = type.readSource(nbt.getCompoundTag("Src"));
		else
			HammerAnimations.LOG.warn("Unable to find animation source {}", nbt.getString("Type"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void executeOnClient2(PacketContext net)
	{
		World world = HammerAnimations.PROXY.getClientWorld();
		if(world != null)
		{
			IAnimatedObject object = source.get(world);
			if(object != null) object.getAnimationSystem().deserializeNBT(tag);
		}
	}
}