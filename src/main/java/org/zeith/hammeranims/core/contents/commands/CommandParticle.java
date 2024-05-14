package org.zeith.hammeranims.core.contents.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammeranims.joml.Vector3d;
import org.zeith.hammeranims.net.PacketPlayParticleEffectAtPos;
import org.zeith.hammeranims.net.PacketProvideCustomParticleEffectList;
import org.zeith.hammerlib.net.Network;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class CommandParticle
{
	public static final Map<UUID, CompletableFuture<Set<ResourceLocation>>> PLAYER_CUSTOM_MAP = new ConcurrentHashMap<>();
	
	public static CompletableFuture<Set<ResourceLocation>> getOrRequest(ServerPlayer player)
	{
		return PLAYER_CUSTOM_MAP.computeIfAbsent(player.getGameProfile().getId(), uid ->
		{
			Network.sendTo(new PacketProvideCustomParticleEffectList(), player);
			return new CompletableFuture<>();
		});
	}
	
	public static CompletableFuture<Set<ResourceLocation>> getOrUnresolved(ServerPlayer player)
	{
		if(player == null) return null;
		return PLAYER_CUSTOM_MAP.computeIfAbsent(player.getGameProfile().getId(), uid -> new CompletableFuture<>());
	}
	
	public static SuggestionProvider<CommandSourceStack> effectSuggestor()
	{
		return (cs, b) ->
		{
			HammerAnimationsApi.particleContainers().getKeys().stream().map(Objects::toString).forEach(b::suggest);
			return getOrRequest(cs.getSource().getPlayerOrException()).thenApply(custom ->
			{
				custom.stream().map(Objects::toString).forEach(b::suggest);
				return b.build();
			});
		};
	}
	
	public static LiteralArgumentBuilder<CommandSourceStack> register()
	{
		return Commands.literal("particle")
				.then(Commands.literal("spawn")
						.then(Commands.argument("effect", ResourceLocationArgument.id()).suggests(effectSuggestor())
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
						)
				)
				.then(Commands.literal("hasplayer")
						.then(Commands.argument("players", EntityArgument.players())
								.then(Commands.argument("effect", ResourceLocationArgument.id()).suggests(effectSuggestor())
										.executes(cs ->
										{
											ResourceLocation id = ResourceLocationArgument.getId(cs, "effect");
											Collection<ServerPlayer> players = EntityArgument.getPlayers(cs, "players");
											int has = (int) players.stream()
													.map(CommandParticle::getOrRequest)
													.filter(CompletableFuture::isDone)
													.map(CompletableFuture::join)
													.filter(s -> s.contains(id))
													.count();
											if(has > 0)
												cs.getSource().sendSuccess(InstanceHelpers.componentTranslate("command.hammeranims:has_particle_effect", has, id), true);
											else
												cs.getSource().sendFailure(InstanceHelpers.componentTranslate("command.hammeranims:has_particle_effect", has, id));
											return has;
										})
								)
						)
				);
	}
}