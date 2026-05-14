package dev.zeith.lzvm.molang.tokenizer;

import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public enum ETokenType
{
	EQUALS("=="),
	NOT_EQUALS("!="),
	COALESCE("??"),
	AND("&&"),
	OR("||"),
	GREATER_EQ(">="),
	LESS_EQ("<="),
	GREATER(">"),
	LESS("<"),
	BRACKET_LEFT("("),
	BRACKET_RIGHT(")"),
	ARRAY_LEFT("["),
	ARRAY_RIGHT("]"),
	CURLY_BRACKET_LEFT("{"),
	CURLY_BRACKET_RIGHT("}"),
	SEMICOLON(";"),
	COMMA(","),
	ASSIGN("="),
	PLUS("+"),
	PLUS_DOUBLE("++"),
	MINUS("-"),
	MINUS_DOUBLE("--"),
	ASTERISK("*"),
	SLASH("/"),
	PERCENT("%"),
	QUESTION("?"),
	
	COMP_PLUS("+="),
	COMP_MINUS("-="),
	COMP_ASTERISK("*="),
	COMP_SLASH("/="),
	COMP_PERCENT("%="),
	
	COLON(":"),
	BANG("!"),
	RETURN("return"),
	CONTINUE("continue"),
	BREAK("break"),
	FOR_EACH("for_each"),
	LOOP("loop"),
	THIS("this"),
	TRUE("true"),
	FALSE("false"),
	STRING(""),
	NUMBER(""),
	NAME(""),
	EOF("");
	
	public final String value;
	
	private static final Map<String, ETokenType> TYPE_MAP;
	private static final Map<Character, ETokenType> CHAR_TYPE_MAP;
	
	static
	{
		Map<String, ETokenType> map = new HashMap<>();
		Map<Character, ETokenType> map2 = new HashMap<>();
		for(ETokenType type : ETokenType.values())
			if(!type.value.isEmpty())
			{
				map.put(type.value, type);
				char[] ca = type.value.toCharArray();
				if(ca.length == 1) map2.put(ca[0], type);
			}
		TYPE_MAP = map;
		CHAR_TYPE_MAP = map2;
	}
	
	public static ETokenType find(String val)
	{
		return TYPE_MAP.get(val);
	}
	
	public static ETokenType find(char val)
	{
		return CHAR_TYPE_MAP.get(val);
	}
}