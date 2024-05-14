package org.zeith.hammeranims.api.particles;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammeranims.net.PacketPlayParticleEffectAtObject;
import org.zeith.hammeranims.net.PacketPlayParticleEffectAtPos;
import org.zeith.hammerlib.net.Network;

public class BedrockParticleSpawner
{
	public static void spawnAt(ServerLevel world, Vec3 pos, IParticleContainer effect)
	{
		spawnAt(world, pos, effect.getRegistryKey());
	}
	
	public static void spawnAt(ServerLevel world, Vec3 pos, ResourceLocation effect)
	{
		var bpos = BlockPos.containing(pos);
		if(!world.isLoaded(bpos)) return;
		Network.sendToTracking(world.getChunkAt(bpos),
				new PacketPlayParticleEffectAtPos(new Vector3d(pos.x, pos.y, pos.z), effect)
		);
	}
	
	public static void spawnAt(IAnimatedObject pos, IParticleContainer effect)
	{
		spawnAt(pos, effect.getRegistryKey());
	}
	
	public static void spawnAt(IAnimatedObject pos, ResourceLocation effect)
	{
		var world = pos.getAnimatedObjectWorld();
		if(world.isClientSide()) return;
		BlockPos bpos = BlockPos.containing(pos.getAnimatedObjectPosition());
		if(!world.isLoaded(bpos)) return;
		Network.sendToTracking(world.getChunkAt(bpos),
				new PacketPlayParticleEffectAtObject(pos, effect)
		);
	}
}