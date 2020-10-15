package aztech.modern_industrialization.pipes.impl;

import aztech.modern_industrialization.pipes.api.PipeEndpointType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.util.math.Direction.*;

/**
 * A class that can build pipe model parts using a simple interface.
 */
abstract class PipePartBuilder {
    /**
     * The width of a pipe.
     */
    static final float SIDE = 2.0f / 16;
    /**
     * The spacing between two pipes.
     */
    protected static final float SPACING = 1.0f / 16;
    /**
     * The distance between the side of the block and the first of the five pipe slots.
     */
    protected static final float FIRST_POS = (1.0f - 5 * SIDE - 4 * SPACING) / 2;
    Vec3d pos;
    Vec3d facing;
    Vec3d right;

    PipePartBuilder(int slotPos, Direction direction) {
        this.facing = Vec3d.of(direction.getVector());
        // initial position + half pipe + slotPos * width
        float position = (1.0f - 3 * SIDE - 2 * SPACING) / 2.0f + SIDE / 2.0f + slotPos * (SIDE + SPACING);
        this.pos = new Vec3d(position, position, position);
        // Find a suitable right direction (both right and up must face inside of the block).
        for (Direction d : Direction.values()) {
            this.right = Vec3d.of(d.getVector());
            if (isTowardsInside(this.right) && isTowardsInside(up())) break;
        }
        // Move out of the center cube.
        moveForward(SIDE / 2);
    }

    /**
     * Find out whether the axis direction is far from the sides of the block.
     */
    protected boolean isTowardsInside(Vec3d direction) {
        return distanceToSide(direction) > 0.5f - 1e-6;
    }

    /**
     * Get the distance along some axis direction to the nearest block side.
     */
    protected float distanceToSide(Vec3d direction) {
        float p = (float) direction.dotProduct(pos);
        if (p > 0) {
            return 1 - p;
        } else {
            return -p;
        }
    }

    /**
     * Draw a 5-sided pipe.
     */
    protected final void drawPipe(float length, Intent intent) {
        drawPipe(length, intent, true);
    }

    /**
     * Draw a pipe.
     */
    abstract void drawPipe(float length, Intent intent, boolean end);

    /**
     * Move forward.
     */
    void moveForward(float amount) {
        this.pos = this.pos.add(this.facing.multiply(amount));
    }

    /**
     * Get up vector.
     */
    Vec3d up() {
        return right.crossProduct(facing);
    }

    /**
     * Rotate clockwise around the facing axis.
     */
    protected void rotateCw() {
        right = up().multiply(-1);
    }

    /**
     * Turn 90° up.
     */
    protected void turnUp() {
        facing = up();
    }

    /**
     * Draw a straight line.
     */
    void straightLine(boolean reduced, boolean end) {
        if(reduced) moveForward(SIDE + SPACING);
        drawPipe(distanceToSide(facing), Intent.STRAIGHT, end);
    }

    /**
     * Draw a short bend.
     */
    void shortBend(boolean reduced, boolean end) {
        if(reduced) moveForward(SIDE + SPACING);
        // horizontal
        float dist = FIRST_POS + 2 * SIDE + SPACING;
        float advDist = distanceToSide(facing) - dist;
        boolean bendConflicting = advDist + SIDE < 0;
        drawPipe(advDist + SIDE, Intent.BEND);
        moveForward(advDist + SIDE / 2);
        turnUp();
        rotateCw();
        // vertical
        moveForward(SIDE / 2);
        drawPipe(SPACING + SIDE, bendConflicting ? Intent.BEND_CONFLICTING : Intent.BEND, !bendConflicting);
        moveForward(SPACING + SIDE / 2);
        turnUp();
        rotateCw();
        // perpendicular
        moveForward(SIDE / 2);
        drawPipe(SPACING + SIDE, Intent.BEND);
        moveForward(SPACING + SIDE / 2);
        turnUp();
        // again vertical
        moveForward(SIDE / 2);
        drawPipe(distanceToSide(facing), Intent.STRAIGHT, end);
    }

    /**
     * Draw a short bend, on the extra slot.
     */
    void farShortBend(boolean reduced, boolean end) {
        if(reduced) moveForward(SIDE + SPACING);
        // horizontal
        float dist = FIRST_POS + SIDE;
        float advDist = distanceToSide(facing) - dist;
        drawPipe(advDist + SIDE, Intent.BEND);
        moveForward(advDist + SIDE / 2);
        turnUp();
        rotateCw();
        // vertical
        moveForward(SIDE / 2);
        drawPipe(SPACING + SIDE, Intent.BEND);
        moveForward(SPACING + SIDE / 2);
        turnUp();
        rotateCw();
        // perpendicular
        moveForward(SIDE / 2);
        drawPipe(SPACING + SIDE, Intent.BEND);
        moveForward(SPACING + SIDE / 2);
        turnUp();
        // again vertical
        moveForward(SIDE / 2);
        drawPipe(distanceToSide(facing), Intent.STRAIGHT, end);
    }

    /**
     * Draw a long bend.
     */
    void longBend(boolean reduced, boolean end) {
        if(reduced) moveForward(SIDE + SPACING);
        // horizontal
        float dist = FIRST_POS + SIDE;
        float advDist = distanceToSide(facing) - dist;
        drawPipe(advDist + SIDE, Intent.BEND);
        moveForward(advDist + SIDE / 2);
        turnUp();
        rotateCw();
        // vertical
        moveForward(SIDE / 2);
        drawPipe(2 * SPACING + 2 * SIDE, Intent.BEND);
        moveForward(2 * SPACING + 1.5f * SIDE);
        turnUp();
        rotateCw();
        // perpendicular
        moveForward(SIDE / 2);
        drawPipe(2 * SPACING + 2 * SIDE, Intent.BEND);
        moveForward(2 * SPACING + 1.5f * SIDE);
        turnUp();
        // again vertical
        moveForward(SIDE / 2);
        drawPipe(distanceToSide(facing), Intent.STRAIGHT, end);
    }

    public static int getSlotPos(int slot) {
        return slot == 0 ? 1 : slot == 1 ? 0 : 2;
    }

    /**
     * Get the type of a connection.
     */
    static int getRenderType(int logicalSlot, Direction direction, PipeEndpointType[][] connections) {
        if (connections[logicalSlot][direction.getId()] == null) {
            // no connection
            return 0;
        } else if (connections[logicalSlot][direction.getId()] != PipeEndpointType.PIPE) {
            // straight line when connecting to a block
            return 1;
        } else {
            int connSlot = 0;
            for (int i = 0; i < logicalSlot; i++) {
                if (connections[i][direction.getId()] != null) {
                    connSlot++;
                }
            }
            if (logicalSlot == 1) {
                // short bend
                if (connSlot == 0) {
                    return 2;
                }
            } else if (logicalSlot == 2) {
                if (connSlot == 0) {
                    // short bend, but far if the direction is west to avoid collisions in some cases.
                    return direction == WEST ? 3 : 2;
                } else if (connSlot == 1) {
                    // long bend
                    return 4;
                }
            }
            // default to straight line
            return 1;
        }
    }

    /**
     * Get the initial direction of a connection.
     */
    static Direction getInitialDirection(int logicalSlot, Direction connectionDirection, int renderType) {
        if (renderType == 2) { // only for short bend
            if (logicalSlot == 1) {
                if(connectionDirection == NORTH) return UP;
                if(connectionDirection == WEST) return SOUTH;
                if(connectionDirection == DOWN) return EAST;
            } else if(logicalSlot == 2) {
                if(connectionDirection == UP) return NORTH;
                if(connectionDirection == SOUTH) return WEST;
                if(connectionDirection == EAST) return DOWN;
            }
        }
        return connectionDirection;
    }

    /**
     * Indicates why a particular pipe kind is being used
     */
    public enum Intent {
        STRAIGHT,
        BEND,
        BEND_CONFLICTING,
    }
}
