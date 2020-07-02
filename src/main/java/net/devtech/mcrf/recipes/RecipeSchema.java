package net.devtech.mcrf.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.elements.impl.RetroactiveElementParser;
import net.devtech.mcrf.util.Id;

public interface RecipeSchema {
	interface RetroactiveSchema extends RecipeSchema {
		@Override
		default Iterator<ElementParser<?>> getInputs() {
			return this.getInputs(null);
		}

		Iterator<ElementParser<?>> getInputs(Id machine);
	}

	Iterator<ElementParser<?>> getInputs();

	Iterator<ElementParser<?>> getOutputs(Id machine);

	/**
	 * a dynamic schema can change it's element types during and after parsing,
	 * although it may be less performant, it allows modders to parse multiple
	 * recipes from the same file, and add more rich recipe systems.
	 *
	 * dynamic outputs do not change the performance, however if you use dynamic inputs,
	 * you must use a {@link RetroactiveElementParser} or other element parser that supports
	 * retroactive parsing. for example if all of your recipes start with an ID, then some
	 * combination of elements after it which depends on the machine {@link net.devtech.mcrf.defaults.MinecraftRecipes}
	 * you can first read all of the inputs, store them for later, then parse the machine id, and the outputs, then
	 * go back and parse the inputs. This is what enables us to have all vanilla recipes in a single file.
	 */
	class DynamicBuilder implements RetroactiveSchema {
		private final Map<Id, List<ElementParser<?>>> inputs = new HashMap<>();
		private final Map<Id, List<ElementParser<?>>> outputs = new HashMap<>();

		public DynamicBuilder addDefault(ElementParser<?>... defaultInputs) {
			return this.addInput(null, defaultInputs);
		}

		public DynamicBuilder addInputs(Id[] id, ElementParser<?>... inputs) {
			for (Id identifier : id) {
				this.addInput(identifier, inputs);
			}
			return this;
		}

		public DynamicBuilder addInput(Id id, ElementParser<?>... inputs) {
			this.inputs.computeIfAbsent(id, i -> new ArrayList<>()).addAll(Arrays.asList(inputs));
			return this;
		}

		public DynamicBuilder addOutputs(Id[] ids, ElementParser<?>...outputs) {
			for (Id id : ids) {
				this.addOutput(id, outputs);
			}
			return this;
		}

		public DynamicBuilder addOutput(Id id, ElementParser<?>... outputs) {
			this.outputs.computeIfAbsent(id, i -> new ArrayList<>()).addAll(Arrays.asList(outputs));
			return this;
		}

		@Override
		public Iterator<ElementParser<?>> getInputs(Id machine) {
			return this.inputs.getOrDefault(machine, Collections.emptyList()).iterator();
		}

		@Override
		public Iterator<ElementParser<?>> getOutputs(Id machine) {
			return this.outputs.getOrDefault(machine, Collections.emptyList()).iterator();
		}
	}

	/**
	 * a regular schema who's outputs or inputs does not change based on parameters
	 */
	class Builder implements RecipeSchema {
		private final List<ElementParser<?>> inputs = new ArrayList<>();
		private final List<ElementParser<?>> outputs = new ArrayList<>();
		private final Id[] machine;

		public Builder(Id...machine) {
			this.machine = machine;
			Arrays.sort(machine);
		}

		public Builder addInput(ElementParser<?> type) {
			if (type.needsPostProcessing()) {
				throw new IllegalArgumentException("use RecipeSchema.Dynamic for post processing elements!");
			}
			this.inputs.add(type);
			return this;
		}

		public Builder addOutput(ElementParser<?> type) {
			this.outputs.add(type);
			return this;
		}

		@Override
		public Iterator<ElementParser<?>> getInputs() {
			return this.inputs.iterator();
		}

		@Override
		public Iterator<ElementParser<?>> getOutputs(Id machine) {
			if (Arrays.binarySearch(this.machine, machine) != -1) {
				return this.outputs.iterator();
			} else {
				throw new IllegalArgumentException("attempted to make recipe with machine " + Arrays.toString(this.machine) + " but user entered " + machine + '!');
			}
		}
	}
}
