package net.devtech.mcrf.recipes;

import static net.devtech.mcrf.util.IOUtil.skipWhitespace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.UnknownFormatConversionException;
import java.util.function.BiPredicate;

import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.util.IOUtil;
import net.devtech.mcrf.util.Id;

/**
 * a machine's recipe
 */
public final class Recipe {
	private static final char[] START = "--[".toCharArray();
	private static final char[] END = "]->".toCharArray();

	private final Id machine;
	private final Object[] inputs;
	private final Object[] outputs;

	private Recipe(Id machine, Object[] inputs, Object[] outputs) {
		this.machine = machine;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	public static List<Recipe> parse(Reader reader, RecipeSchema schema) throws IOException {
		if(!reader.markSupported()) {
			throw new IllegalArgumentException("Reader must support mark()!");
		}

		List<Recipe> recipes = new ArrayList<>();
		while (true) {
			IOUtil.skipWhitespace(reader);
			reader.mark(1);
			int chr = reader.read();
			if(chr == -1)
				break;
			reader.reset();
			Object[] inputs = read(reader, schema.getInputs(), false);
			Id id = readMachine(reader);
			Object[] outputs = read(reader, schema.getOutputs(), true);
			recipes.add(new Recipe(id, inputs, outputs));
		}
		return recipes;
	}

	public static List<Recipe> parse(InputStream stream, RecipeSchema schema) throws IOException {
		return parse(new BufferedReader(new InputStreamReader(stream)), schema);
	}

	private static Id readMachine(Reader reader) throws IOException {
		char[] arr = new char[3];
		String stage = "starting";
		if (reader.read(arr) == START.length && Arrays.equals(arr, START)) {
			StringBuilder idBuilder = new StringBuilder();
			int read;
			while (true) {
				reader.mark(1);
				read = reader.read();
				if (read == -1) {
					break;
				}

				if (read == END[0]) {
					reader.reset();
					break;
				}
				idBuilder.append((char) read);
			}
			if (reader.read(arr) == END.length && Arrays.equals(arr, END)) {
				int colon = idBuilder.indexOf(":");
				if (colon == -1) {
					throw new UnknownFormatConversionException(idBuilder + " is an invalid identifier!");
				}
				Id id = new Id(idBuilder.substring(0, colon), idBuilder.substring(colon + 1));
				skipWhitespace(reader);
				return id;
			} else {
				stage = "ending";
			}
		}

		throw new MissingFormatArgumentException("machine segments must be represented as '--[machine:id]->' but found " + new String(arr) + " as " + stage + " chars instead!");
	}

	private static Object[] read(Reader reader, Iterator<ElementParser<?>> iterator, boolean ignoreEnd) throws IOException {
		List<Object> inputs = new ArrayList<>();
		while (iterator.hasNext()) {
			ElementParser<?> input = iterator.next();
			try {
				inputs.add(input.parse(reader));
			} catch (Throwable t) {
				t.printStackTrace();
			}
			skipWhitespace(reader);
			// only non-last elements need + checking
			if (iterator.hasNext()) {
				int c;
				reader.mark(1);
				if ((c = reader.read()) != '+') {
					if(ignoreEnd) {
						reader.reset();
					} else {
						throw new MissingFormatArgumentException("Error extra character : " + (char)c + " found!");
					}
				}
			}
			skipWhitespace(reader);
		}
		return inputs.toArray();
	}

	public boolean matches(Object... inputs) {
		return Arrays.equals(inputs, this.inputs);
	}

	public boolean matches(BiPredicate<Object, Object> equals, Object... inputs) {
		for (int i = 0; i < Math.max(this.inputs.length, inputs.length); i++) {
			if (equals.test(get(inputs, i), get(this.inputs, i))) {
				return false;
			}
		} return true;
	}

	private static Object get(Object[] arr, int index) {
		if (index < arr.length) {
			return arr[index];
		}
		return null;
	}

	public Object[] getInputs() {
		return this.inputs;
	}

	public <T> T getInput(int index) {
		return (T) this.inputs[index];
	}

	public <T> T getOutput(int index) {
		return (T) this.outputs[index];
	}


	public Object[] getOutputs() {
		return this.outputs;
	}

	public Id getMachine() {
		return this.machine;
	}

	@Override
	public String toString() {
		return Arrays.deepToString(this.inputs) + "--["+this.machine+"]->" + Arrays.deepToString(this.outputs);
	}
}
