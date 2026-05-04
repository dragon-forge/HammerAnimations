package org.zeith.hammeranims.molang.ast;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.runtime.MoLangEnvironment;
import org.zeith.hammeranims.molang.runtime.MoScope;
import org.zeith.hammeranims.molang.runtime.value.DoubleValue;
import org.zeith.hammeranims.molang.runtime.value.MoValue;
import lombok.Value;

@Value
public class BreakExpression implements Expression {

    @Override
    public MoValue evaluate(MoScope scope, MoLangEnvironment environment) {
        scope.setBreak(true);
        return DoubleValue.ZERO;
    }
}
