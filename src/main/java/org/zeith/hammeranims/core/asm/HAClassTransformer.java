package org.zeith.hammeranims.core.asm;

import org.zeith.hammeranims.core.asm.tfs.EntityTransformationUtilsTransformer;
import org.zeith.hammeranims.core.asm.tfs.EntityTransformer;

public class HAClassTransformer
		extends CoreClassTransformer
{
	private final EntityTransformer entity = new EntityTransformer();
	private final EntityTransformationUtilsTransformer entityTransformationUtils = new EntityTransformationUtilsTransformer();
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if(checkName(name, "vg", "net.minecraft.entity.Entity"))
		{
			System.out.println("HammerAnimationsCore: Transforming Entity class (" + name + ")");
			return this.entity.transform(name, basicClass);
		} else if(name.equals("org.zeith.hammeranims.core.utils.EntityTransformationUtils"))
		{
			System.out.println("HammerAnimationsCore: Transforming EntityTransformationUtils class (" + name + ")");
			return this.entityTransformationUtils.transform(name, basicClass);
		}
		
		return basicClass;
	}
}