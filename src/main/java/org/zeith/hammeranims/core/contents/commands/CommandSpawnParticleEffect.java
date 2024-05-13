package org.zeith.hammeranims.core.contents.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zeith.hammeranims.joml.Vector3d;
import org.zeith.hammeranims.net.PacketPlayParticleEffectAtPos;
import org.zeith.hammerlib.net.Network;

@Mod.EventBusSubscriber
public class CommandSpawnParticleEffect
{
	@SubscribeEvent
	public static void registerCmd(RegisterCommandsEvent e)
	{
		e.getDispatcher().register(register());
	}
	
	public static LiteralArgumentBuilder<CommandSourceStack> register()
	{
		return Commands.literal("particle_effect")
				.then(Commands.argument("effect", ResourceLocationArgument.id())
						.executes(cs ->
						{
							var id = ResourceLocationArgument.getId(cs, "effect");
							var mcpos = cs.getSource().getPosition();
							
							Vector3d pos = new Vector3d(mcpos.x, mcpos.y, mcpos.z);
							
							var chunk = cs.getSource().getLevel().getChunkAt(new BlockPos(mcpos));
							Network.sendToTracking(new PacketPlayParticleEffectAtPos(pos, id), chunk);
							
							return 1;
						})
						.then(Commands.argument("pos", Vec3Argument.vec3(true))
								.executes(cs ->
								{
									var id = ResourceLocationArgument.getId(cs, "effect");
									var mcpos = Vec3Argument.getVec3(cs, "pos");
									
									Vector3d pos = new Vector3d(mcpos.x, mcpos.y, mcpos.z);
									
									var chunk = cs.getSource().getLevel().getChunkAt(new BlockPos(mcpos));
									Network.sendToTracking(new PacketPlayParticleEffectAtPos(pos, id), chunk);
									
									return 1;
								})
						)
				);
	}
}