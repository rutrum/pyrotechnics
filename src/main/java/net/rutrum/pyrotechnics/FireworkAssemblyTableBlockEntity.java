package net.rutrum.pyrotechnics;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class FireworkAssemblyTableBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {

    public FireworkAssemblyTableBlockEntity(BlockPos pos, BlockState state) {
        super(Pyrotechnics.FIREWORK_ASSEMBLY_TABLE_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new FireworkAssemblyTableScreenHandler(syncId, inventory, this);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.pyrotechnics.firework_assembly_table");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }
}
