package net.devtech.mcrf.elements.impl.minecraft;

import java.io.IOException;
import java.io.Reader;

import net.devtech.mcrf.elements.ElementParser;
import net.devtech.mcrf.util.minecraft.EntityData;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * parses entitydata, looks like this:
 *
 * @param <T>
 */
public class EntityElementParser<T extends Entity> implements ElementParser<EntityData<T>> {
	@Override
	@SuppressWarnings ("unchecked")
	public EntityData<T> parse(Reader reader) throws IllegalArgumentException, IOException {
		Identifier identifier = IDENTIFIER.parse(reader);
		return new EntityData(Registry.ENTITY_TYPE.get(identifier), NBT.parse(reader));
	}
}
