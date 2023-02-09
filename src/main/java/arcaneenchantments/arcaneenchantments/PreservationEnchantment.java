package arcaneenchantments.arcaneenchantments;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.level.BlockEvent;

public class PreservationEnchantment extends ArcaneEnchantment implements DigTool, WeaponTool {
    protected PreservationEnchantment(Rarity p_44676_, EnchantmentCategory p_44677_, EquipmentSlot[] p_44678_) {
        super(p_44676_, p_44677_, p_44678_);
    }

    public int getMinCost(int p_44572_) {
        return 1 + (p_44572_ - 1) * 10;
    }

    public int getMaxCost(int p_44574_) {
        return this.getMinCost(p_44574_) + 15;
    }

    public int getMaxLevel() {
        return 1;
    }

    public Component getFullname(int p_44701_) {
        MutableComponent mutablecomponent = Component.translatable(this.getDescriptionId());
        mutablecomponent.withStyle(ChatFormatting.GOLD);

        if (p_44701_ != 1 || this.getMaxLevel() != 1) {
            mutablecomponent.append(" ").append(Component.translatable("enchantment.level." + p_44701_));
        }

        return mutablecomponent;
    }
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = "Blessing of Preservation";//Util.makeDescriptionId("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(this));
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @Override
    public void handleBreakBlock(BlockEvent.BreakEvent event) {

    }

    @Override
    public void handleEntityEvent(LivingDeathEvent event) {

    }
}
