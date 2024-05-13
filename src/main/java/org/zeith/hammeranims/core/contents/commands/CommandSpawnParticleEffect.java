package org.zeith.hammeranims.core.contents.commands;

import com.zeitheron.hammercore.net.HCNet;
import lombok.var;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammeranims.joml.Vector3d;
import org.zeith.hammeranims.net.PacketPlayParticleEffectAtPos;

import java.util.Locale;

public class CommandSpawnParticleEffect
		extends CommandBase
{
	@Override
	public String getName()
	{
		return "particle_effect";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/particle_effect hammeranims:poof ~ ~ ~";
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
			CommandBase.CoordinateArg x = parseCoordinate(mcpos.x, args[1], true);
			CommandBase.CoordinateArg y = parseCoordinate(mcpos.y, args[2], -4096, 4096, false);
			CommandBase.CoordinateArg z = parseCoordinate(mcpos.z, args[3], true);
			
			pos.set(
					x.getResult(),
					y.getResult(),
					z.getResult()
			);
		}
		
		HCNet.INSTANCE.sendToAllAroundTracking(new PacketPlayParticleEffectAtPos(pos, id), HCNet.point(sender.getEntityWorld(),
				new Vec3d(pos.x, pos.y, pos.z),
				1
		));
	}
}