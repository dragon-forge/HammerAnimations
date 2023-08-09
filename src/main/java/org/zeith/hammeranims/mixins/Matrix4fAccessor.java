package org.zeith.hammeranims.mixins;

import com.mojang.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Matrix4f.class)
public interface Matrix4fAccessor
{
	@Accessor("m00")
	void setM00(float m00);
}
