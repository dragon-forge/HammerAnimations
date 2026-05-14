package org.zeith.hammeranims.core.molang.jvm;

public class MathClass
		extends BaseJvmClass
{
	public MathClass(String name)
	{
		super(name);
	}
	
	@Override
	protected void registerMethods()
	{
		registerDuOperator("acos", Math::acos);
		registerDuOperator("asin", Math::asin);
		registerDuOperator("atan", Math::atan);
		registerDuOperator("exp", Math::exp);
		registerDuOperator("abs", Math::abs);
		registerDuOperator("sqrt", Math::sqrt);
		registerDuOperator("ceil", Math::ceil);
		registerDuOperator("log", Math::log);
		registerDuOperator("floor", Math::floor);
		registerDbOperator("copySign", Math::copySign);
		registerDbOperator("atan2", Math::atan2);
		registerDbOperator("pow", Math::pow);
	}
}