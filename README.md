# Set Bonuses
This 1.17.1 Fabric mod requires [Fabric API](). 
It was written by [CodedSakura](http://codedsakura.eu/) 
for the [2021 ServJam](https://servjam.xyz/summer21/).

It includes in itself [Polymer](https://github.com/Patbox/polymer) 
and [Server Translations API](https://github.com/arthurbambou/Server-Translations).

Set Bonuses adds fully configurable enchantments and set bonuses.


## Config

Located in `config/SetBonuses.json`.

Root:  
`enchantments`:  
&gt; `enabled`: boolean = `true`  
&gt; `list`: [ConfigEnchantment](#configenchantment)[] = `[]`  
`setBonuses`:  
&gt; `enabled`: boolean = `true`  
&gt; `list`: [ConfigSetBonus](#configsetbonus)[] = `[]`

### ConfigEnchantment
[Source](https://github.com/CodedSakura/SetBonuses/blob/873ed931ca72a73d6d0cdda353c3b85a420d95db/src/main/java/eu/codedsakura/setbonuses/config/ConfigEnchant.java#L9)

`enabled`: boolean = `true`    
`id`: string [**Required!**]  
`levels`: int = `1` - amount of enchantment levels  
`toggleable`: boolean = `true` - weather the player can toggle the enchantment using MMB  
`treasue`: boolean = `false` - is treasure enchant  
`cursed`: boolean = `false` - is cursed enchant  
`forEnchantedBook`: boolean = `true` - can be gotten in an enchantment table  
`forRandomSelection`: boolean = `true` - can be gotten by villager trading  
`slots`: string[] = `["HEAD", "CHEST", "LEGS", "FEET"]` - on what slots it will work  
`target`: string = `"ARMOR"` - on what can be applied 
(valid values: `"ARMOR"`, `"ARMOR_HEAD"`, `"ARMOR_CHEST"`, `"ARMOR_LEGS"`, `"ARMOR_FEET"`)  
`rarity`: string = `"UNCOMMON"` - (valid values: `"COMMON"`, `"UNCOMMON"`, `"RARE"`, `"VERY_RARE"`)  
`power`: - level scaling in enchantment table  
&gt; `base`: int = `5` - base level  
&gt; `delta`: int = `5` - level range  
&gt; `increment`: int = `5` - increase to base per level  
`stacking`: string = `"MAX"` - `"MAX"` - highest, `"ADDITIVE"` - sum, `"MULTIPLICATIVE"` - product  
`effect`: [] = `[]` - array of potion effects  
&gt; `id`: string [**Required!**]  
&gt; `duration`: int = `210`
&gt; `ambient`: boolean = `false`
&gt; `showParticles`: boolean = `false`
&gt; `showIcon`: boolean = `true`
`materials`: string[] = `[]` - empty array means any item, otherwise functions as whitelist
(valid values: `"LEATHER"`, `"CHAIN"`, `"IRON"`, `"GOLD"`, `"DIAMOND"`, `"TURTLE"`, `"NETHERITE"`)

### ConfigSetBonus
[Source](https://github.com/CodedSakura/SetBonuses/blob/873ed931ca72a73d6d0cdda353c3b85a420d95db/src/main/java/eu/codedsakura/setbonuses/config/ConfigSetBonus.java#L7)

`enabled`: boolean = `true`  
`effect`: [] = `[]` - array of potion effects  
&gt; `id`: string [**Required!**]  
&gt; `duration`: int = `210`  
&gt; `ambient`: boolean = `false`  
&gt; `showParticles`: boolean = `false`  
&gt; `showIcon`: boolean = `true`  
`toughness`: float = `0` - added toughness per piece  
`protection`: float = `0` - added protection per piece  
`knockbackResistance`: float = `0` - added knockback resistance per piece  
`partial`: string = `"OFF"` - `"OFF"` - only if all 4 pieces, `"REDUCED_3"` - reduced by half if 3 of 4 pieces
`"MISSING_CHEST"` - 4 pieces or mismatched chest piece  
`material`: string [**Required!**] - (valid values: `"LEATHER"`, `"CHAIN"`, `"IRON"`, `"GOLD"`, `"DIAMOND"`, `"NETHERITE"`)  


## Thanks

Many thanks to the helpful people over at the ServJam discord and Fabric Server-Side Development discord!
Without your help ~~I couldn't have rushed it last evening to completion~~ it wouldn't be where it ended up being!
Special shout-outs to (in no particular order) BradBot_1#2042, TheEpicBlock_TEB#0452, Patbox#4687, Potatoboy#3053 and SpaceClouds42#2255!
