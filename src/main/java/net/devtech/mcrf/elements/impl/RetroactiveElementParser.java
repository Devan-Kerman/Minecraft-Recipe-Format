package net.devtech.mcrf.elements.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.recipes.Recipe;
import net.devtech.mcrf.recipes.RecipeSchema;
import net.devtech.mcrf.util.Id;

public class RetroactiveElementParser implements ElementParser<StringBuilder> {
	private static final char[] START = Recipe.START_STR.toCharArray();

	@Override
	public StringBuilder parse(Reader reader) throws IllegalArgumentException, IOException {
		StringBuilder builder = new StringBuilder();

		char[] buf = new char[START.length + 1];
		while (true) {
			// peek start
			reader.mark(START.length);
			if (reader.read(buf) != buf.length) {
				throw new IllegalStateException("EOF reached, but in a RetroactiveElementParser, which is supposed to be an input-only element, meaning there is a syntax error!");
			}

			// if next 3 chars == the starting of the ""equals""
			if(this.test(buf)) {
				// if escaped "--["
				if(buf[0] == '\\') {
					builder.append(buf, 1, START.length);
				} else { // else, return
					reader.reset();
					builder.append((char)reader.read());
					break;
				}
			} else { // normal
				reader.reset();
				builder.append((char) reader.read());
			}
		}
		return builder;
	}

	private boolean test(char[] buf) {
		for (int i = 0; i < START.length; i++) {
			if(buf[i + 1] != START[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean finalizing() {
		return true;
	}

	@Override
	public boolean needsPostProcessing() {
		return true;
	}

	@Override
	public Object[] postProcess(RecipeSchema schema, Id id, StringBuilder object) {
		if (schema instanceof RecipeSchema.RetroactiveSchema) {
			try {
				return Recipe.read(new StringReader(object.toString()), ((RecipeSchema.RetroactiveSchema) schema).getInputs(id), false);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new IllegalArgumentException("Recipe schema is not retroactive!");
		}
	}
}
