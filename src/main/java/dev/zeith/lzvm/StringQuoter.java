package dev.zeith.lzvm;

import java.io.*;

public class StringQuoter
{
	public static String quote(String string)
	{
		if(string == null || string.isEmpty())
			return "\"\"";
		try(StringBuilderWriter sw = new StringBuilderWriter(string.length() + 2))
		{
			return quote(string, sw).toString();
		} catch(IOException ignored)
		{
			// will never happen - we are writing to a string writer
			return "";
		}
	}
	
	public static Writer quote(String string, Writer w)
			throws IOException
	{
		if(string == null || string.isEmpty())
		{
			w.write("\"\"");
			return w;
		}
		
		char b;
		char c = 0;
		String hhhh;
		int i;
		int len = string.length();
		
		w.write('"');
		for(i = 0; i < len; i += 1)
		{
			b = c;
			c = string.charAt(i);
			switch(c)
			{
				case '\\':
				case '"':
					w.write('\\');
					w.write(c);
					break;
				case '/':
					if(b == '<')
					{
						w.write('\\');
					}
					w.write(c);
					break;
				case '\b':
					w.write("\\b");
					break;
				case '\t':
					w.write("\\t");
					break;
				case '\n':
					w.write("\\n");
					break;
				case '\f':
					w.write("\\f");
					break;
				case '\r':
					w.write("\\r");
					break;
				default:
					if(c < ' ' || (c >= '\u0080' && c < '\u00a0')
							|| (c >= '\u2000' && c < '\u2100'))
					{
						w.write("\\u");
						hhhh = Integer.toHexString(c);
						w.write("0000", 0, 4 - hhhh.length());
						w.write(hhhh);
					} else
					{
						w.write(c);
					}
			}
		}
		w.write('"');
		return w;
	}
	
	public static class StringBuilderWriter
			extends Writer
	{
		private final StringBuilder builder;
		
		/**
		 * Create a new string builder writer using the default initial string-builder buffer size.
		 */
		public StringBuilderWriter()
		{
			builder = new StringBuilder();
			lock = builder;
		}
		
		/**
		 * Create a new string builder writer using the specified initial string-builder buffer size.
		 *
		 * @param initialSize
		 * 		The number of {@code char} values that will fit into this buffer
		 * 		before it is automatically expanded
		 *
		 * @throws IllegalArgumentException
		 * 		If {@code initialSize} is negative
		 */
		public StringBuilderWriter(int initialSize)
		{
			builder = new StringBuilder(initialSize);
			lock = builder;
		}
		
		@Override
		public void write(int c)
		{
			builder.append((char) c);
		}
		
		@Override
		public void write(char[] cbuf, int offset, int length)
		{
			if((offset < 0) || (offset > cbuf.length) || (length < 0) ||
					((offset + length) > cbuf.length) || ((offset + length) < 0))
			{
				throw new IndexOutOfBoundsException();
			} else if(length == 0)
			{
				return;
			}
			builder.append(cbuf, offset, length);
		}
		
		@Override
		public void write(String str)
		{
			builder.append(str);
		}
		
		@Override
		public void write(String str, int offset, int length)
		{
			builder.append(str, offset, offset + length);
		}
		
		@Override
		public StringBuilderWriter append(CharSequence csq)
		{
			write(String.valueOf(csq));
			return this;
		}
		
		@Override
		public StringBuilderWriter append(CharSequence csq, int start, int end)
		{
			if(csq == null)
			{
				csq = "null";
			}
			return append(csq.subSequence(start, end));
		}
		
		@Override
		public StringBuilderWriter append(char c)
		{
			write(c);
			return this;
		}
		
		@Override
		public String toString()
		{
			return builder.toString();
		}
		
		@Override
		public void flush()
		{
		}
		
		@Override
		public void close()
				throws IOException
		{
		}
	}
}