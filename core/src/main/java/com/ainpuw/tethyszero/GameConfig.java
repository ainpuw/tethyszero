package com.ainpuw.tethyszero;

import com.badlogic.gdx.utils.Array;

public class GameConfig {
    public final boolean ydown = false;
    public final float w = 32f;
    public final float h = 32f;
    public final int wallMinX = 2;
    public final int wallMaxX = 29;
    public final int wallMinY = 2;
    public final int wallMaxY = 29;

    public final float screenBgR = 100f;
    public final float screenBgG = 100f;
    public final float screenBgB = 0.2f;
    public final float screenBgA = 1f;

    public final float camStartPosX = 16f;
    public final float camStartPosY = 16f;

    public final int tileLen = 32;

    // The time interval to make one step in the game.
    public final float playerDT = 0.01f;
    public final float explosionDT = 0.01f;

    public Array<int[]> spawns = new Array<>();
    public final int nSpawns;

    // Need this number of units in a connected rectangular region to cancel.
    public int cancelThreshold = 5;

    public GameConfig() {
        // 28 spawning blocks in total.
        // O block.
        spawns.add(new int[]{15, 15, 15, 16, 16, 15, 16, 16});
        spawns.add(new int[]{15, 15, 15, 16, 16, 15, 16, 16});
        spawns.add(new int[]{15, 15, 15, 16, 16, 15, 16, 16});
        spawns.add(new int[]{15, 15, 15, 16, 16, 15, 16, 16});
        // I block.
        spawns.add(new int[]{14, 15, 15, 15, 16, 15, 17, 15});
        spawns.add(new int[]{14, 16, 15, 16, 16, 16, 17, 16});
        spawns.add(new int[]{15, 14, 15, 15, 15, 16, 15, 17});
        spawns.add(new int[]{16, 14, 16, 15, 16, 16, 16, 17});
        // T block.
        spawns.add(new int[]{15, 16, 16, 16, 16, 17, 16, 15});
        spawns.add(new int[]{15, 15, 16, 15, 17, 15, 16, 16});
        spawns.add(new int[]{15, 16, 15, 15, 15, 14, 16, 15});
        spawns.add(new int[]{14, 16, 15, 16, 16, 16, 15, 15});
        // Z block.
        spawns.add(new int[]{15, 16, 16, 16, 16, 15, 17, 15});
        spawns.add(new int[]{16, 16, 16, 15, 15, 15, 15, 14});
        spawns.add(new int[]{16, 15, 15, 15, 15, 16, 14, 16});
        spawns.add(new int[]{15, 15, 15, 16, 16, 16, 16, 17});
        // S block.
        spawns.add(new int[]{16, 16, 15, 16, 15, 15, 14, 15});
        spawns.add(new int[]{15, 16, 15, 15, 16, 15, 16, 14});
        spawns.add(new int[]{15, 15, 16, 15, 16, 16, 17, 16});
        spawns.add(new int[]{16, 15, 16, 16, 15, 16, 15, 17});
        // L block.
        spawns.add(new int[]{15, 17, 15, 16, 15, 15, 16, 15});
        spawns.add(new int[]{14, 15, 15, 15, 16, 15, 16, 16});
        spawns.add(new int[]{16, 14, 16, 15, 16, 16, 15, 16});
        spawns.add(new int[]{17, 16, 16, 16, 15, 16, 15, 15});
        // J block.
        spawns.add(new int[]{16, 17, 16, 16, 16, 15, 15, 15});
        spawns.add(new int[]{17, 15, 16, 15, 15, 15, 15, 16});
        spawns.add(new int[]{15, 14, 15, 15, 15, 16, 16, 16});
        spawns.add(new int[]{14, 16, 15, 16, 16, 16, 16, 15});

        nSpawns = spawns.size;
    }
}
