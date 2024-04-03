package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.ICoverable;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.Cover;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.refinedmods.refinedstorage.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.joml.Vector3f;
import javax.annotation.Nullable;

public class WrenchItem extends Item {
    public WrenchItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Nullable
    private static Direction getDirectionFromClickLocation(Vector3f location, @Nullable BlockEntity entity) {
        if (entity == null) {
            return null;
        }
        BlockPos pos = entity.getBlockPos();
        Vector3f centered = new Vector3f((float)pos.getX() + 0.5f, (float)pos.getY() + 0.5f, (float)pos.getZ() + 0.5f);
        Vector3f subbed = location.sub(centered);
        Vector3f normalized = subbed.normalize();
        return Direction.getNearest(normalized.x, normalized.y, normalized.z);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        if (ctx.getLevel().isClientSide) {
            return InteractionResult.CONSUME;
        }

        INetworkNode node = NetworkUtils.getNodeFromBlockEntity(ctx.getLevel().getBlockEntity(ctx.getClickedPos()));
        INetwork network = NetworkUtils.getNetworkFromNode(node);
        if (network != null && !network.getSecurityManager().hasPermission(Permission.BUILD, ctx.getPlayer())) {
            LevelUtils.sendNoPermissionMessage(ctx.getPlayer());

            return InteractionResult.FAIL;
        }
        BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos());

        if (node instanceof ICoverable) {
            Vector3f location = ctx.getClickLocation().toVector3f();
            BlockEntity entity = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
            Direction clickedSide =  getDirectionFromClickLocation(location, entity);
            if (((ICoverable) node).getCoverManager().hasCover(clickedSide)) {
                Cover cover = ((ICoverable) node).getCoverManager().removeCover(clickedSide);
                if (cover != null) {
                    ItemStack stack1 = cover.getType().createStack();
                    CoverItem.setItem(stack1, cover.getStack());
                    ItemHandlerHelper.giveItemToPlayer(ctx.getPlayer(), stack1);
                    ctx.getLevel().sendBlockUpdated(ctx.getClickedPos(), state, state, 3);
                    ctx.getLevel().updateNeighborsAt(ctx.getClickedPos(), ctx.getLevel().getBlockState(ctx.getClickedPos()).getBlock());
                    return InteractionResult.SUCCESS;
                }
            }
        }

        ctx.getLevel().setBlockAndUpdate(ctx.getClickedPos(), state.rotate(ctx.getLevel(), ctx.getClickedPos(), Rotation.CLOCKWISE_90));

        return InteractionResult.CONSUME;
    }
}
