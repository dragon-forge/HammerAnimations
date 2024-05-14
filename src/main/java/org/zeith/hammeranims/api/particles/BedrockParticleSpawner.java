package org.zeith.hammeranims.api.particles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammeranims.net.PacketPlayParticleEffectAtObject;
import org.zeith.hammeranims.net.PacketPlayParticleEffectAtPos;
import org.zeith.hammerlib.net.Network;

public class BedrockParticleSpawner
{
	public static void spawnAt(ServerWorld world, Vector3d pos, IParticleContainer effect)
	{
		spawnAt(world, pos, effect.getRegistryKey());
	}
	
	public static void spawnAt(ServerWorld world, Vector3d pos, ResourceLocation effect)
	{
		BlockPos bpos = new BlockPos(pos);
		if(!world.isLoaded(bpos)) return;
		Network.sendToTracking(world.getChunkAt(bpos),
				new PacketPlayParticleEffectAtPos(new org.zeith.hammeranims.joml.Vector3d(pos.x, pos.y, pos.z), effect)
		);
	}
	
	public static void spawnAt(IAnimatedObject pos, IParticleContainer effect)
	{
		spawnAt(pos, effect.getRegistryKey());
	}
	
	public static void spawnAt(IAnimatedObject pos, ResourceLocation effect)
	{
		World world = pos.getAnimatedObjectWorld();
		if(world.isClientSide()) return;
		BlockPos bpos = new BlockPos(pos.getAnimatedObjectPosition());
		if(!world.isLoaded(bpos)) return;
		Network.sendToTracking(world.getChunkAt(bpos),
				new PacketPlayParticleEffectAtObject(pos, effect)
		);
	}
}