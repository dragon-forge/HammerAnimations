package dev.zeith.lzvm.molang.tokenizer;

import lombok.Value;

@Value
public class TokenPos
{
	int ln;
	int pos;
	int len;
}