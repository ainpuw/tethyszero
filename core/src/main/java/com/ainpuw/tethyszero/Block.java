package com.ainpuw.tethyszero;

enum Speed {UP, DOWN, LEFT, RIGHT, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT, ZERO};
enum Power {T, Z, E, R, O};

public class Block {
    public int [] shape;
    public int n;
    public Speed speed;
    public Power power;
    public int tileId;
    public boolean moved = false;

    public Block(int [] shape, Speed speed, Power power, int tileId) {
        this.shape = new int[shape.length];
        for (int i = 0; i < shape.length; i++)
            this.shape[i] = shape[i];

        this.n = shape.length / 2;
        this.speed = speed;
        this.power = power;
        this.tileId = tileId;
    }

    public void step() {
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

    public void stepBack() {
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
}
