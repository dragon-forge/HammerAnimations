package org.zeith.hammeranims.molang;

import org.zeith.hammeranims.molang.parser.MoLangParser;
import org.zeith.hammeranims.molang.parser.tokenizer.TokenIterator;
import org.zeith.hammeranims.molang.runtime.MoLangRuntime;
import org.zeith.hammeranims.molang.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MoLang
{
	public static List<Expression> parse(String code)
	{
		return (createParser(code)).parse();
	}
	
	public static List<Expression> parse(Path path)
	{
		return (createParser(path)).parse();
	}
	
	public static List<Expression> parse(InputStream stream)
			throws IOException
	{
		return (createParser(stream)).parse();
	}
	
	public static MoLangParser createParser(String code)
	{
		return new MoLangParser(new TokenIterator(code));
	}
	
	public static MoLangParser createParser(Path path)
	{
		byte[] fileBytes;
		try
		{
			fileBytes = Files.readAllBytes(path);
		} catch(IOException e)
		{
			fileBytes = new byte[0];
		}
		
		return new MoLangParser(new TokenIterator(new String(fileBytes, StandardCharsets.UTF_8)));
	}
	
	public static MoLangParser createParser(InputStream stream)
			throws IOException
	{
		return new MoLangParser(new TokenIterator(FileUtils.readFile(stream)));
	}
	
	public static MoLangRuntime createRuntime(boolean concurrent)
	{
		return new MoLangRuntime(concurrent);
	}
}