package org.zeith.hammeranims.core.molang.jvm;

import dev.zeith.lzvm.molang.compiler.libs.MoMathLibrary;

public class MoMathClass
	extends BaseJvmClass
{
	public MoMathClass(String name)
	{
		super(name);
	}
	
	@Override
	protected void registerMethods()
	{
		registerDuOperator("signum", MoMathLibrary::signum);
		registerDuOperator("minAngle", MoMathLibrary::minAngle);
		registerDuOperator("hermiteBlend", MoMathLibrary::hermiteBlend);
		registerDuOperator("trunc", MoMathLibrary::trunc);
		registerDuOperator("round", MoMathLibrary::round);
		
		registerDbOperator("random", MoMathLibrary::random);
		registerDbOperator("randomInt", MoMathLibrary::randomInt);
		
		registerDtOperator("lerp", MoMathLibrary::lerp);
		registerDtOperator("inverseLerp", MoMathLibrary::inverseLerp);
		registerDtOperator("clamp", MoMathLibrary::clamp);
		registerDtOperator("lerpRotate", MoMathLibrary::lerpRotate);
		registerDtOperator("dieRoll", MoMathLibrary::dieRoll);
		registerDtOperator("dieRollInt", MoMathLibrary::dieRollInt);
	}
}