package org.zeith.hammeranims.core.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.core.client.render.tile.RenderTileBilly;
import org.zeith.hammeranims.core.contents.actions.PrintHelloWorldAction;
import org.zeith.hammeranims.core.contents.blocks.*;
import org.zeith.hammerlib.annotations.*;
import org.zeith.hammerlib.annotations.client.TileRenderer;
import org.zeith.hammerlib.api.forge.BlockAPI;

@SimplyRegister
public interface ContainersHA
{
	@RegistryName("billy")
	BlockBilly BILLY_BLOCK = new BlockBilly();
	
	@RegistryName("billy")
	@TileRenderer(RenderTileBilly.class)
	BlockEntityType<TileBilly> BILLY_TILE = BlockAPI.createBlockEntityType(TileBilly::new, BILLY_BLOCK);
	
	@RegistryName("billy")
	IAnimationContainer BILLY_ANIM = IAnimationContainer.create();
	
	@RegistryName("billy_breathe")
	IAnimationContainer BILLY_BREATHE = IAnimationContainer.create();
	
	@RegistryName("billy")
	IGeometryContainer BILLY_GEOM = IGeometryContainer.create();
	
	@RegistryName("hello_world")
	PrintHelloWorldAction HELLO_WORLD_ACTION = new PrintHelloWorldAction();
	
	AnimationHolder BILLY_WALK = new AnimationHolder(BILLY_ANIM, "walk");
}