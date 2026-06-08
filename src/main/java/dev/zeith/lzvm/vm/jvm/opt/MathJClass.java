package dev.zeith.lzvm.vm.jvm.opt;

import dev.zeith.lzvm.vm.jvm.BaseJClass;

public class MathJClass
		extends BaseJClass
{
	public MathJClass(String name)
	{
		super(name);
	}
	
	@Override
	protected void registerMethods()
	{
		registerDuOperator("sin", Math::sin);
		registerDuOperator("cos", Math::cos);
		registerDuOperator("tan", Math::tan);
		registerDuOperator("asin", Math::asin);
		registerDuOperator("acos", Math::acos);
		registerDuOperator("atan", Math::atan);
		registerDuOperator("toRadians", Math::toRadians);
		registerDuOperator("toDegrees", Math::toDegrees);
		registerDuOperator("exp", Math::exp);
		registerDuOperator("log", Math::log);
		registerDuOperator("log10", Math::log10);
		registerDuOperator("sqrt", Math::sqrt);
		registerDuOperator("cbrt", Math::cbrt);
		registerDbOperator("IEEEremainder", Math::IEEEremainder);
		registerDuOperator("ceil", Math::ceil);
		registerDuOperator("floor", Math::floor);
		registerDuOperator("rint", Math::rint);
		registerDbOperator("atan2", Math::atan2);
		registerDbOperator("pow", Math::pow);
		registerDuOperator("round", Math::round);
		registerDuOperator("abs", Math::abs);
		registerDbOperator("max", Math::max);
		registerDbOperator("min", Math::min);
		registerDuOperator("ulp", Math::ulp);
		registerDuOperator("signum", Math::signum);
		registerDuOperator("sinh", Math::sinh);
		registerDuOperator("cosh", Math::cosh);
		registerDuOperator("tanh", Math::tanh);
		registerDbOperator("hypot", Math::hypot);
		registerDuOperator("expm1", Math::expm1);
		registerDuOperator("log1p", Math::log1p);
		registerDbOperator("copySign", Math::copySign);
		registerDuOperator("getExponent", Math::getExponent);
		registerDbOperator("nextAfter", Math::nextAfter);
		registerDuOperator("nextUp", Math::nextUp);
		registerDuOperator("nextDown", Math::nextDown);
	}
}