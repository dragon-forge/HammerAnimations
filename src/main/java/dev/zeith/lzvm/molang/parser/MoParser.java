package dev.zeith.lzvm.molang.parser;

import dev.zeith.lzvm.molang.expression.MLExpression;
import dev.zeith.lzvm.molang.parcelets.*;
import dev.zeith.lzvm.molang.parcelets.infix.*;
import dev.zeith.lzvm.molang.parcelets.prefix.*;
import dev.zeith.lzvm.molang.tokenizer.*;

import java.util.*;

public class MoParser
{
	private final static Map<ETokenType, IPrefixParselet> prefixParselets = new HashMap<>();
	private final static Map<ETokenType, IInfixParselet> infixParselets = new HashMap<>();
	
	static
	{
		// Simple feature set
		prefixParselets.put(ETokenType.NAME, new NameParselet());
		prefixParselets.put(ETokenType.STRING, new StringParselet());
		prefixParselets.put(ETokenType.NUMBER, new NumberParselet());
		prefixParselets.put(ETokenType.TRUE, new BoolParselet());
		prefixParselets.put(ETokenType.FALSE, new BoolParselet());
		prefixParselets.put(ETokenType.BREAK, new BreakParselet());
		prefixParselets.put(ETokenType.CONTINUE, new ContinueParselet());
		prefixParselets.put(ETokenType.BRACKET_LEFT, new GroupParselet());
		prefixParselets.put(ETokenType.CURLY_BRACKET_LEFT, new CodeBlockParselet());
		prefixParselets.put(ETokenType.MINUS, new UnaryMinusParselet());
		prefixParselets.put(ETokenType.PLUS, new UnaryPlusParselet());
		prefixParselets.put(ETokenType.BANG, new NotParselet());
		prefixParselets.put(ETokenType.PLUS_DOUBLE, new UnaryCounterParselet());
		prefixParselets.put(ETokenType.MINUS_DOUBLE, new UnaryCounterParselet());
		
		// More complex logic
		prefixParselets.put(ETokenType.LOOP, new LoopParselet());
		prefixParselets.put(ETokenType.RETURN, new ReturnParselet());
		
		///////////
		// INFIX //
		///////////
		
		infixParselets.put(ETokenType.PLUS, new GenericBinaryOpParselet(EPrecedence.SUM));
		infixParselets.put(ETokenType.MINUS, new GenericBinaryOpParselet(EPrecedence.SUM));
		infixParselets.put(ETokenType.SLASH, new GenericBinaryOpParselet(EPrecedence.PRODUCT));
		infixParselets.put(ETokenType.ASTERISK, new GenericBinaryOpParselet(EPrecedence.PRODUCT));
		infixParselets.put(ETokenType.PERCENT, new GenericBinaryOpParselet(EPrecedence.PRODUCT));
		infixParselets.put(ETokenType.EQUALS, new GenericBinaryOpParselet(EPrecedence.COMPARE));
		infixParselets.put(ETokenType.NOT_EQUALS, new GenericBinaryOpParselet(EPrecedence.COMPARE));
		infixParselets.put(ETokenType.GREATER, new GenericBinaryOpParselet(EPrecedence.COMPARE));
		infixParselets.put(ETokenType.GREATER_EQ, new GenericBinaryOpParselet(EPrecedence.COMPARE));
		infixParselets.put(ETokenType.LESS, new GenericBinaryOpParselet(EPrecedence.COMPARE));
		infixParselets.put(ETokenType.LESS_EQ, new GenericBinaryOpParselet(EPrecedence.COMPARE));
		infixParselets.put(ETokenType.AND, new GenericBinaryOpParselet(EPrecedence.AND));
		infixParselets.put(ETokenType.OR, new GenericBinaryOpParselet(EPrecedence.OR));
		infixParselets.put(ETokenType.COALESCE, new GenericBinaryOpParselet(EPrecedence.NULLISH_COALESCING));
		// Compound assigns
		infixParselets.put(ETokenType.COMP_PLUS, CompoundAssignParselet.INSTANCE);
		infixParselets.put(ETokenType.COMP_MINUS, CompoundAssignParselet.INSTANCE);
		infixParselets.put(ETokenType.COMP_ASTERISK, CompoundAssignParselet.INSTANCE);
		infixParselets.put(ETokenType.COMP_SLASH, CompoundAssignParselet.INSTANCE);
		infixParselets.put(ETokenType.COMP_PERCENT, CompoundAssignParselet.INSTANCE);
		//
		infixParselets.put(ETokenType.QUESTION, new TernaryParselet());
		infixParselets.put(ETokenType.ASSIGN, new AssignParselet());
		infixParselets.put(ETokenType.ARRAY_LEFT, new ArrayParselet());
	}
	
	private final Tokenizer tokenIterator;
	private final List<Token> readTokens = new ArrayList<>();
	private final Map<String, String> aliases;
	
	public int loopCountLimiter = 1024;
	
	public MoParser(Map<String, String> aliases, Tokenizer iterator)
	{
		this.aliases = aliases;
		this.tokenIterator = iterator;
	}
	
	public String resolveTopLevelAlias(String s)
	{
		return this.aliases.getOrDefault(s.toLowerCase(Locale.ROOT), s);
	}
	
	public ArrayList<MLExpression> parse()
	{
		ArrayList<MLExpression> exprs = new ArrayList<>();
		
		do
		{
			MLExpression expr = parseExpression();
			if(expr != null) exprs.add(expr);
			else break;
		} while(matchToken(ETokenType.SEMICOLON));
		
		return exprs;
	}
	
	public MLExpression parseExpression()
	{
		return parseExpression(EPrecedence.ANYTHING);
	}
	
	public MLExpression parseExpression(EPrecedence precedence)
	{
		Token token = consumeToken();
		
		if(token.getType().equals(ETokenType.EOF))
			return null;
		
		IPrefixParselet parselet = prefixParselets.get(token.getType());
		if(parselet == null) throw new RuntimeException("Cannot parse " + token.getType().name() + " expression");
		
		MLExpression expr = parselet.parse(this, token);
		
		int prec = precedence.ordinal();
		while(prec < curPrecedence().ordinal())
		{
			token = consumeToken();
			expr = infixParselets.get(token.getType()).parse(this, token, expr);
		}
		
		return expr;
	}
	
	private EPrecedence curPrecedence()
	{
		Token token = readToken();
		if(token != null)
		{
			IInfixParselet parselet = infixParselets.get(token.getType());
			if(parselet != null) return parselet.getPrecedence();
		}
		return EPrecedence.ANYTHING;
	}
	
	public ArrayList<MLExpression> parseArgs()
	{
		ArrayList<MLExpression> args = new ArrayList<>();
		
		if(matchToken(ETokenType.BRACKET_LEFT) && !matchToken(ETokenType.BRACKET_RIGHT))
		{
			// Arguments
			do
			{
				args.add(parseExpression());
			} while(matchToken(ETokenType.COMMA));
			consumeToken(ETokenType.BRACKET_RIGHT);
		}
		
		return args;
	}
	
	public boolean matchToken(ETokenType expectedType)
	{
		return matchToken(expectedType, true);
	}
	
	public boolean matchToken(ETokenType expectedType, boolean consume)
	{
		Token token = readToken();
		if(token == null || !token.getType().equals(expectedType)) return false;
		if(consume) consumeToken();
		return true;
	}
	
	public Token consumeToken()
	{
		return consumeToken(null);
	}
	
	public Token consumeToken(ETokenType expectedType)
	{
		Token token = readToken();
		
		if(expectedType != null)
			if(!token.getType().equals(expectedType))
				throw new RuntimeException("Expected token " + expectedType.name() + " and " + token.getType().name() + " given");
		
		return readTokens.remove(0);
	}
	
	private Token readToken()
	{
		return readToken(0);
	}
	
	private Token readToken(int max)
	{
		while(max >= readTokens.size()) readTokens.add(tokenIterator.next());
		return readTokens.get(max);
	}
}