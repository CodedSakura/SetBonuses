package eu.codedsakura.setbonuses;

import eu.codedsakura.setbonuses.config.Config;
import eu.codedsakura.setbonuses.config.ConfigReader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetBonuses implements ModInitializer {
    public static final Logger logger = LogManager.getLogger("Set Bonuses");
    public static Config CONFIG = null;

    @Override
    public void onInitialize() {
        logger.info("[Set Bonuses] Bonus-ing the sets!");
        if (CONFIG == null) {
            try {
                CONFIG = new ConfigReader(FabricLoader.getInstance().getConfigDir(), "SetBonuses.json").read();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (CONFIG.enchantments.enabled) {
            EnchantmentFactory.register();
        }
    }
}
