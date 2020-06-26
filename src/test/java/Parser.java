import java.io.IOException;

import net.devtech.mcrf.MCRF;
import net.devtech.mcrf.recipes.Recipe;

public class Parser {
	public static void main(String[] args) throws IOException {
		Recipe.parse(MCRF.class.getResourceAsStream("/data/mcrf/shaped/test.mcrf"), MCRF.SHAPED_CRAFTING_RECIPE_SCHEMA);
	}
}
