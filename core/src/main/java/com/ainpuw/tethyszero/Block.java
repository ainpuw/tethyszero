package com.ainpuw.tethyszero;

enum Speed {UP, DOWN, LEFT, RIGHT, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT, ZERO};
enum Power {T, Z, E, R, O};

public class Block {
    public int [] shape;
    public int n;
    public boolean centerSymmetric;
    public Speed speed;
    public int rotation = 0;  // 0 = no rotation, -1 = left, +1 = right.
    public int [] shapeBeforeRotation;
    public Power power;
    public int tileId;
    public boolean moved = false;
    public int remainingActions = 30;

    public Block(int [] shape, Speed speed, Power power, int tileId) {
        this.shape = new int[shape.length];
        this.shapeBeforeRotation = new int[shape.length];
        for (int i = 0; i < shape.length; i++) {
            this.shape[i] = shape[i];
            this.shapeBeforeRotation[i] = shape[i];
        }
        this.n = shape.length / 2;
        this.centerSymmetric = isCenterSymmetric();

        this.speed = speed;
        this.power = power;
        this.tileId = tileId;
    }

    public void move() {
        switch (speed) {
            // Horizontal and vertical.
            case UP:
                for (int i = 0; i < n; i++)
                    shape[i * 2 + 1] += 1;
                break;
            case DOWN:
                for (int i = 0; i < n; i++)
                    shape[i * 2 + 1] -= 1;
                break;
            case LEFT:
                for (int i = 0; i < n; i++)
                    shape[i * 2] -= 1;
                break;
            case RIGHT:
                for (int i = 0; i < n; i++)
                    shape[i * 2] += 1;
                break;
            // Diagonal.
            case UPLEFT:
                for (int i = 0; i < n; i++) {
                    shape[i * 2] -= 1;
                    shape[i * 2 + 1] += 1;
                }
                break;
            case UPRIGHT:
                for (int i = 0; i < n; i++) {
                    shape[i * 2] += 1;
                    shape[i * 2 + 1] += 1;
                }
                break;
            case DOWNLEFT:
                for (int i = 0; i < n; i++) {
                    shape[i * 2] -= 1;
                    shape[i * 2 + 1] -= 1;
                }
                break;
            case DOWNRIGHT:
                for (int i = 0; i < n; i++) {
                    shape[i * 2] += 1;
                    shape[i * 2 + 1] -= 1;
                }
                break;
            // No movement.
            case ZERO:
                break;
        }
    }

    public void moveBack() {
        switch (speed) {
            // Horizontal and vertical.
            case UP:
                for (int i = 0; i < n; i++)
                    shape[i * 2 + 1] -= 1;
                break;
            case DOWN:
                for (int i = 0; i < n; i++)
                    shape[i * 2 + 1] += 1;
                break;
            case LEFT:
                for (int i = 0; i < n; i++)
                    shape[i * 2] += 1;
                break;
            case RIGHT:
                for (int i = 0; i < n; i++)
                    shape[i * 2] -= 1;
                break;
            // Diagonal.
            case UPLEFT:
                for (int i = 0; i < n; i++) {
                    shape[i * 2] += 1;
                    shape[i * 2 + 1] -= 1;
                }
                break;
            case UPRIGHT:
                for (int i = 0; i < n; i++) {
                    shape[i * 2] -= 1;
                    shape[i * 2 + 1] -= 1;
                }
                break;
            case DOWNLEFT:
                for (int i = 0; i < n; i++) {
                    shape[i * 2] += 1;
                    shape[i * 2 + 1] += 1;
                }
                break;
            case DOWNRIGHT:
                for (int i = 0; i < n; i++) {
                    shape[i * 2] -= 1;
                    shape[i * 2 + 1] += 1;
                }
                break;
            // No movement.
            case ZERO:
                break;
        }
    }

    public void rotate() {
        shapeBeforeRotation = new int[shape.length];
        for (int i = 0; i < shape.length; i++)
            shapeBeforeRotation[i] = shape[i];

        if (rotation == 0 || centerSymmetric)
            return;

        // Find center.
        int[] center = getCenter();
        int cx = center[0];
        int cy = center[1];

        // Rotate around center.
        for (int i = 0; i < n; i++) {
            int x = shape[i * 2];
            int y = shape[i * 2 + 1];
            int dx = x - cx;
            int dy = y - cy;
            if (rotation == 1) {
                // (-0.9, 0.1) -> (0.1, 0.9).
                x = cx + dy;
                y = cy - dx;
            }
            else if (rotation == -1) {
                // (0.1, 0.9) -> (-0.9, 0.1).
                x = cx - dy;
                y = cy + dx;
            }
            shape[i * 2] = x;
            shape[i * 2 + 1] = y;
        }

        rotation = 0;
    }

    public void rotateBack() {
        shape = shapeBeforeRotation;
    }
    public boolean overlap(Block b) {
        // This is a brute force algorithm.
        // But each block can have up to 4 units, there are at most 16 computations.
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < b.n; j++) {
                if (shape[2 * i] == b.shape[2 * j] && shape[2 * i + 1] == b.shape[2 * j + 1])
                    return true;
            }
        }
        return false;
    }

    public boolean overlap(int minx, int maxx, int miny, int maxy) {
        for (int i = 0; i < n; i++) {
            if (shape[2 * i] <= minx || shape[2 * i] >= maxx)
                return true;
            if (shape[2 * i + 1] <= miny || shape[2 * i + 1] >= maxy)
                return true;
        }
        return false;
    }

    public int [] getCenter() {
        // Get center.
        int minx = Integer.MAX_VALUE;
        int maxx = Integer.MIN_VALUE;
        int miny = Integer.MAX_VALUE;
        int maxy = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            minx = Math.min(shape[i * 2], minx);
            maxx = Math.max(shape[i * 2], maxx);
            miny = Math.min(shape[i * 2 + 1], miny);
            maxy = Math.max(shape[i * 2 + 1], maxy);
        }
        int cx = (maxx - minx) / 2 + minx;
        int cy = (maxy - miny) / 2 + miny;
        int [] center = new int[]{cx, cy};

        return center;
    }

    public boolean isCenterSymmetric() {
        if (n <= 1)
            return true;
        else if (n == 2 || n == 3)
            return false;
        else if (n > 5)
            ;  // This should never happen.

        int minx = Integer.MAX_VALUE;
        int maxx = Integer.MIN_VALUE;
        int miny = Integer.MAX_VALUE;
        int maxy = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            minx = Math.min(shape[i * 2], minx);
            maxx = Math.max(shape[i * 2], maxx);
            miny = Math.min(shape[i * 2 + 1], miny);
            maxy = Math.max(shape[i * 2 + 1], maxy);
        }

        int check = 0;
        for (int i = 0; i < n; i++) {
            if ((shape[i * 2] == minx || shape[i * 2] == maxx) &&
                (shape[i * 2 + 1] == miny || shape[i * 2 + 1] == maxy))
                check++;
        }
        return check == 4;
    }

}
