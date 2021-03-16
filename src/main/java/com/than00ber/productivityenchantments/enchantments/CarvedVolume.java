package com.than00ber.productivityenchantments.enchantments;

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

    public CarvedVolume filterByBlock(Block... blocks) {
        List<Block> filter = Arrays.asList(blocks);
        this.VOLUME.removeIf(pos -> !filter.contains(this.WORLD.getBlockState(pos).getBlock()));
        return this;
    }

    public CarvedVolume shiftBy(int x, int y, int z) {

        Set<BlockPos> newVol = new HashSet<>();
        for (BlockPos pos : this.VOLUME)
            newVol.add(pos.add(x, y, z));
        this.VOLUME = newVol;

        return this;
    }

    public CarvedVolume rotate(double radians, Direction facing) {

        // x = y * cos( A ) - z * sin( A )

        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        Set<BlockPos> volume = new HashSet<>();
        for (BlockPos pos : this.VOLUME) {
            int x = pos.getX();
            int y = (int) (pos.getY() * cos - pos.getZ() * sin);
            int z = (int) (pos.getZ() * cos + pos.getY() * sin);
            volume.add(new BlockPos(x, y, z));
        }
        this.VOLUME = volume;

        return this;
    }

    /**
     * Filters through implemented validation callback.
     * Make sure to call CarvedVolume#setToolRestrictions
     * before performing filtering.
     *
     * @param callback validation callback
     * @return instance
     */
    public CarvedVolume filterViaCallback(IValidatorCallback callback) {

        if (this.TOOL_RESTRICTION_ITEM == null || this.TOOL_RESTRICTION_TYPE == null)
            throw new IllegalArgumentException("Cannot perform block filtering validation without tool restrictions set.");

        List<BlockPos> volume = new ArrayList<>(this.VOLUME);
        volume.removeIf(pos -> !callback.isBlockValid(this.WORLD.getBlockState(pos), this.WORLD, pos, this.TOOL_RESTRICTION_ITEM, this.TOOL_RESTRICTION_TYPE, null));
        this.VOLUME = new HashSet<>(volume);

        return this;
    }

    public CarvedVolume filterConnectedRecursively() {
        this.VOLUME = filterRecursively(true, this.ORIGIN, this.VOLUME, new HashSet<>());
        return this;
    }

    public CarvedVolume filterConnectedRecursively(boolean closestNeighbor) {
        this.VOLUME = filterRecursively(closestNeighbor, this.ORIGIN, this.VOLUME, new HashSet<>());
        return this;
    }

    private Set<BlockPos> filterRecursively(boolean closestNeighbor, BlockPos origin, Set<BlockPos> volume, Set<BlockPos> cluster) {
        cluster.add(origin);

        if (cluster.size() < volume.size() && cluster.size() < 2048) {
            Set<BlockPos> branch = new HashSet<>();

            if (closestNeighbor) {

                // only check for nearest neighboring blockpos
                for (Direction direction : Direction.values()) {
                    BlockPos current = origin.offset(direction);
                    Block block = this.WORLD.getBlockState(current).getBlock();

                    if (!cluster.contains(current) && volume.contains(current) && block != Blocks.AIR)
                        branch.add(current);
                }
            }
            else {

                // check for all surrounding blockpos
                for (int x = -1; x <= 1; x++) {

                    for (int y = -1; y <= 1; y++) {

                        for (int z = -1; z <= 1; z++) {
                            BlockPos current = origin.add(x, y, z);
                            Block block = this.WORLD.getBlockState(current).getBlock();

                            if (!cluster.contains(current) && volume.contains(current) && block != Blocks.AIR)
                                branch.add(current);
                        }
                    }
                }
            }

            branch.forEach(pos -> cluster.addAll(filterRecursively(closestNeighbor, pos, volume, cluster)));
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
