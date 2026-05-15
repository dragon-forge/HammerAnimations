package dev.zeith.lzvm.molang.compiler.jclass;

import dev.zeith.lzvm.molang.compiler.libs.MoLangEasing;
import dev.zeith.lzvm.vm.jvm.BaseJClass;

public class MoEasingJClass
		extends BaseJClass
{
	public MoEasingJClass(String name)
	{
		super(name);
	}
	
	@Override
	protected void registerMethods()
	{
		registerDtOperator("easeInBack", MoLangEasing::easeInBack);
		registerDtOperator("easeInBounce", MoLangEasing::easeInBounce);
		registerDtOperator("easeInCirc", MoLangEasing::easeInCirc);
		registerDtOperator("easeInCubic", MoLangEasing::easeInCubic);
		registerDtOperator("easeInElastic", MoLangEasing::easeInElastic);
		registerDtOperator("easeInExpo", MoLangEasing::easeInExpo);
		registerDtOperator("easeInOutBack", MoLangEasing::easeInOutBack);
		registerDtOperator("easeInOutBounce", MoLangEasing::easeInOutBounce);
		registerDtOperator("easeInOutCirc", MoLangEasing::easeInOutCirc);
		registerDtOperator("easeInOutCubic", MoLangEasing::easeInOutCubic);
		registerDtOperator("easeInOutElastic", MoLangEasing::easeInOutElastic);
		registerDtOperator("easeInOutExpo", MoLangEasing::easeInOutExpo);
		registerDtOperator("easeInOutQuad", MoLangEasing::easeInOutQuad);
		registerDtOperator("easeInOutQuart", MoLangEasing::easeInOutQuart);
		registerDtOperator("easeInOutQuint", MoLangEasing::easeInOutQuint);
		registerDtOperator("easeInOutSine", MoLangEasing::easeInOutSine);
		registerDtOperator("easeInQuad", MoLangEasing::easeInQuad);
		registerDtOperator("easeInQuart", MoLangEasing::easeInQuart);
		registerDtOperator("easeInQuint", MoLangEasing::easeInQuint);
		registerDtOperator("easeInSine", MoLangEasing::easeInSine);
		registerDtOperator("easeOutBack", MoLangEasing::easeOutBack);
		registerDtOperator("easeOutBounce", MoLangEasing::easeOutBounce);
		registerDtOperator("easeOutCirc", MoLangEasing::easeOutCirc);
		registerDtOperator("easeOutCubic", MoLangEasing::easeOutCubic);
		registerDtOperator("easeOutElastic", MoLangEasing::easeOutElastic);
		registerDtOperator("easeOutExpo", MoLangEasing::easeOutExpo);
		registerDtOperator("easeOutQuad", MoLangEasing::easeOutQuad);
		registerDtOperator("easeOutQuart", MoLangEasing::easeOutQuart);
		registerDtOperator("easeOutQuint", MoLangEasing::easeOutQuint);
		registerDtOperator("easeOutSine", MoLangEasing::easeOutSine);
	}
}