package org.zeith.hammeranims.molang.ast;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.runtime.MoLangEnvironment;
import org.zeith.hammeranims.molang.runtime.MoScope;
import org.zeith.hammeranims.molang.runtime.value.DoubleValue;
import org.zeith.hammeranims.molang.runtime.value.MoValue;
import lombok.Value;

@Value
public class LoopExpression implements Expression {

    Expression count;
    Expression body;

    @Override
    public MoValue evaluate(MoScope scope, MoLangEnvironment environment) {
        int loop = (int) count.evaluate(scope, environment).asDouble();
        MoScope subScope = new MoScope();

        while (loop > 0) {
            body.evaluate(subScope, environment);
            loop--;

            if (subScope.getReturnValue() != null) {
                return subScope.getReturnValue();
            } else if (subScope.isBreak()) {
                break;
            }
        }

        return DoubleValue.ZERO;
    }
}
