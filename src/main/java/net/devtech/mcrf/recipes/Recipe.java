package net.devtech.mcrf.recipes;

import static net.devtech.mcrf.util.MCRFUtil.skipWhitespace;

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

import com.google.common.collect.Iterators;
import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.util.MCRFUtil;
import net.devtech.mcrf.util.Id;
import net.devtech.mcrf.util.io.CommentStrippingReader;
import net.devtech.mcrf.util.io.LineTrackingReader;

/**
 * a machine's recipe
 */
public final class Recipe {
	public static final String START_STR = "--[";

	private static final char[] START = START_STR.toCharArray();
	private static final char[] END = "]->".toCharArray();

	private final Id machine;
	private final Object[] inputs;
	private final Object[] outputs;

	private Recipe(Id machine, Object[] inputs, Object[] outputs) {
		this.machine = machine;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	/**
	 * parse all the recipes in a given file
	 * @see net.devtech.mcrf.defaults.MinecraftRecipes
	 * @see net.devtech.mcrf.util.RefreshingRecipe
	 */
	public static List<Recipe> parse(LineTrackingReader reader, RecipeSchema schema) throws IOException {
		List<Recipe> recipes = new ArrayList<>();
		while (true) {
			MCRFUtil.skipWhitespace(reader);
			reader.mark(1);
			int chr = reader.read();
			if(chr == -1)
				break;
			reader.reset();
			try {
				Object[] inputs = read(reader, schema.getInputs(), false);
				Id id = readMachine(reader);
				Object[] outputs = read(reader, schema.getOutputs(id), true);
				recipes.add(new Recipe(id, postProcess(inputs, id, schema), outputs));
			} catch (Throwable t) {
				System.err.println("Syntax exception on line: " + reader.getLineNumber());
				System.err.println(reader.getLine() + " <-- error");
				t.printStackTrace();
			}
		}
		return recipes;
	}

	private static Object[] postProcess(Object[] inputs, Id machine, RecipeSchema schema) {
		if(schema instanceof RecipeSchema.RetroactiveSchema) {
			ArrayList<Object> list = new ArrayList<>();
			Iterator<Object> iterator = Iterators.forArray(inputs);
			Iterator<ElementParser<?>> parsers = schema.getInputs();
			while (iterator.hasNext()) {
				Object next = iterator.next();
				ElementParser parser = parsers.next();
				if(parser.needsPostProcessing()) {
					list.addAll(Arrays.asList(parser.postProcess(schema, machine, next)));
				} else {
					list.add(next);
				}
			}
			return list.toArray();
		}
		return inputs;
	}

	public static List<Recipe> parse(InputStream stream, RecipeSchema schema) throws IOException {
		return parse(new LineTrackingReader(new BufferedReader(new CommentStrippingReader(new InputStreamReader(stream)))), schema);
	}

	private static Id readMachine(Reader reader) throws IOException {
		char[] arr = new char[3];
		String stage = "starting";
		if (reader.read(arr)
		    == START.length && Arrays.equals(arr, START)) {
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

	/**
	 * @deprecated internal api
	 */
	@Deprecated
	public static Object[] read(Reader reader, Iterator<ElementParser<?>> iterator, boolean ignoreEnd) throws IOException {
		List<Object> inputs = new ArrayList<>();
		skipWhitespace(reader);
		while (iterator.hasNext()) {
			ElementParser<?> input = iterator.next();
			if(input.finalizing() && iterator.hasNext()) {
				throw new IllegalArgumentException("Finalizing element parser is not at the end of the stream!");
			}
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
						throw new MissingFormatArgumentException("Error extra character : '" + (char)c + "' found!");
					}
				}
				skipWhitespace(reader);
			}

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
