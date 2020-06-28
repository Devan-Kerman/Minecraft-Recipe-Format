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

	class DynamicBuilder implements RetroactiveSchema {
		private final Map<Id, List<ElementParser<?>>> inputs = new HashMap<>();
		private final Map<Id, List<ElementParser<?>>> outputs = new HashMap<>();

		public DynamicBuilder addDefault(ElementParser<?>... defaultInputs) {
			return this.addInput((Id) null, defaultInputs);
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

	class Builder implements RecipeSchema {
		private final List<ElementParser<?>> inputs = new ArrayList<>();
		private final List<ElementParser<?>> outputs = new ArrayList<>();
		private final Id machine;

		public Builder(Id machine) {
			this.machine = machine;
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
			if (this.machine.equals(machine)) {
				return this.outputs.iterator();
			} else {
				throw new IllegalArgumentException("attempted to make recipe with machine " + this.machine + " but user entered " + machine + '!');
			}
		}
	}
}
