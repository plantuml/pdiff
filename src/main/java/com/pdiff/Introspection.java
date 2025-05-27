package com.pdiff;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Introspection {

	public static String versionString() {
		try {
			final Class<?> versionClass = Class.forName("net.sourceforge.plantuml.version.Version");
			final Method versionStringMethod = versionClass.getMethod("versionString");
			return (String) versionStringMethod.invoke(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static OutputRun outputImage(final Path outputPathPng, final String text, boolean onlyPerf)
			throws Exception {

		final Class<?> readerClass = Class.forName("net.sourceforge.plantuml.SourceStringReader");
		final Class<?> fileFormatClass = Class.forName("net.sourceforge.plantuml.FileFormat");
		final Class<?> fileFormatOptionClass = Class.forName("net.sourceforge.plantuml.FileFormatOption");

		final Enum fileFormatPng = onlyPerf ? Enum.valueOf((Class<Enum>) fileFormatClass, "PNG_EMPTY")
				: Enum.valueOf((Class<Enum>) fileFormatClass, "PNG");
		final Constructor<?> fileFormatOptionConstructor = fileFormatOptionClass.getConstructor(fileFormatClass);
		final Object fileFormatOptionInstance = fileFormatOptionConstructor.newInstance(fileFormatPng);

		final Constructor<?> readerConstructor = readerClass.getConstructor(String.class);
		final Object readerInstance = readerConstructor.newInstance(text);

		final Method outputImageMethod = readerClass.getMethod("outputImage", OutputStream.class, int.class,
				fileFormatOptionClass);

		try (OutputStream png = Files.newOutputStream(outputPathPng)) {
			final Object result = outputImageMethod.invoke(readerInstance, png, 0, fileFormatOptionInstance);

			final Method getDescriptionMethod = result.getClass().getMethod("getDescription");
			final String description = (String) getDescriptionMethod.invoke(result);

			// Introspection on getBlocks()
			final Method getBlocksMethod = readerClass.getMethod("getBlocks");
			final List<?> blocks = (List<?>) getBlocksMethod.invoke(readerInstance);
			final Object firstBlock = blocks.get(0);
			final Method getDiagramMethod = firstBlock.getClass().getMethod("getDiagram");
			final Object diagram = getDiagramMethod.invoke(firstBlock);
			final Class<?> diagramClass = diagram.getClass();

			return new OutputRun(description, diagramClass.getSimpleName());
		}
	}

}
