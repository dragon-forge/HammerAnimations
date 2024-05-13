package org.zeith.hammeranims.mixins;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zeith.hammeranims.api.particles.IEntityOlderMovement;

@Mixin(Entity.class)
@Implements({
		@Interface(iface = IEntityOlderMovement.class, prefix = "e$")
})
public class EntityMixin
{
	@Shadow
	public double xo;
	
	@Shadow
	public double yo;
	
	@Shadow
	public double zo;
	
	@Unique
	private double ha$prevPrevX, ha$prevPrevY, ha$prevPrevZ;
	
	@Inject(
			method = "baseTick",
			at = @At("HEAD")
	)
	private void HammerAnimations_baseTick(CallbackInfo ci)
	{
		ha$prevPrevX = xo;
		ha$prevPrevY = yo;
		ha$prevPrevZ = zo;
	}
	
	public double e$getPrevPrevX()
	{
		return ha$prevPrevX;
	}
	
	public double e$getPrevPrevY()
	{
		return ha$prevPrevY;
	}
	
	public double e$getPrevPrevZ()
	{
		return ha$prevPrevZ;
	}
}