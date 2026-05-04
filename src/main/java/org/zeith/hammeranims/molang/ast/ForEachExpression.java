package org.zeith.hammeranims.molang.ast;

import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.runtime.MoLangEnvironment;
import org.zeith.hammeranims.molang.runtime.MoScope;
import org.zeith.hammeranims.molang.runtime.struct.VariableStruct;
import org.zeith.hammeranims.molang.runtime.value.DoubleValue;
import org.zeith.hammeranims.molang.runtime.value.MoValue;
import lombok.Value;

import java.util.ArrayList;

@Value
public class ForEachExpression implements Expression {

    Expression variable;
    Expression array;
    Expression body;

    @Override
    public MoValue evaluate(MoScope scope, MoLangEnvironment environment) {
        MoValue array = this.array.evaluate(scope, environment);

        if (array instanceof VariableStruct) {
            VariableStruct struct = (VariableStruct) array;
            MoScope scope2 = new MoScope();

            for (MoValue value : new ArrayList<>(struct.getMap().values())) {
                variable.assign(scope2, environment, value);
                body.evaluate(scope2, environment);

                if (scope2.getReturnValue() != null) {
                    return scope2.getReturnValue();
                } else if (scope2.isBreak()) {
                    break;
                }
            }
        }

        return DoubleValue.ZERO;
    }
}
