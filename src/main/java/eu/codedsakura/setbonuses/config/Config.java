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

        protected void verify() {
            if (list == null) list = new ConfigEnchant[0];
            for (ConfigEnchant configEnchant : list) configEnchant.verify();
        }
    }
    public static class SetBonuses {
        public boolean enabled = true;
        public ConfigSetBonus[] list = new ConfigSetBonus[0];

        public SetBonuses() {}
        public SetBonuses(boolean enabled) {
            this.enabled = enabled;
        }

        protected void verify() {
            if (list == null) list = new ConfigSetBonus[0];
            for (ConfigSetBonus configSetBonus : list) configSetBonus.verify();
        }
    }

    public void verify() {
        if (enchantments == null) enchantments = new Enchantments(false);
        if (setBonuses == null) setBonuses = new SetBonuses(false);
        enchantments.verify();
        setBonuses.verify();
    }
}
