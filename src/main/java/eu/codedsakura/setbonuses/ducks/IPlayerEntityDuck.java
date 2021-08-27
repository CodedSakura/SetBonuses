package eu.codedsakura.setbonuses.ducks;

public interface IPlayerEntityDuck {
    void toggle(String name);
    boolean isDisabled(String name);

    float getAddProtection();
    float getAddToughness();
    float getAddKnockbackResistance();
}
