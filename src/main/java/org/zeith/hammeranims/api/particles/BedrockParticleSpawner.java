package org.zeith.hammeranims.api.particles;

import com.zeitheron.hammercore.net.HCNet;
import lombok.var;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.WorldServer;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammeranims.joml.Vector3d;
import org.zeith.hammeranims.net.*;

public class BedrockParticleSpawner
{
	public static void spawnAt(WorldServer world, Vec3d pos, IParticleContainer effect)
	{
		spawnAt(world, pos, effect.getRegistryKey());
	}
	
	public static void spawnAt(WorldServer world, Vec3d pos, ResourceLocation effect)
	{
		var bpos = new BlockPos(pos);
		if(!world.isBlockLoaded(bpos)) return;
		HCNet.INSTANCE.sendToAllAroundTracking(
				new PacketPlayParticleEffectAtPos(new Vector3d(pos.x, pos.y, pos.z), effect),
				HCNet.point(world, pos, 128)
		);
	}
	
	public static void spawnAt(IAnimatedObject pos, IParticleContainer effect)
	{
		spawnAt(pos, effect.getRegistryKey());
	}
	
	public static void spawnAt(IAnimatedObject pos, ResourceLocation effect)
	{
		var world = pos.getAnimatedObjectWorld();
		if(world.isRemote) return;
		var bpos = new BlockPos(pos.getAnimatedObjectPosition());
		if(!world.isBlockLoaded(bpos)) return;
		HCNet.INSTANCE.sendToAllAroundTracking(
				new PacketPlayParticleEffectAtObject(pos, effect),
				HCNet.point(world, pos.getAnimatedObjectPosition(), 128)
		);
	}
}