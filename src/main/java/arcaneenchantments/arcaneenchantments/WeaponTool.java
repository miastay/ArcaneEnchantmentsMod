package arcaneenchantments.arcaneenchantments;

import net.minecraft.world.entity.EntityEvent;
import net.minecraftforge.event.level.BlockEvent;

public interface WeaponTool {
    public void handleEntityEvent(net.minecraftforge.event.entity.living.LivingDeathEvent event);
}
