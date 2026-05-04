package org.zeith.hammeranims.molang.parser.tokenizer;

import lombok.Value;

@Value
public class TokenPosition {

    int startLineNumber;
    int endLineNumber;
    int startColumn;
    int endColumn;
}
