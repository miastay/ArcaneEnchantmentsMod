package arcaneenchantments.arcaneenchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Map;

public class ArcaneEnchantment extends Enchantment {

    protected ArcaneEnchantment(Rarity p_44676_, EnchantmentCategory p_44677_, EquipmentSlot[] p_44678_) {
        super(p_44676_, p_44677_, p_44678_);
    }

    public int usedEnchantment(Enchantment enchantment, Map<Enchantment, Integer> enchantments) {
        boolean hasEnchantment = false;
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            if (entry.getKey() == enchantment) {
                return entry.getValue();
            }
        }
        return -1;
    }

    public enum ArcaneRarity {
        COMMON(1),
        UNCOMMON(2),
        UNIQUE(4),
        RARE(8),
        MYTHICAL(15),
        ARCANE(25);
        private final int weight;

        private ArcaneRarity(int p_44715_) {
            this.weight = p_44715_;
        }

        public int getWeight() {
            return this.weight;
        }
    }


}
