package org.zeith.hammeranims.core.molang;

import dev.zeith.lzvm.jvm.*;
import dev.zeith.lzvm.molang.compiler.MoLangCompiler;
import dev.zeith.lzvm.molang.expression.MLExpression;
import dev.zeith.lzvm.program.LzProgramBody;
import lombok.extern.slf4j.Slf4j;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.event.ReloadHammerAnimationsEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Slf4j
public class MolangExpressionParser
{
	static IClassDefiner CLASS_LOADER = new LzJVM.LzClassLoader();
	static final LzJvmCompiler JVM_COMPILER = new LzJvmCompiler();
	static final MoLangCompiler MOLANG_COMPILER = new MoLangCompiler();
	static Map<String, FactoryReference> CACHE = new ConcurrentHashMap<>();
	
	static
	{
		HammerAnimationsApi.EVENT_BUS.register(MolangExpressionParser.class);
		
		// Include minimal set of classes required to provide functionality.
		Set<String> permittedClasses = new HashSet<>();
		MOLANG_COMPILER.includeRequiredClasses(permittedClasses);
		JVM_COMPILER.addJCallShutter(new ClassSetJCallShutter(permittedClasses));
	}
	
	public static LzFactory parse(String expression)
	{
		// Constant expressions don't need caching since they don't involve any class generation.
		ArrayList<MLExpression> parsed = MOLANG_COMPILER.parse(expression);
		if(parsed.size() == 1)
		{
			OptionalDouble exp = parsed.get(0).asOptimizedDouble();
			if(exp.isPresent()) return new ConstantExpression(exp.getAsDouble());
		}
		
		LzProgramBody program = MOLANG_COMPILER.compile(parsed);
		return CACHE.computeIfAbsent(program.disassemble(false),
				k -> new FactoryReference(LzJVM.compile(JVM_COMPILER, program, 0, CLASS_LOADER))
		).get();
	}
	
	public static int getDedupedExpressions()
	{
		return CACHE.values().stream().mapToInt(f -> f.dupCounter.get() - 1).sum();
	}
	
	@SubscribeEvent
	public static void reload(ReloadHammerAnimationsEvent e)
	{
		CLASS_LOADER = new LzJVM.LzClassLoader();
		CACHE.clear();
	}
	
	@SubscribeEvent
	public static void postReload(ReloadHammerAnimationsEvent.Post e)
	{
		log.info("Parsed {} expressions, {} deduped.", CACHE.size(), getDedupedExpressions());
	}
	
	static class FactoryReference
			implements Supplier<LzFactory>
	{
		final AtomicInteger dupCounter = new AtomicInteger();
		final LzFactory factory;
		
		FactoryReference(LzFactory factory)
		{
			this.factory = factory;
		}
		
		@Override
		public LzFactory get()
		{
			dupCounter.incrementAndGet();
			return factory;
		}
	}
}