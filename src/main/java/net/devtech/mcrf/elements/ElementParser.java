package net.devtech.mcrf.elements;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import net.devtech.mcrf.elements.impl.java.ArrayElementParser;
import net.devtech.mcrf.elements.impl.java.FloatingElementParser;
import net.devtech.mcrf.elements.impl.java.IntegerElementParser;
import net.devtech.mcrf.elements.impl.java.StringElementParser;
import net.devtech.mcrf.elements.impl.minecraft.BlockElementParser;
import net.devtech.mcrf.elements.impl.minecraft.EntityElementParser;
import net.devtech.mcrf.elements.impl.minecraft.IdentifierParser;
import net.devtech.mcrf.elements.impl.minecraft.ItemElementParser;
import net.devtech.mcrf.elements.impl.minecraft.ItemStackElementParser;
import net.devtech.mcrf.elements.impl.minecraft.NbtElementParser;
import net.devtech.mcrf.util.world.BlockData;
import net.devtech.mcrf.util.world.EntityData;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public interface ElementParser<T> {
	/**
	 * this is a pattern for matching the end of an element, depending on your parser, you may need this. Arrays don't need this because they have their own delimited ([]), but Integers and Doubles don't, so they use this
	 */
	String ENDING_DELIMITER = "[\\s,+\\-]";

	// minecraft specific types
	ElementParser<CompoundTag> NBT = new NbtElementParser();
	ElementParser<Identifier> IDENTIFIER = new IdentifierParser();
	ElementParser<BlockData> BLOCK = new BlockElementParser();
	ElementParser<EntityData<?>> ENTITY = new EntityElementParser();
	ElementParser<ItemStack> ITEM_STACK = new ItemStackElementParser();
	ElementParser<Item> ITEM = new ItemElementParser();

	// integer types, all support hex, binary and decimal
	ElementParser<Byte> BYTE = (IntegerElementParser) Byte::parseByte;
	ElementParser<Short> SHORT = (IntegerElementParser) Short::parseShort;
	ElementParser<Integer> INTEGER = (IntegerElementParser) Integer::parseInt;
	ElementParser<Long> LONG = (IntegerElementParser) Long::parseLong;
	ElementParser<BigInteger> BIG_INTEGER = (IntegerElementParser) BigInteger::new;

	// floating point types
	ElementParser<Float> FLOAT = (FloatingElementParser<Float>) Float::parseFloat;
	ElementParser<Double> DOUBLE = (FloatingElementParser<Double>) Double::parseDouble;
	ElementParser<BigDecimal> BIG_DECIMAL = (FloatingElementParser<BigDecimal>) BigDecimal::new;

	// special types
	/**
	 * Unquoted string, '+' ',' and newlines must all be escaped with '\'
	 */
	ElementParser<String> STRING = StringElementParser.INSTANCE;
	char[] TRUE = "true".toCharArray();
	char[] FALSE = "fals".toCharArray();
	/**
	 * true/false
	 */
	ElementParser<Boolean> BOOLEAN = r -> {
		char[] fal = new char[4];
		int len = r.read(fal);
		if(len == FALSE.length & Arrays.equals(fal, FALSE)) {
			if(r.read() == 'e') {
				return false;
			} else throw new IllegalArgumentException("fals != false!");
		} else if(len == TRUE.length & Arrays.equals(fal, TRUE)) {
			return true;
		} else {
			throw new IllegalArgumentException(new String(fal) + " is not true/false!");
		}
	};
	ElementParser<Character> CHARACTER = r -> (char) r.read();

	static <T> ElementParser<List<T>> array(ElementParser<T> type) {
		return new ArrayElementParser<>(type);
	}

	/**
	 * do not read what you do not need! mark is supported, use it!
	 */
	T parse(Reader reader) throws IllegalArgumentException, IOException;
}
