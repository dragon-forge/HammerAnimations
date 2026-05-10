package org.zeith.hammeranims.core.init;

import net.minecraft.world.entity.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.core.client.render.tile.RenderTileBilly;
import org.zeith.hammeranims.core.contents.actions.*;
import org.zeith.hammeranims.core.contents.blocks.*;
import org.zeith.hammeranims.core.contents.entity.EntityBilly;
import org.zeith.hammerlib.annotations.*;
import org.zeith.hammerlib.annotations.client.TileRenderer;
import org.zeith.hammerlib.api.forge.BlockAPI;

@SimplyRegister
public interface ContainersHA
{
	@RegistryName("billy")
	EntityType<EntityBilly> BILLY_ENTITY = EntityType.Builder.of(EntityBilly::new, MobCategory.MISC).sized(0.75F, 1F).build("billy");
	
	@RegistryName("billy")
	BlockBilly BILLY_BLOCK = new BlockBilly(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS));
	
	@RegistryName("billy")
	@TileRenderer(RenderTileBilly.class)
	BlockEntityType<TileBilly> BILLY_TILE = BlockAPI.createBlockEntityType(TileBilly::new, BILLY_BLOCK);
	
	@RegistryName("billy")
	IAnimationContainer BILLY_ANIM = IAnimationContainer.create();
	
	@RegistryName("billy_breathe")
	IAnimationContainer BILLY_BREATHE = IAnimationContainer.create();
	
	@RegistryName("billy")
	IGeometryContainer BILLY_GEOM = IGeometryContainer.create();
	
	@RegistryName("method_call")
	MethodAnimAction METHOD_CALL = new MethodAnimAction();
	
	@RegistryName("rainbow")
	IParticleContainer RAINBOW_PARTICLES = IParticleContainer.create();
	
	AnimationHolder BILLY_WALK = new AnimationHolder(BILLY_ANIM, "walk");
}