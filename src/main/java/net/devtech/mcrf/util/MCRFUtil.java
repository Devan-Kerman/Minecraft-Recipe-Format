package net.devtech.mcrf.util;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.devtech.mcrf.mixin.DefaultedListAccess;
import net.devtech.mcrf.mixin.ItemEntityAccess;
import net.devtech.mcrf.util.minecraft.CountedTags;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.tag.TagRegistry;

/**
 * some utility functions
 */
public final class MCRFUtil {
	/**
	 * execute an in-world recipe (item entities)
	 * @param world the world
	 * @param pos where to search for items
	 * @param ingredients the ingredients {@link net.devtech.mcrf.elements.ElementParser#MCRF_INGREDIENT}
	 * @param output the result
	 * @return true if the recipe was sucessfully executed
	 */
	public static boolean executeInWorldRecipe(World world, BlockPos pos, List<Either<CountedTags<Item>, ItemStack>> ingredients, List<ItemStack> output) {
		List<ItemEntity> stacks = world.getEntitiesByType(EntityType.ITEM, new Box(pos), e -> true);
		for (ItemEntity stack : stacks) {
			((ItemEntityAccess) stack).callTryMerge();
		}

		Object2IntMap<ItemEntity> removed = new Object2IntOpenHashMap<>();
		if (!ingredients.isEmpty()) {
			boolean allSatisfied = true;
			for (Either<CountedTags<Item>, ItemStack> ingredient : ingredients) {
				if (!stacks.removeIf(i -> MCRFUtil.suffices(i.getStack(), ingredient) && removed.put(i, MCRFUtil.getAmount(ingredient)) != Integer.MIN_VALUE)) {
					allSatisfied = false;
				}
			}

			if (!allSatisfied) {
				return false;
			}
		}

		// enact transformation
		removed.forEach((i, a) -> i.getStack()
		                           .decrement(a));
		output = new ArrayList<>(output);
		for (int i = 0; i < output.size(); i++) {
			output.set(i,
			           output.get(i)
			                 .copy()
			);
		}

		ItemScatterer.spawn(world, pos.up(), DefaultedListAccess.createDefaultedList(output, ItemStack.EMPTY));
		return true;
	}

	/**
	 * checks if the itemstack is valid and has enough items to satisfy the ingredient
	 */
	public static boolean suffices(ItemStack stack, Either<CountedTags<Item>, ItemStack> ingredient) {
		Optional<CountedTags<Item>> tag = ingredient.left();
		Item item = stack.getItem();
		return tag.map(ids -> ids.getIdentifiers()
		                         .stream()
		                         .map(TagRegistry::item)
		                         .anyMatch(t -> t.contains(item)))
		          .orElseGet(() -> hasAtleast(stack,
		                                      ingredient.right()
		                                                .get()
		          ));
	}

	public static int getAmount(Either<CountedTags<Item>, ItemStack> ingredient) {
		return ingredient.left()
		                 .isPresent() ? ingredient.left()
		                                          .get()
		                                          .getAmount() : ingredient.right()
		                                                                   .get()
		                                                                   .getCount();
	}

	public static boolean hasAtleast(ItemStack item, ItemStack ingredient) {
		return item.getCount() >= ingredient.getCount() && item.getItem() == ingredient.getItem() && match(ingredient.getTag(), item.getTag());
	}

	/**
	 * checks if the 2 targets match
	 * @param tag the tag
	 * @param target the target tag
	 * @return true if the target tag contains all the values of the other tag (and not / nor vice versa!)
	 */
	public static boolean match(Tag tag, Tag target) {
		if (tag == null && target == null) {
			return true;
		}
		if (tag == null ^ target == null) {
			return false;
		}
		if (tag.getClass() == target.getClass()) {
			if (tag instanceof CompoundTag) {
				for (String key : ((CompoundTag) tag).getKeys()) {
					if (!match(((CompoundTag) tag).get(key), ((CompoundTag) target).get(key))) {
						return false;
					}
				}
				return true;
			} else {
				return tag.equals(target);
			}
		} else {
			return false;
		}
	}

	public static int peek(Reader reader) throws IOException {
		reader.mark(1);
		int peek = reader.read();
		reader.reset();
		return peek;
	}

	public static void skipWhitespace(Reader reader) throws IOException {
		int curr;
		while (true) {
			reader.mark(1);
			curr = reader.read();
			if (curr == -1) {
				break;
			}
			if (!(Character.isWhitespace(curr) | curr == 0)) {
				reader.reset();
				break;
			}
		}
	}

	/**
	 * reads between the 2 chars, this is meant for parenthesis, so [[]] becomes [] and not [ because it counts
	 * the number of starting and ending characters.
	 */
	public static String readBetween(Reader reader, char start, char end) throws IOException {
		reader.mark(1);
		if (reader.read() != start) {
			reader.reset();
			return "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(start);
		int starts = 1;
		int chr;
		while ((chr = reader.read()) != -1) {
			builder.append((char) chr);
			if (chr == start) {
				starts++;
			} else if (chr == end) {
				starts--;
				if (starts == 0) {
					break;
				}
			}
		}
		return builder.toString();
	}
}
