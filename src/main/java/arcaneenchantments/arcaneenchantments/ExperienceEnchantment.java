package arcaneenchantments.arcaneenchantments;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.UntouchingEnchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.level.BlockEvent;

import java.util.HashMap;
import java.util.Map;

public class ExperienceEnchantment extends ArcaneEnchantment implements DigTool, WeaponTool {

    /**
     * This enchantment applies to pickaxes.
     * The enchantment allows for random weighted ores to be obtained from mining stone-type blocks.
     * The probability of any given ore being obtained is: Math.random() < max((yieldProb * blockModifier * gunpowderBoost) / 100, 0.5)
     */
    protected ExperienceEnchantment(Enchantment.Rarity p_44568_, EquipmentSlot... p_44569_) {
        super(p_44568_, EnchantmentCategory.DIGGER, p_44569_);
    }

    public int getMinCost(int p_44572_) {
        return 1 + (p_44572_ - 1) * 10;
    }

    public int getMaxCost(int p_44574_) {
        return this.getMinCost(p_44574_) + 15;
    }

    public int getMaxLevel() {
        return 3;
    }

    public Component getFullname(int p_44701_) {
        MutableComponent mutablecomponent = Component.translatable(this.getDescriptionId());
        mutablecomponent.withStyle(ChatFormatting.GREEN);

        if (p_44701_ != 1 || this.getMaxLevel() != 1) {
            mutablecomponent.append(" ").append(Component.translatable("enchantment.level." + p_44701_));
        }

        return mutablecomponent;
    }
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = "Experience";//Util.makeDescriptionId("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(this));
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }


    @Override
    public void handleBreakBlock(BlockEvent.BreakEvent event) {
        int expToDrop = event.getExpToDrop();
        int enchLvl = 0;
        if(event.getPlayer().getMainHandItem().getEnchantmentTags().isEmpty()) { return; }
        Map<Enchantment, Integer> enchantments = event.getPlayer().getMainHandItem().getItem().getAllEnchantments(event.getPlayer().getMainHandItem());
        boolean hasExperienceEnchantment = false;
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            if (entry.getKey() instanceof ExperienceEnchantment) {
                enchLvl = entry.getValue();
                hasExperienceEnchantment = true;
            }
        }
        if (!hasExperienceEnchantment) {
            return;
        }
        expToDrop = (int) Math.ceil(expToDrop * (1 + (enchLvl * 0.33)));
        event.setExpToDrop(expToDrop);
    }

    @Override
    public void handleEntityEvent(LivingDeathEvent event) {

        int expToDrop = event.getEntity().getExperienceReward();
        Map<Enchantment, Integer> enchantments = null;
        try {
            Player perp = (Player)event.getSource().getEntity();
            enchantments = perp.getMainHandItem().getItem().getAllEnchantments(perp.getMainHandItem());
        } catch (Exception e) {
            //ArcaneEnchantments.LOGGER.info("ERROR {}", e);
            return;
        }
            if(enchantments == null) { return; }

        int enchLvl = usedEnchantment(this, enchantments);
            if(enchLvl == -1) { return; }

        ExperienceOrb.award((ServerLevel)event.getEntity().level, event.getEntity().position(), (int) Math.ceil(expToDrop * (enchLvl * 0.33)));
        ArcaneEnchantments.LOGGER.info("DEATH RESULT, EXP: {}", expToDrop);
        ArcaneEnchantments.LOGGER.info("DEATH RESULT, ENCHLVL: {}", enchLvl);
        return;
    }
}