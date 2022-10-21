package net.rutrum.pyrotechnics;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicate.Extended;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireworkAssemblyTableBlock extends Block implements BlockEntityProvider {
    private static final Text TITLE = Text.translatable("firework_assembly_table");

    public FireworkAssemblyTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {

        if (!world.isClient) {
            // Get entity of location, could be null
            BlockEntity entity = world.getBlockEntity(pos);

            // Create a factory (constant function) that creates a new firework assembly table entity
            NamedScreenHandlerFactory factory = ((FireworkAssemblyTableBlockEntity) entity);

            // Block position could be null, technically
            if (factory != null) {
                player.openHandledScreen(factory);
            }
        }

        //player.openHandledScreen(new ExtendedScreenHandlerFactory(
        //    (syncId, inventory, p) -> new FireworkAssemblyTableScreenHandler(syncId, inventory, entity, ScreenHandlerContext.EMPTY), TITLE));

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FireworkAssemblyTableBlockEntity(pos, state);
    }
}