package org.zeith.hammeranims.api.particles;

import com.zeitheron.hammercore.net.HCNet;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammeranims.net.PacketPlayParticleEffectAtObject;
import org.zeith.hammeranims.net.PacketPlayParticleEffectAtPos;

public class BedrockParticleSpawner
{
	public static void spawnAt(WorldServer world, Vec3d pos, IParticleContainer effect)
	{
		spawnAt(world, pos, effect.getRegistryKey());
	}
	
	public static void spawnAt(WorldServer world, Vec3d pos, ResourceLocation effect)
	{
		BlockPos bpos = new BlockPos(pos);
		if(!world.isBlockLoaded(bpos)) return;
		HCNet.INSTANCE.sendToAllAroundTracking(
				new PacketPlayParticleEffectAtPos(new org.zeith.hammeranims.joml.Vector3d(pos.x, pos.y, pos.z), effect),
				HCNet.point(world, pos, 128)
		);
	}
	
	public static void spawnAt(IAnimatedObject pos, IParticleContainer effect)
	{
		spawnAt(pos, effect.getRegistryKey());
	}
	
	public static void spawnAt(IAnimatedObject pos, ResourceLocation effect)
	{
		World world = pos.getAnimatedObjectWorld();
		if(world.isRemote) return;
		BlockPos bpos = new BlockPos(pos.getAnimatedObjectPosition());
		if(!world.isBlockLoaded(bpos)) return;
		HCNet.INSTANCE.sendToAllAroundTracking(
				new PacketPlayParticleEffectAtObject(pos, effect),
				HCNet.point(world, pos.getAnimatedObjectPosition(), 128)
		);
	}
}