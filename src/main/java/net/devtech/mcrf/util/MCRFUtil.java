package net.devtech.mcrf.util;

import java.io.IOException;
import java.io.Reader;

import net.devtech.mcrf.mixin.ItemEntityAccess;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public final class MCRFUtil {


	public static boolean hasAtleast(ItemStack item, ItemStack ingredient) {
		return item.getCount() >= ingredient.getCount() && item.getItem() == ingredient.getItem() && match(ingredient.getTag(), item.getTag());
	}

	public static boolean match(Tag tag, Tag target) {
		if(tag == null && target == null)
			return true;
		if(tag == null ^ target == null)
			return false;
		if(tag.getClass() == target.getClass()) {
			if(tag instanceof CompoundTag) {
				for (String key : ((CompoundTag) tag).getKeys()) {
					if(!match(((CompoundTag) tag).get(key), ((CompoundTag)target).get(key))) {
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

	public static String readBetween(Reader reader, char start, char end) throws IOException {
		reader.mark(1);
		if(reader.read() != start) {
			reader.reset();
			return "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(start);
		int starts = 1;
		int chr;
		while ((chr = reader.read()) != -1) {
			builder.append((char)chr);
			if(chr == start) {
				starts++;
			} else if(chr == end) {
				starts--;
				if(starts == 0)
					break;
			}
		}
		return builder.toString();
	}
}
