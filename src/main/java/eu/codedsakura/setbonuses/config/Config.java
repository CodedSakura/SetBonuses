package eu.codedsakura.setbonuses.config;

import com.google.gson.annotations.SerializedName;

public class Config {
    public Enchantments enchantments;

    @SerializedName("set-bonuses")
    public SetBonuses setBonuses;

    public static class Enchantments {
        public boolean enabled;
        public ConfigEnchant[] list;
    }
    public static class SetBonuses {
        public boolean enabled;
        public ConfigSetBonus[] list;
    }
}
