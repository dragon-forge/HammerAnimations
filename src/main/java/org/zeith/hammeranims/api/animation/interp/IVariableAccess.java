package org.zeith.hammeranims.api.animation.interp;

import java.util.function.BiConsumer;

public interface IVariableAccess
{
	void putObjects(BiConsumer<String, Object> storage);
}