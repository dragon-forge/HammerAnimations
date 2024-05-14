package org.zeith.hammeranims.core.contents.commands;

import com.zeitheron.hammercore.net.HCNet;
import lombok.var;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.server.command.CommandTreeBase;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammeranims.joml.Vector3d;
import org.zeith.hammeranims.net.PacketPlayParticleEffectAtPos;
import org.zeith.hammeranims.net.PacketProvideCustomParticleEffectList;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class CommandParticle
		extends CommandTreeBase
{
	public static final Map<UUID, CompletableFuture<Set<ResourceLocation>>> PLAYER_CUSTOM_MAP = new ConcurrentHashMap<>();
	
	public static CompletableFuture<Set<ResourceLocation>> getOrRequest(EntityPlayerMP player)
	{
		return PLAYER_CUSTOM_MAP.computeIfAbsent(player.getGameProfile().getId(), uid ->
		{
			HCNet.INSTANCE.sendTo(new PacketProvideCustomParticleEffectList(), player);
			return new CompletableFuture<>();
		});
	}
	
	public static CompletableFuture<Set<ResourceLocation>> getOrUnresolved(EntityPlayerMP player)
	{
		if(player == null) return null;
		return PLAYER_CUSTOM_MAP.computeIfAbsent(player.getGameProfile().getId(), uid -> new CompletableFuture<>());
	}
	
	public CommandParticle()
	{
		addSubcommand(new Spawn());
	}
	
	@Override
	public String getName()
	{
		return "particle";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/bedrockmc particle spawn hammeranims:poof ~ ~ ~";
	}
	
	static class Spawn
			extends CommandBase
	{
		@Override
		public String getName()
		{
			return "spawn";
		}
		
		@Override
		public String getUsage(ICommandSender sender)
		{
			return "/bedrockmc particle spawn hammeranims:poof ~ ~ ~";
		}
		
		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args)
				throws CommandException
		{
			if(args.length < 1) throw new CommandException("command.hammeranims:particle_effect.nofx");
			if(args.length > 1 && args.length < 4) throw new CommandException("command.hammeranims:particle_effect.nopos");
			ResourceLocation id = InstanceHelpers.tryParseLocation(args[0].toLowerCase(Locale.ROOT));
			sender.sendMessage(new TextComponentTranslation("command.hammeranims:particle_effect.spawned", id.toString()));
			
			var mcpos = sender.getPositionVector();
			Vector3d pos = new Vector3d(mcpos.x, mcpos.y, mcpos.z);
			
			if(args.length > 1)
			{
				pos.set(
						parseCoordinate(mcpos.x, args[1], true).getResult(),
						parseCoordinate(mcpos.y, args[2], -4096, 4096, false).getResult(),
						parseCoordinate(mcpos.z, args[3], true).getResult()
				);
			}
			
			HCNet.INSTANCE.sendToAllAroundTracking(new PacketPlayParticleEffectAtPos(pos, id), HCNet.point(sender.getEntityWorld(),
					new Vec3d(pos.x, pos.y, pos.z),
					1
			));
		}
		
		@Override
		public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
		{
			if(args.length == 1)
			{
				List<ResourceLocation> all = new ArrayList<>();
				
				all.addAll(HammerAnimationsApi.particleContainers().getKeys());
				
				try
				{
					CompletableFuture<Set<ResourceLocation>> f = getOrRequest(getCommandSenderAsPlayer(sender));
					if(f.isDone()) all.addAll(f.join());
				} catch(PlayerNotFoundException e)
				{
				}
				
				return getListOfStringsMatchingLastWord(args, all);
			}
			
			return Collections.emptyList();
		}
	}
}