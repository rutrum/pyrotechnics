package net.rutrum.pyrotechnics;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireworkAssemblyTableBlock extends Block implements BlockEntityProvider {

    public FireworkAssemblyTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {

        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        // Get entity of location, could be null
        BlockEntity entity = world.getBlockEntity(pos);

        // Create a factory (constant function) that creates a new firework assembly table entity
        NamedScreenHandlerFactory factory = ((FireworkAssemblyTableBlockEntity) entity);

        // Block position could be null, technically
        if (factory != null) {
            player.openHandledScreen(factory);
        }

        return ActionResult.CONSUME;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FireworkAssemblyTableBlockEntity(pos, state);
    }
}