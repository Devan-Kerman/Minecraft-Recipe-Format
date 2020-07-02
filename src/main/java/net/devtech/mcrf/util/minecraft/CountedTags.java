package net.devtech.mcrf.util.minecraft;

import java.util.List;

import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

// todo add nbt support?

/**
 * it's basically a list of tags with a count
 * [tag:id, tag:id2] xAMOUNT (in most things it's actually just prefixed with a #, so it'll be #[id:bee, id:tree]), the array brackets are usually optional too
 * @param <T>
 */
public abstract class CountedTags<T> {
	private final List<Identifier> identifiers;
	private final int amount;

	public CountedTags(List<Identifier> identifiers, int amount) {
		this.identifiers = identifiers;
		this.amount = amount;
	}

	public List<Identifier> getIdentifiers() {
		return this.identifiers;
	}
	
	protected abstract Tag<T> get(Identifier identifier);
	
	public boolean matches(T object) {
		for (Identifier id : this.identifiers) {
			if(this.get(id).contains(object))
				return true;
		}
		return false;
	}

	public int getAmount() {
		return this.amount;
	}
}
