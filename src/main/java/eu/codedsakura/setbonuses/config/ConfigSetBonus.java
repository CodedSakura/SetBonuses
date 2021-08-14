package eu.codedsakura.setbonuses.config;

public class ConfigSetBonus {
    public boolean enabled = true;
    public String id;
    public String[] effects;
    public float toughness = 0;
    public Partial partial = Partial.OFF;

    public enum Partial {
        OFF, REDUCED
    }
}
