package org.zeith.hammeranims.core.molang;

import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.animation.scope.VarSymbol;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;
import org.zeith.hammeranims.molang.*;
import org.zeith.hammeranims.molang.runtime.*;
import org.zeith.hammeranims.molang.runtime.struct.*;
import org.zeith.hammeranims.molang.runtime.value.DoubleSupplierValue;

import java.util.List;
import java.util.function.Supplier;

public class MolangExpressionParser
{
	public static final VarSymbol<MoLangRuntime> RUNTIME_SYMBOL = VarSymbol.of("molang_runtime");
	public static final Supplier<MoLangRuntime> RUNTIME_FACTORY = () -> MoLang.createRuntime(false);
	
	private static final String[][] PARTICLE_POS = {
			{"particle_pos", "x"},
			{"particle_pos", "y"},
			{"particle_pos", "z"},
			{"particle_pos", "distance"}
	};
	
	private static final String[][] PARTICLE_SPEED = {
			{"particle_speed", "x"},
			{"particle_speed", "y"},
			{"particle_speed", "z"},
			{"particle_speed", "distance"}
	};
	
	public static <T extends IVariableAccess> InterpolatedDouble<T> parse(String expression)
	{
		expression = ExpressionFixer.fixExpression(expression);
		
		// Try parsing expression as constant first.
		try
		{
			return InterpolatedDouble.constant(Double.parseDouble(expression));
		} catch(Throwable e)
		{
		}
		
		List<Expression> exprs = MoLang.parse(expression);
		
		return vars -> vars
				.getVariables()
				.computeIfAbsent(RUNTIME_SYMBOL, RUNTIME_FACTORY)
				.execute(exprs)
				.asDouble();
	}
	
	public static void initializeQuery(Query query)
	{
		MoLangRuntime runtime = query.getVariables().computeIfAbsent(RUNTIME_SYMBOL, RUNTIME_FACTORY);
		MoLangEnvironment env = runtime.getEnvironment();
		QueryStruct q = env.query;
		q.setFunction("anim_duration", (a) -> query.anim_duration);
		q.setFunction("anim_time", (a) -> query.anim_time);
		q.setFunction("anim_length", (a) -> query.anim_length);
	}
	
	public static void initializeParticleVariables(ParticleVariables vars)
	{
		MoLangRuntime runtime = vars.getVariables().computeIfAbsent(RUNTIME_SYMBOL, RUNTIME_FACTORY);
		MoLangEnvironment env = runtime.getEnvironment();
		VariableStruct v = env.variable;
		
		v.setValue("emitter_age", new DoubleSupplierValue(() -> vars.emitter_age));
		v.setValue("emitter_lifetime", new DoubleSupplierValue(() -> vars.emitter_lifetime));
		v.setValue("emitter_random_1", new DoubleSupplierValue(() -> vars.emitter_random_1));
		v.setValue("emitter_random_2", new DoubleSupplierValue(() -> vars.emitter_random_2));
		v.setValue("emitter_random_3", new DoubleSupplierValue(() -> vars.emitter_random_3));
		v.setValue("emitter_random_4", new DoubleSupplierValue(() -> vars.emitter_random_4));
		v.setValue(PARTICLE_POS[0], new DoubleSupplierValue(() -> vars.particle_pos.x));
		v.setValue(PARTICLE_POS[1], new DoubleSupplierValue(() -> vars.particle_pos.y));
		v.setValue(PARTICLE_POS[2], new DoubleSupplierValue(() -> vars.particle_pos.z));
		v.setValue(PARTICLE_POS[3], new DoubleSupplierValue(() -> vars.particle_pos.distance));
		v.setValue(PARTICLE_SPEED[0], new DoubleSupplierValue(() -> vars.particle_speed.x));
		v.setValue(PARTICLE_SPEED[1], new DoubleSupplierValue(() -> vars.particle_speed.y));
		v.setValue(PARTICLE_SPEED[2], new DoubleSupplierValue(() -> vars.particle_speed.z));
		v.setValue(PARTICLE_SPEED[3], new DoubleSupplierValue(() -> vars.particle_speed.distance));
		v.setValue("entity_scale", new DoubleSupplierValue(() -> vars.entity_scale));
		v.setValue("particle_age", new DoubleSupplierValue(() -> vars.particle_age));
		v.setValue("particle_lifetime", new DoubleSupplierValue(() -> vars.particle_lifetime));
		v.setValue("particle_random_1", new DoubleSupplierValue(() -> vars.particle_random_1));
		v.setValue("particle_random_2", new DoubleSupplierValue(() -> vars.particle_random_2));
		v.setValue("particle_random_3", new DoubleSupplierValue(() -> vars.particle_random_3));
		v.setValue("particle_random_4", new DoubleSupplierValue(() -> vars.particle_random_4));
		v.setValue("particle_bounces", new DoubleSupplierValue(() -> vars.particle_bounces));
	}
	
	public static IVariableStorage getMolangStorage(IVariableAccess vars)
	{
		MoLangRuntime runtime = vars.getVariables().computeIfAbsent(RUNTIME_SYMBOL, RUNTIME_FACTORY);
		return runtime.getEnvironment();
	}
}