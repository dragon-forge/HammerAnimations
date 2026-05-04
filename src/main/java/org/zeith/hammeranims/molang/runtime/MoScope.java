package org.zeith.hammeranims.molang.runtime;

import org.zeith.hammeranims.molang.runtime.value.MoValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoScope
{
	private boolean isBreak = false;
	private boolean isContinue = false;
	private MoValue returnValue;
}