import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import net.devtech.mcrf.defaults.MinecraftRecipes;

public class Parser {
	public static void main(String[] args) throws IOException, URISyntaxException {
		MinecraftRecipes.loadFromStream(null, new HashMap<>(), Parser.class.getResourceAsStream("/test.txt"));
	}
}
