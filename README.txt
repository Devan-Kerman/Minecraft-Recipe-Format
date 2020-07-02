MCRF user documentation

All MCRF recipes exist in the given format

(input 1) + (input 2) + (input 3)... --[machine:id]-> (output 1) + (output 2) + (output 3)

different parsers have different specifications.

an exception to remember, the string "--[", may have different implications depending on the code of ther parser,
so it is important for devs to remember to state they are using a retroactive parser or not. If you are a user:
retroactive parsing requires you to escape "--[", by using "\--[" instead.

That's the entire format, I'll go over the default supported parsers here:

Comments:
% this is a comment
% it can start anywhere in the line
% from the start of the percent sign to the end of the line is considered a comment
% comments can be escaped with '\', so for example
This mod is written 100\% in java

Floating Point Numbers:
these all come in decimal format, so 1.4, and negatives are supported, no fractions.

Integers:
Binary is supported via the "0b" prefix, ex. 0b011010
Hexadecimal is also supported via the "0x" prefix, ex. 0xFFFFFF
and of course decimal numbers are supported too, but no prefix is needed, ex. 142221
negatives are supported as well.

Lists:
lists are enclosed with a '[]', all elements inside are comma separated.
ex. [stone, iron, lead]

there are also list or singleton parsers, which, in the absence of a '[' character at the beginning of the element, will
instead just parse one element.

so for example, both of the following are still valid:
stone --[test:test]-> ...
[stone, air] --[test:test]-> ...

Strings:
strings are unquoted but require heavy escaping
newlines, commas, pluses, and dashes all need escaping
they can all be escaped with '\', ex. a '\' that does not
escape anything is a '\', if you want to end with a '\' simply
put "\\".

1\+2=3
/o\ help, I my head hurts

Wildcards:
some recipes (anvil smashing result) will accept a wildcard, this can mean a variety of things and should be outlined by the mod maker
they are described with the '*' character.
ex.
*
hello

    Minecraft Specific Parsers

Blocks: MCRF blocks allow for adjusting properties and blockentity data, ex.
namespace:id
namespace:id[optional=blockstate, property=0]
namespace:id{optional:"nbt_data"}
namespace:id[optional=blockstate, property=0]{optional:"nbt_data"}

Counted Tags: counted tags are a list of tags with an amount, ex. tag:id x3, tag:id, [tag:id, tag:id0], however they're usually prefixed with a '#' to differentiate between tags and the other element, eg. #tag:id

Entities: MCRF supports NBT in entities, ex. entity:id, entity:id{nbt:"data"}

Identifiers: identifiers are just namespaced ids, ex. namespace:id

Ingredients: supports arrays/singletons of tags and items, ex. #[tag:id, tag2:id], #tag:id, item:id, [item:id, item2:id]

Items: same as identifiers but only for items

ItemStacks: an identifier, nbt data and an amount, ex. item:id, item:id{nbt:"data"}, item:id{nbt:"data"} x4, item:id x5

Default recipes:
    Anvils
mcrf/falling_anvil
has support for normal anvil recipes, and falling anvil recipes
for normal anvils, the format is
mcrf:ingredient + mcrf:ingredient + xp --[minecraft:anvil]-> mcrf:ingredient

    Minecraft recipes
mcrf/minecraft
all recipe's first ingredient is the identifier of the recipe, and yes, you can override vanilla recipes with this

crafting recipes:
shapeless recipes can be made with a single, non nested array, these only support vanilla ingredients.
(identifier) + (list of ingredient) --[minecraft:crafting_table]-> (itemstack)
recipe:id + [stone, stone, iron_ingot] --[minecraft:crafting_table]-> stone

shaped recipes are made with nested arays
(identifier) + (list of list of ingredient) --[minecraft:crafting_table]-> (itemstack)
recipe:id + [[stone, stone, stone], [stone, air, stone], [stone, stone, stone]] --[minecraft:crafting_table]-> stone x9


stone cutting:
(identifier) + (ingredient) --[minecraft:stone_cutter] -> (itemstack)
recipe:id + stone --[minecraft:stone_cutter]-> iron_ingot

smithing table:
(identifier) + (ingredient) + (ingredient) --[minecraft:smithing_table]-> (itemstack)

campfire, smoker, blast_furnace, furnace:
(identifier) + (ingredient) + (int : time) --[minecraft:%MACHINE_ID%]-> (itemstack) + (exp)

