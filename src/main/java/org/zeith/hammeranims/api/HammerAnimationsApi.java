package org.zeith.hammeranims.api;

import com.google.common.collect.Lists;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.animsys.actions.AnimationAction;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.time.TimeFunction;
import org.zeith.hammeranims.api.utils.IResourceProvider;

import java.util.*;
import java.util.function.Supplier;

import static org.zeith.hammerlib.util.java.Cast.constant;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class HammerAnimationsApi
{
	public static final IEventBus EVENT_BUS = BusBuilder.builder().build();
	
	static
	{
		Keys.init();
	}
	
	private static final List<IResourceProvider> AUXILIARY_RESOURCE_PROVIDERS = Lists.newArrayList();
	
	private static Supplier<IForgeRegistry<IAnimationContainer>> ANIMATION_CONTAINERS;
	private static Supplier<IForgeRegistry<IGeometryContainer>> GEOMETRY_CONTAINERS;
	private static Supplier<IForgeRegistry<TimeFunction>> TIME_FUNCTIONS;
	private static Supplier<IForgeRegistry<AnimationAction>> ANIMATION_ACTIONS;
	private static boolean hasInitialized = false;
	
	@SubscribeEvent
	public static void newRegistries(RegistryEvent.NewRegistry e)
	{
		ANIMATION_CONTAINERS = constant(new RegistryBuilder<IAnimationContainer>()
				.setType(IAnimationContainer.class)
				.setName(HammerAnimations.id("animations"))
				.disableSaving()
				.create()
		);
		
		GEOMETRY_CONTAINERS = constant(new RegistryBuilder<IGeometryContainer>()
				.setType(IGeometryContainer.class)
				.setName(HammerAnimations.id("geometry"))
				.disableSaving()
				.create()
		);
		
		TIME_FUNCTIONS = constant(new RegistryBuilder<TimeFunction>()
				.setType(TimeFunction.class)
				.setName(HammerAnimations.id("time_functions"))
				.disableSaving()
				.setDefaultKey(HammerAnimations.id("linear"))
				.create()
		);
		
		ANIMATION_ACTIONS = constant(new RegistryBuilder<AnimationAction>()
				.setType(AnimationAction.class)
				.setName(HammerAnimations.id("animation_actions"))
				.disableSaving()
				.setDefaultKey(HammerAnimations.id("empty"))
				.create()
		);
		
		hasInitialized = true;
	}
	
	public static boolean hasInitialized()
	{
		return hasInitialized;
	}
	
	public static void addAuxiliaryResourceProvider(IResourceProvider provider)
	{
		AUXILIARY_RESOURCE_PROVIDERS.add(provider);
	}
	
	public static List<IResourceProvider> getAuxiliaryResourceProviders()
	{
		return Collections.unmodifiableList(AUXILIARY_RESOURCE_PROVIDERS);
	}
	
	public static IForgeRegistry<IAnimationContainer> animations()
	{
		return ANIMATION_CONTAINERS.get();
	}
	
	public static IForgeRegistry<IGeometryContainer> geometries()
	{
		return GEOMETRY_CONTAINERS.get();
	}
	
	public static IForgeRegistry<TimeFunction> timeFunctions()
	{
		return TIME_FUNCTIONS.get();
	}
	
	public static IForgeRegistry<AnimationAction> animationActions()
	{
		return ANIMATION_ACTIONS.get();
	}
	
	public static class Keys
	{
		public static final RegistryKey<Registry<IAnimationContainer>> ANIMATION_CONTAINERS = key("animations");
		public static final RegistryKey<Registry<IGeometryContainer>> GEOMETRY_CONTAINERS = key("geometry");
		public static final RegistryKey<Registry<TimeFunction>> TIME_FUNCTIONS = key("time_functions");
		public static final RegistryKey<Registry<AnimationAction>> ANIMATION_ACTIONS = key("animation_actions");
		
		private static <T> RegistryKey<Registry<T>> key(String name)
		{
			return RegistryKey.createRegistryKey(new ResourceLocation(name));
		}
		
		private static void init()
		{
		}
	}
}