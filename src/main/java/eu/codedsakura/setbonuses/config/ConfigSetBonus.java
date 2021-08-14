package eu.codedsakura.setbonuses.config;

public class ConfigSetBonus {
    boolean enabled;
    String id;
    String[] effects;
    float toughness;
    Partial partial = Partial.OFF;

    public enum Partial {
        OFF, REDUCED
    }
}
