package dev.zeith.lzvm.molang.tokenizer;

import lombok.*;

@Value
@RequiredArgsConstructor
public class Token
{
	ETokenType type;
	String text;
	TokenPos position;
	
	public Token(ETokenType tokenType, TokenPos position)
	{
		this.type = tokenType;
		this.text = tokenType.value;
		this.position = position;
	}
}