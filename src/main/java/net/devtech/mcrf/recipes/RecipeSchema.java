package net.devtech.mcrf.recipes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterators;
import net.devtech.mcrf.elements.ElementParser;

public interface RecipeSchema {
	Iterator<ElementParser<?>> getInputs();
	Iterator<ElementParser<?>> getOutputs();

	class Builder implements RecipeSchema {
		private final List<ElementParser<?>> inputs = new ArrayList<>();
		private final List<ElementParser<?>> outputs = new ArrayList<>();

		public Builder addInput(ElementParser<?> type) {
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
		public Iterator<ElementParser<?>> getOutputs() {
			return this.outputs.iterator();
		}
	}
}
