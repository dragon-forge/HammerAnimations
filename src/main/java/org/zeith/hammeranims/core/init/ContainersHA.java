package org.zeith.hammeranims.core.init;

import net.minecraft.entity.*;
import net.minecraft.tileentity.TileEntityType;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.core.client.render.tile.RenderTileBilly;
import org.zeith.hammeranims.core.contents.actions.PrintHelloWorldAction;
import org.zeith.hammeranims.core.contents.blocks.*;
import org.zeith.hammeranims.core.contents.entity.EntityBilly;
import org.zeith.hammerlib.annotations.*;
import org.zeith.hammerlib.api.forge.TileAPI;

@SimplyRegister
public interface ContainersHA
{
	@RegistryName("billy")
	EntityType<EntityBilly> BILLY_ENTITY = EntityType.Builder.of(EntityBilly::new, EntityClassification.MISC).sized(0.75F, 1F).build("billy");
	
	@RegistryName("billy")
	BlockBilly BILLY_BLOCK = new BlockBilly();
	
	@RegistryName("billy")
	@TileRenderer(RenderTileBilly.class)
	TileEntityType<TileBilly> BILLY_TILE = TileAPI.createType(TileBilly::new, BILLY_BLOCK);
	
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