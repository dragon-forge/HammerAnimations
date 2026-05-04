package org.zeith.hammeranims.molang.ast;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.runtime.MoLangEnvironment;
import org.zeith.hammeranims.molang.runtime.MoScope;
import org.zeith.hammeranims.molang.runtime.value.DoubleValue;
import org.zeith.hammeranims.molang.runtime.value.MoValue;
import lombok.Value;

@Value
public class TernaryExpression implements Expression {

    Expression condition;
    Expression thenExpr;
    Expression elseExpr;

    @Override
    public MoValue evaluate(MoScope scope, MoLangEnvironment environment) {
        if (condition.evaluate(scope, environment).equals(DoubleValue.ONE)) {
            return thenExpr == null ? condition.evaluate(scope, environment) : thenExpr.evaluate(scope, environment);
        } else if (elseExpr != null) {
            return elseExpr.evaluate(scope, environment);
        }

        return DoubleValue.ZERO;
    }
}
