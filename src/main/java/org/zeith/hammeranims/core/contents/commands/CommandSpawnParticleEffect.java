package org.zeith.hammeranims.core.contents.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.var;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zeith.hammeranims.joml.Vector3d;
import org.zeith.hammeranims.net.PacketPlayParticleEffectAtPos;
import org.zeith.hammerlib.net.Network;

import javax.swing.border.CompoundBorder;

@Mod.EventBusSubscriber
public class CommandSpawnParticleEffect
{
	@SubscribeEvent
	public static void registerCmd(RegisterCommandsEvent e)
	{
		e.getDispatcher().register(register());
	}
	
	public static LiteralArgumentBuilder<CommandSource> register()
	{
		return Commands.literal("particle_effect")
				.then(Commands.argument("effect", ResourceLocationArgument.id())
						.executes(cs ->
						{
							ResourceLocation id = ResourceLocationArgument.getId(cs, "effect");
							var mcpos = cs.getSource().getPosition();
							
							Vector3d pos = new Vector3d(mcpos.x, mcpos.y, mcpos.z);
							
							var chunk = cs.getSource().getLevel().getChunkAt(new BlockPos(mcpos));
							Network.sendToTracking(new PacketPlayParticleEffectAtPos(pos, id), chunk);
							
							return 1;
						})
						.then(Commands.argument("pos", Vec3Argument.vec3(true))
								.executes(cs ->
								{
									ResourceLocation id = ResourceLocationArgument.getId(cs, "effect");
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