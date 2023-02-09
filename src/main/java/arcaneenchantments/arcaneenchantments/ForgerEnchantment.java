package arcaneenchantments.arcaneenchantments;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.UntouchingEnchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.level.BlockEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForgerEnchantment extends Enchantment implements DigTool {
    public ForgerEnchantment(Enchantment.Rarity p_44568_, EquipmentSlot... p_44569_) {
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
        mutablecomponent.withStyle(ChatFormatting.GOLD);

        if (p_44701_ != 1 || this.getMaxLevel() != 1) {
            mutablecomponent.append(" ").append(Component.translatable("enchantment.level." + p_44701_));
        }

        return mutablecomponent;
    }
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = "Forging";//Util.makeDescriptionId("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(this));
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @Override
    public void handleBreakBlock(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Tag enchTag = null;
        Integer enchLvl = null;

        Map<Item, Item> smeltableItems = new HashMap<>();
        smeltableItems.put(Items.RAW_IRON, Items.IRON_INGOT);
        smeltableItems.put(Items.RAW_GOLD, Items.GOLD_INGOT);
        smeltableItems.put(Items.RAW_COPPER, Items.COPPER_INGOT);
        smeltableItems.put(Items.NETHERITE_SCRAP, Items.NETHERITE_INGOT);

        if(!event.getPlayer().getMainHandItem().getEnchantmentTags().isEmpty()) {
            // find ForgerEnchantment and corresponding level
            Map<Enchantment, Integer> enchantments = event.getPlayer().getMainHandItem().getItem().getAllEnchantments(event.getPlayer().getMainHandItem());
            boolean hasForgerEnchantment = false;
            for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                if(entry.getKey() instanceof UntouchingEnchantment) {
                    // tool has silk touch, cannot apply forging enchantment
                    return;
                }
                else if(entry.getKey() instanceof ForgerEnchantment) {
                    enchLvl = entry.getValue();
                    hasForgerEnchantment = true;
                }
            }
            if(!hasForgerEnchantment) { return; }
            // now perform logic based on enchantment level value
            Block blockBroken = event.getState().getBlock();
            List<ItemStack> blockDrops = blockBroken.getDrops(event.getState(), Minecraft.getInstance().getSingleplayerServer().getLevel(Minecraft.getInstance().level.dimension()), event.getPos(), null);
            if(blockDrops.isEmpty()) { return; }
            Item drop = blockDrops.get(0).getItem();
            //get smelted form for valid item drop
            if(smeltableItems.containsKey(drop)) {
                Item toDrop = smeltableItems.get(drop);
                float chanceOfDrop = enchLvl / 12f;
                if(Math.random() < chanceOfDrop) {
                    ItemEntity dropEntity = new ItemEntity( player.getLevel(),
                            event.getPos().getX(),
                            event.getPos().getY(),
                            event.getPos().getZ(),
                            new ItemStack(toDrop));
                    dropEntity.spawnAtLocation(toDrop);
                    /*TODO spawn particles*/
                }
            }
        }
    }
}