package arcaneenchantments.arcaneenchantments;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.print.attribute.IntegerSyntax;
import java.util.*;

public class RockBlastingEnchantment extends Enchantment implements DigTool {

    /**
     * This enchantment applies to pickaxes.
     * The enchantment allows for random weighted ores to be obtained from mining stone-type blocks.
     * The probability of any given ore being obtained is: Math.random() < max((yieldProb * blockModifier * gunpowderBoost) / 100, 0.5)
     */
    private Map<Item, Integer> yieldItems = new Object2IntArrayMap<>();
    private Map<Block, Integer> blockTypeModifier = new HashMap<>();
    protected RockBlastingEnchantment(Enchantment.Rarity p_44568_, EquipmentSlot... p_44569_) {
        super(p_44568_, EnchantmentCategory.DIGGER, p_44569_);
        // instantiate yield items
        yieldItems.put(Items.DIAMOND, 1);
        yieldItems.put(Items.EMERALD, 1);
        yieldItems.put(Items.LAPIS_LAZULI, 2);
        yieldItems.put(Items.REDSTONE, 2);
        yieldItems.put(Items.GOLD_NUGGET, 2);
        yieldItems.put(Items.COAL, 5);
        yieldItems.put(Items.IRON_NUGGET, 7);
        yieldItems.put(Items.RAW_COPPER, 8);
        yieldItems.put(Items.FLINT, 10);

        //set yield modifier by block type
        blockTypeModifier.put(Blocks.STONE, 1);
        blockTypeModifier.put(Blocks.DEEPSLATE, 2);
        blockTypeModifier.put(Blocks.GRANITE, 3);
        blockTypeModifier.put(Blocks.DIORITE, 3);
        blockTypeModifier.put(Blocks.ANDESITE, 3);
        blockTypeModifier.put(Blocks.TUFF, 4);
        blockTypeModifier.put(Blocks.CALCITE, 5);
    }
    public Component getFullname(int p_44701_) {
        MutableComponent mutablecomponent = Component.translatable(this.getDescriptionId());
        mutablecomponent.withStyle(ChatFormatting.RED);

        if (p_44701_ != 1 || this.getMaxLevel() != 1) {
            mutablecomponent.append(" ").append(Component.translatable("enchantment.level." + p_44701_));
        }

        return mutablecomponent;
    }
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = "Rockblasting";//Util.makeDescriptionId("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(this));
        }

        return this.descriptionId;
    }
    public int getMaxLevel() {
        return 5;
    }

    private Item tryYield(BlockEvent.BreakEvent event, Block block, boolean hasGunpowder, int enchLevel) {

        // first, run check of whether we will yield *something*
        if(Math.random() > ((enchLevel * 0.1) + 0.2)) { return null; }
        if(blockTypeModifier.get(block) == null) { return null; }

        final int maxProbFactor = 12;
        final int minProb = 5;

        double sampledProb = (Math.random() * maxProbFactor) + minProb;
        double thresh = Math.max(sampledProb - blockTypeModifier.get(block), 0) * (hasGunpowder ? 1f : 5f);
        ArcaneEnchantments.LOGGER.info("THRESHOLD {}", thresh);
        ArcaneEnchantments.LOGGER.info("SAMPLEDPROB {}", sampledProb);

        for(Item item : yieldItems.keySet()) {
            if(thresh < yieldItems.get(item)) {
                // we have a match for an item to yield
                // now, check if we need to suppress the normal drops
                if(Math.random() < ((maxProbFactor + minProb) - thresh)) {
                    // don't drop block
                    //TODO
                }
                return item;
            }
        }
        return null;
    }

    @Override
    public void handleBreakBlock(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        //Confirm that enchantment was present
        int enchLvl = 0;
        if(!player.getMainHandItem().getEnchantmentTags().isEmpty()) {
            // find ForgerEnchantment and corresponding level
            Map<Enchantment, Integer> enchantments = player.getMainHandItem().getItem().getAllEnchantments(event.getPlayer().getMainHandItem());
            boolean hasEnchantment = false;
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                if (entry.getKey() instanceof RockBlastingEnchantment) {
                    enchLvl = entry.getValue();
                    hasEnchantment = true;
                }
            }
            if (!hasEnchantment) {
                return;
            }
        }

        int gunpowderSlot = player.getInventory().findSlotMatchingItem(new ItemStack(Items.GUNPOWDER));
        boolean hasGunpowder = (gunpowderSlot != -1);

        Block blockBroken = event.getState().getBlock();

        Item yieldedItem = tryYield(event, blockBroken, hasGunpowder, enchLvl);
        if(yieldedItem == null) { return; }
        ItemEntity dropEntity = new ItemEntity(player.getLevel(),
                event.getPos().getX(),
                event.getPos().getY(),
                event.getPos().getZ(),
                new ItemStack(yieldedItem));
        dropEntity.spawnAtLocation(yieldedItem);
        ArcaneEnchantments.LOGGER.info("SPAWNEDITEM {}", yieldedItem);

        if(hasGunpowder) player.getInventory().removeItem(gunpowderSlot, 1);
    }
}
