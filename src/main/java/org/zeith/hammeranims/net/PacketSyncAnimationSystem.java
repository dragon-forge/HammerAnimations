package org.zeith.hammeranims.net;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammerlib.net.*;

@MainThreaded
public class PacketSyncAnimationSystem
		implements INBTPacket
{
	protected CompoundTag tag;
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
	public void write(CompoundTag nbt)
	{
		nbt.put("Sys", tag);
		nbt.put("Src", source.writeSource());
		nbt.putString("Type", source.getType().getRegistryKey().toString());
	}
	
	@Override
	public void read(CompoundTag nbt)
	{
		tag = nbt.getCompound("Sys");
		AnimationSourceType type = HammerAnimationsApi.animationSources()
				.getValue(new ResourceLocation(nbt.getString("Type")));
		if(type != null)
			source = type.readSource(nbt.getCompound("Src"));
		else
			HammerAnimations.LOG.warn("Unable to find animation source {}", nbt.getString("Type"));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext ctx)
	{
		Level world = HammerAnimations.PROXY.getClientWorld();
		if(world != null)
		{
			IAnimatedObject object = source.get(world);
			if(object != null) object.getAnimationSystem().deserializeNBT(tag);
		}
	}
}