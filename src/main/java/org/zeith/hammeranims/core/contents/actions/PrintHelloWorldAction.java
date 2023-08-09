package org.zeith.hammeranims.core.contents.actions;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.*;
import org.zeith.hammeranims.api.animsys.actions.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import javax.annotation.Nonnull;

public class PrintHelloWorldAction
		extends AnimationAction
{
	@Override
	public HelloWorldActionInstance defaultInstance()
	{
		return createInstance();
	}
	
	@Nonnull
	@Override
	protected HelloWorldActionInstance createInstance()
	{
		return new HelloWorldActionInstance(this);
	}
	
	@Override
	public void execute(AnimationActionInstance instance, AnimationLayer layer)
	{
		World world = getAnimationWorld(layer);
		
		String msg = Cast.optionally(instance, HelloWorldActionInstance.class)
				.map(HelloWorldActionInstance::message)
				.orElse("");
		
		if(world instanceof WorldServer)
			world.getMinecraftServer().getPlayerList().sendMessage(InstanceHelpers.componentText(
					"Hello from server! Owner " + layer.system.owner + "@" + getAnimationPos(layer) +
							" has finished animation " +
							layer.getCurrentAnimation().getLocation() + " with message: " + msg), false);
		else
		{
			for(EntityPlayer player : world.playerEntities)
			{
				player.sendMessage(InstanceHelpers.componentText(
						"Hello from client! Owner " + layer.system.owner + "@" + getAnimationPos(layer) +
								" has finished animation " +
								layer.getCurrentAnimation().getLocation() + " with message: " + msg)
				);
			}
		}
	}
	
	public static class HelloWorldActionInstance
			extends AnimationActionInstance
	{
		public String message = "";
		
		public HelloWorldActionInstance(AnimationAction action)
		{
			super(action);
		}
		
		public String message()
		{
			return message;
		}
		
		public HelloWorldActionInstance withMessage(String message)
		{
			this.message = message;
			return this;
		}
		
		@Override
		public NBTTagCompound serializeNBT()
		{
			NBTTagCompound tag = super.serializeNBT();
			tag.setString("Msg", message);
			return tag;
		}
		
		@Override
		public void deserializeNBT(NBTTagCompound nbt)
		{
			message = nbt.getString("Msg");
			super.deserializeNBT(nbt);
		}
	}
}