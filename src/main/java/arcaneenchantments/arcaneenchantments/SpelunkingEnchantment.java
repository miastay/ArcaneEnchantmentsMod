package arcaneenchantments.arcaneenchantments;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.event.level.BlockEvent;

import java.util.Map;

public class SpelunkingEnchantment extends ArcaneEnchantment implements DigTool {
    protected SpelunkingEnchantment(Rarity p_44676_, EquipmentSlot[] p_44678_) {
        super(p_44676_, EnchantmentCategory.DIGGER, p_44678_);
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
            this.descriptionId = "Spelunking";//Util.makeDescriptionId("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(this));
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @Override
    public void handleBreakBlock(BlockEvent.BreakEvent event) {
        Map<Enchantment, Integer> enchantments = null;
        try {
            Player perp = event.getPlayer();
            enchantments = perp.getMainHandItem().getItem().getAllEnchantments(perp.getMainHandItem());
        } catch (Exception e) {
            //ArcaneEnchantments.LOGGER.info("ERROR {}", e);
            return;
        }
        if(enchantments == null) { return; }

        int enchLvl = usedEnchantment(this, enchantments);
        if(enchLvl == -1) { return; }

        pulseNearbyOres(event);

        // lose durability for performing function

        Item toolUsed = event.getPlayer().getMainHandItem().getItem();
        int currentDurability = toolUsed.getDamage(toolUsed.getDefaultInstance());
        toolUsed.setDamage(toolUsed.getDefaultInstance(), currentDurability - (11 - enchLvl));
    }

    public void pulseNearbyOres(BlockEvent.BreakEvent event) {
        ChunkAccess chunk = Minecraft.getInstance().level.getChunk(event.getPos());
        int xi = event.getPos().getX();
        int yi = event.getPos().getY();
        int zi = event.getPos().getZ();
        for(int d = 1; d < 10; d++) {
            BlockState nextBlock = chunk.getBlockState(new BlockPos(xi + d, yi, zi));
            spawnOreParticles(nextBlock.getBlock());
            ArcaneEnchantments.LOGGER.info("NEXT POSITION: {}", xi + d);
            ArcaneEnchantments.LOGGER.info("NEXT POSITION BLOCK: {}", nextBlock.getBlock().toString());
        }
        return;
    }

    private void spawnOreParticles(Block ore) {
        if (ore.equals(Blocks.COAL_ORE)) {
           // Minecraft.getInstance().level.addParticle();
        }
    }

}
