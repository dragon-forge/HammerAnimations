package org.zeith.hammeranims.molang.ast.binaryop;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.ast.BinaryOpExpression;
import org.zeith.hammeranims.molang.runtime.MoLangEnvironment;
import org.zeith.hammeranims.molang.runtime.MoScope;
import org.zeith.hammeranims.molang.runtime.value.MoValue;

public class ArrowExpression extends BinaryOpExpression {

    public ArrowExpression(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String getSigil() {
        return "->";
    }

    @Override
    public MoValue evaluate(MoScope scope, MoLangEnvironment environment) {
        Object leftEnv = left.evaluate(scope, environment);
        if (leftEnv instanceof MoLangEnvironment) {
            return right.evaluate(scope, (MoLangEnvironment) leftEnv);
        }

        return null;
    }
}
