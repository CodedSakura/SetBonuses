package eu.codedsakura.setbonuses.config;

public class Config {
    public Enchantments enchantments = new Enchantments(false);
    public SetBonuses setBonuses = new SetBonuses(false);

    public int updateInterval = 10;

    public static class Enchantments {
        public boolean enabled = true;
        public ConfigEnchant[] list = new ConfigEnchant[0];

        public Enchantments() {}
        public Enchantments(boolean enabled) {
            this.enabled = enabled;
        }
    }
    public static class SetBonuses {
        public boolean enabled = true;
        public ConfigSetBonus[] list = new ConfigSetBonus[0];

        public SetBonuses() {}
        public SetBonuses(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
