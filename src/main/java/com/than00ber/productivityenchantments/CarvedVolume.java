package com.than00ber.productivityenchantments;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.*;

public class CarvedVolume {

    private final BlockPos ORIGIN;
    private final World WORLD;
    private Set<BlockPos> VOLUME;
    private ItemStack TOOL_RESTRICTION_ITEM;
    private ToolType TOOL_RESTRICTION_TYPE;

    public CarvedVolume(Shape shape, int radius, BlockPos origin, World world) {
        this.ORIGIN = origin;
        this.WORLD = world;
        this.VOLUME = new HashSet<>();

        int yOffsetUp = shape.equals(Shape.DISC) ? origin.getY() : origin.getY() + radius;
        int yOffsetDown = shape.equals(Shape.DISC) ? origin.getY() : origin.getY() - radius;
        BlockPos p1 = new BlockPos(origin.getX() - radius, yOffsetUp, origin.getZ() - radius);
        BlockPos p2 = new BlockPos(origin.getX() + radius, yOffsetDown, origin.getZ() + radius);

        for (BlockPos pos : BlockPos.getAllInBoxMutable(p1, p2)) {
            BlockPos current = new BlockPos(pos);

            if (shape.equals(Shape.SQUARE) || origin.withinDistance(current, radius))
                this.VOLUME.add(current);
        }
    }

    public Set<BlockPos> getVolume() {
        return this.VOLUME;
    }

    public CarvedVolume setToolRestrictions(ItemStack stack, ToolType type) {
        this.TOOL_RESTRICTION_ITEM = stack;
        this.TOOL_RESTRICTION_TYPE = type;
        return this;
    }

    public CarvedVolume sortNearestToOrigin() {

        List<BlockPos> volume = new ArrayList<>(this.VOLUME);
        volume.sort(Comparator.comparingDouble(pos -> distance3DVec(pos, this.ORIGIN)));
        this.VOLUME = new HashSet<>(volume);

        return this;
    }

    public CarvedVolume filterBy(BlockState... states) {
        this.VOLUME = filter(true, this.VOLUME, this.WORLD, states);
        return this;
    }

    public CarvedVolume filterOut(BlockState... states) {
        this.VOLUME = filter(false, this.VOLUME, this.WORLD, states);
        return this;
    }

    private static Set<BlockPos> filter(boolean containsState, Set<BlockPos> volume, World world, BlockState... states) {

        List<BlockState> valid = Arrays.asList(states);
        List<BlockPos> v = new ArrayList<>(volume);
        v.removeIf(pos -> !containsState == valid.contains(world.getBlockState(pos)));

        return new HashSet<>(v);
    }

    public CarvedVolume shiftBy(int x, int y, int z) {

        Set<BlockPos> newVol = new HashSet<>();
        for (BlockPos pos : this.VOLUME)
            newVol.add(pos.add(x, y, z));
        this.VOLUME = newVol;

        return this;
    }

    /**
     * Filters through implemented validation callback.
     * Make sure to call CarvedVolume#setToolRestrictionItem and
     * CarvedVolume#setToolRestrictionType before performing filtering.
     *
     * @param callback validation callback
     * @return instance
     */
    public CarvedVolume filterViaCallback(IValidatorCallback callback) {

        if (this.TOOL_RESTRICTION_ITEM == null || this.TOOL_RESTRICTION_TYPE == null)
            throw new IllegalArgumentException("Cannot perform block filtering validation without tool restrictions set.");

        List<BlockPos> volume = new ArrayList<>(this.VOLUME);
        volume.removeIf(pos -> !callback.isBlockValid(this.WORLD.getBlockState(pos), this.WORLD, pos, this.TOOL_RESTRICTION_ITEM, this.TOOL_RESTRICTION_TYPE));
        this.VOLUME = new HashSet<>(volume);

        return this;
    }

    public CarvedVolume filterConnectedRecursively() {
        this.VOLUME = filterRecursively(this.ORIGIN, this.VOLUME, new HashSet<>());
        return this;
    }

    private Set<BlockPos> filterRecursively(BlockPos origin, Set<BlockPos> volume, Set<BlockPos> cluster) {
        cluster.add(origin);

        if (cluster.size() < volume.size() && cluster.size() < 2048) {
            Set<BlockPos> branch = new HashSet<>();

            for (Direction direction : Direction.values()) {
                BlockPos current = origin.offset(direction);
                Block block = this.WORLD.getBlockState(current).getBlock();

                if (!cluster.contains(current) && volume.contains(current) && block != Blocks.AIR)
                    branch.add(current);
            }

            branch.forEach(pos -> cluster.addAll(filterRecursively(pos, volume, cluster)));
        }

        return cluster;
    }

    @Deprecated
    public CarvedVolume filterConnectedRecursively(BlockState... states) {
        this.VOLUME = filterRecursivelyFromState(this.ORIGIN, this.VOLUME, new HashSet<>(), states);
        return this;
    }

    @Deprecated
    private Set<BlockPos> filterRecursivelyFromState(BlockPos origin, Set<BlockPos> volume, Set<BlockPos> cluster, BlockState... states) {
        cluster.add(origin);

        List<BlockState> valid = Arrays.asList(states);
        if (cluster.size() < volume.size() && cluster.size() < 2048) {
            Set<BlockPos> branch = new HashSet<>();

            for (Direction direction : Direction.values()) {
                BlockPos current = origin.offset(direction);
                BlockState state = this.WORLD.getBlockState(current);

                if (!cluster.contains(current) && volume.contains(current) && valid.contains(state))
                    branch.add(current);
            }

            branch.forEach(pos -> cluster.addAll(filterRecursivelyFromState(pos, volume, cluster, states)));
        }

        return cluster;
    }

    private static double distance3DVec(BlockPos p1, BlockPos p2) {
        double x = Math.pow(p2.getX() - p1.getX(), 2);
        double y = Math.pow(p2.getY() - p1.getY(), 2);
        double z = Math.pow(p2.getZ() - p1.getZ(), 2);
        return Math.abs(Math.sqrt(x + y + z));
    }

    public enum Shape {
        SPHERICAL, DISC, SQUARE
    }
}
