package org.zeith.hammeranims.molang.ast;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.runtime.MoLangEnvironment;
import org.zeith.hammeranims.molang.runtime.MoScope;
import org.zeith.hammeranims.molang.runtime.value.DoubleValue;
import org.zeith.hammeranims.molang.runtime.value.MoValue;
import lombok.Value;

@Value
public class StatementExpression implements Expression {

    Expression[] expressions;

    @Override
    public MoValue evaluate(MoScope scope, MoLangEnvironment environment) {
        for (Expression expression : expressions) {
            expression.evaluate(scope, environment);

            if (scope.getReturnValue() != null) {
                return scope.getReturnValue();
            } else if (scope.isBreak() || scope.isContinue()) {
                break;
            }
        }

        return DoubleValue.ZERO;
    }
}
