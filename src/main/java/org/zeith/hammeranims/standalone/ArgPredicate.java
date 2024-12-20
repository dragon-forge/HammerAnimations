package org.zeith.hammeranims.standalone;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.zip.ZipFile;

public record ArgPredicate(SafePredicate validator, String requirements)
{
	public static final ArgPredicate ZIP_FILE = new ArgPredicate(ArgPredicate::isValidZipFile, "must be path to a valid zip file");
	public static final ArgPredicate NON_EXISTING_FILE = new ArgPredicate(ArgPredicate::isValidWritableLocationThatDoesNotExist, "must be path to a non-existent writable location");
	public static final ArgPredicate WRITABLE_FILE = new ArgPredicate(ArgPredicate::isValidWritableLocation, "must be path to a writable location");
	public static final ArgPredicate STRING = new ArgPredicate(s -> !s.isBlank(), "must be non-empty string");
	
	public ArgPredicate or(ArgPredicate other)
	{
		return new ArgPredicate(
				validator.or(other.validator),
				requirements + " | " + other.requirements
		);
	}
	
	private static boolean isValidZipFile(String path)
			throws Exception
	{
		try(ZipFile zf = new ZipFile(new File(path)))
		{
			return zf.size() > 4;
		}
	}
	
	private static boolean isValidWritableLocationThatDoesNotExist(String path)
			throws Exception
	{
		File f = new File(path);
		if(f.exists()) return false;
		try(var out = Files.newOutputStream(f.toPath()))
		{
		}
		f.delete();
		return true;
	}
	
	private static boolean isValidWritableLocation(String path)
			throws Exception
	{
		File f = new File(path);
		try(var out = Files.newOutputStream(f.toPath(), StandardOpenOption.APPEND, StandardOpenOption.CREATE))
		{
		}
		return true;
	}
	
	public interface SafePredicate
			extends Predicate<String>
	{
		@Override
		default boolean test(String s)
		{
			try
			{
				return testUnsafe(s);
			} catch(Exception e)
			{
				throw new IProcedure.ConfigureException("Unable to parse input " + s, e);
			}
		}
		
		@Override
		@NotNull
		default SafePredicate or(@NotNull Predicate<? super String> other)
		{
			Objects.requireNonNull(other);
			return (t) ->
			{
				try
				{
					if(test(t))
						return true;
				} catch(RuntimeException re)
				{
					try
					{
						if(other.test(t))
							return true;
					} catch(RuntimeException re2)
					{
						re2.addSuppressed(re);
						throw re2;
					}
				}
				return false;
			};
		}
		
		boolean testUnsafe(String s)
				throws Exception;
	}
}