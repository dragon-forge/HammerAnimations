package org.zeith.hammeranims.core.contents.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;
import org.zeith.hammeranims.core.init.ContainersHA;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityBilly
		extends PathfinderMob
		implements IAnimatedEntity
{
	protected final AnimationSystem animations = AnimationSystem.create(this);
	
	public EntityBilly(EntityType<? extends EntityBilly> pEntityType, Level pLevel)
	{
		super(pEntityType, pLevel);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
		this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, Ingredient.of(Items.REDSTONE), false));
		this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}
	
	@Override
	public void tick()
	{
		animations.tick();
		super.tick();
		
		if(level.isClientSide) return;
		
		setCustomNameVisible(false);
		setCustomName(ContainersHA.BILLY_BLOCK.getName());
		
		animations.startAnimationAt(CommonLayerNames.AMBIENT, ContainersHA.BILLY_BREATHE);
		
		var pos = position();
		double moved = Math.sqrt(pos.distanceToSqr(xo, yo, zo));
		boolean posChanged = Math.abs(pos.x - xo) >= 0.00390625F || Math.abs(pos.z - zo) >= 0.00390625F;
		if(!posChanged) moved = 0;
		
		if(moved > 0)
		{
			animations.startAnimationAt(CommonLayerNames.LEGS, ContainersHA.BILLY_WALK);
		} else
		{
			animations.stopAnimation(CommonLayerNames.LEGS, 0.4F);
		}
	}
	
	@Override
	public void setupSystem(AnimationSystem.Builder builder)
	{
		builder.autoSync().addLayers(
				new AnimationLayer.Builder(CommonLayerNames.AMBIENT),
				new AnimationLayer.Builder(CommonLayerNames.LEGS)
		);
	}
	
	@Override
	public AnimationSystem getAnimationSystem()
	{
		return animations;
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag pCompound)
	{
		pCompound.put("Animations", animations.serializeNBT());
		super.addAdditionalSaveData(pCompound);
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag pCompound)
	{
		animations.deserializeNBT(pCompound.getCompound("Animations"));
		super.readAdditionalSaveData(pCompound);
	}
	
	@SubscribeEvent
	public static void attributes(EntityAttributeCreationEvent e)
	{
		e.put(ContainersHA.BILLY_ENTITY, Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).build());
	}
}