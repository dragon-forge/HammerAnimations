package dev.zeith.lzvm.molang.tokenizer;

import dev.zeith.lzvm.molang.parser.ExpressionFixer;
import lombok.ToString;

import java.util.Iterator;

@ToString
public class Tokenizer
		implements Iterator<Token>
{
	protected String expression;
	protected int exprLen;
	protected int i;
	protected int pos;
	protected int currentLine;
	
	public void init(String expression)
	{
		expression = !expression.isEmpty() ? ExpressionFixer.fixExpression(expression) : expression;
		
		this.currentLine = 0;
		this.pos = 0;
		this.i = 0;
		this.expression = expression;
		this.exprLen = expression.length();
	}
	
	@Override
	public boolean hasNext()
	{
		return i < exprLen;
	}
	
	@Override
	public Token next()
	{
		this.pos = this.i;
		
		String expression = this.expression;
		
		while(i < exprLen)
		{
			if(exprLen >= i + 2)
			{
				ETokenType token = ETokenType.find(expression.substring(i, i + 2));
				if(token != null)
				{
					i += 2;
					return new Token(token, getPos());
				}
			}
			
			char expr = expression.charAt(i);
			ETokenType tokenType = ETokenType.find(expr);
			if(tokenType != null)
			{
				i++;
				return new Token(tokenType, getPos());
			}
			
			if(expr == '\'')
				return readString(expression);
			
			if(Character.isLetter(expr))
			{
				int end = i + 1;
				
				char c;
				while(end < exprLen && (Character.isLetterOrDigit(c = expression.charAt(end)) || c == '_' || c == '.'))
					end++;
				
				String value = expression.substring(i, end).toLowerCase();
				
				setI(end);
				
				ETokenType token = ETokenType.find(value);
				if(token == null) token = ETokenType.NAME;
				
				return new Token(token, value, getPos());
			}
			
			if(Character.isDigit(expr))
			{
				int numStart = i;
				int numLength = i + 1;
				boolean hasDecimal = false;
				
				char c;
				while(numLength < exprLen && (Character.isDigit(c = expression.charAt(numLength)) || ('.' == c && !hasDecimal)))
				{
					if(c == '.') hasDecimal = true;
					numLength++;
				}
				
				setI(numLength);
				
				return new Token(ETokenType.NUMBER, expression.substring(numStart, numLength), getPos());
			}
			
			if(expr == '\n')
			{
				++currentLine;
			}
			
			setI(i + 1);
		}
		
		// After we hit EOF, reset current state
		init("");
		
		return new Token(ETokenType.EOF, getPos());
	}
	
	private Token readString(String expression)
	{
		int pos = i + 1;
		StringBuilder sb = new StringBuilder();
		
		while(pos < exprLen)
		{
			char c = expression.charAt(pos);
			
			if(c == '\\')
			{
				if(pos + 1 >= exprLen)
					throw new RuntimeException("Unterminated escape sequence");
				
				char next = expression.charAt(pos + 1);
				
				switch(next)
				{
					case '\\': sb.append('\\'); break;
					case '\'': sb.append('\''); break;
					case 'n': sb.append('\n'); break;
					case 't': sb.append('\t'); break;
					case 'r': sb.append('\r'); break;
					default: sb.append(next); break;
				}
				
				pos += 2;
				continue;
			}
			
			if(c == '\'')
			{
				pos++;
				setI(pos);
				return new Token(ETokenType.STRING, sb.toString(), getPos());
			}
			
			sb.append(c);
			pos++;
		}
		
		return new Token(ETokenType.STRING, sb.toString(), getPos());
	}
	
	protected void setI(int newI)
	{
		this.i = newI;
		if(this.i >= exprLen) init("");
	}
	
	public TokenPos getPos()
	{
		return new TokenPos(currentLine, pos, i - pos + 1);
	}
}