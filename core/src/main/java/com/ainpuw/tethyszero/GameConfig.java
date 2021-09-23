package com.ainpuw.tethyszero;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class GameConfig {
    public final boolean ydown = false;
    public final float w = 32f;
    public final float h = 32f;
    public final int wallMinX = 2;
    public final int wallMaxX = 29;
    public final int wallMinY = 2;
    public final int wallMaxY = 29;

    public final float screenBgR = 255f / 255f;
    public final float screenBgG = 135f / 255f;
    public final float screenBgB = 157f / 255f;
    public final float screenBgA = 1f;
    public final float gameScreenBgR = 250f / 255f;
    public final float gameScreenBgG = 224f / 255f;
    public final float gameScreenBgB = 227f / 255f;

    public final float camStartPosX = 16f;
    public final float camStartPosY = 16f;

    public final int tileLen = 32;
    public final int clearTileId = 3;

    // The time interval to make one step in the game.
    public final float playerDT = 0.05f;
    public final float explosionDT = 0.1f;
    public final int waitAfterExplosion = 5;
    public final int gameoverWait = 80;

    public Array<int[]> spawns = new Array<>();
    public final int nSpawns;

    // Need this number of units in a connected rectangular region to cancel.
    public final int cancelThreshold = 60;

    public final float tetrazeroAnimationSpeed = 0.4f;
    public final float clicktostartAnimationSpeed = 0.4f;
    public final float destroyAnimationSpeed = 0.2f;

    public GameConfig() {
        // 28 spawning blocks in total.
        // O block.
        spawns.add(new int[]{15, 15, 15, 16, 16, 15, 16, 16});
        spawns.add(new int[]{15, 15, 15, 16, 16, 15, 16, 16});
        // spawns.add(new int[]{15, 15, 15, 16, 16, 15, 16, 16});
        // spawns.add(new int[]{15, 15, 15, 16, 16, 15, 16, 16});

        // I block.
        spawns.add(new int[]{14, 15, 15, 15, 16, 15, 17, 15});
        spawns.add(new int[]{14, 16, 15, 16, 16, 16, 17, 16});
        // spawns.add(new int[]{15, 14, 15, 15, 15, 16, 15, 17});
        // spawns.add(new int[]{16, 14, 16, 15, 16, 16, 16, 17});

        // T block.
        // spawns.add(new int[]{15, 16, 16, 16, 16, 17, 16, 15});
        spawns.add(new int[]{15, 15, 16, 15, 17, 15, 16, 16});
        // spawns.add(new int[]{15, 16, 15, 15, 15, 14, 16, 15});
        spawns.add(new int[]{14, 16, 15, 16, 16, 16, 15, 15});

        // Z block.
        spawns.add(new int[]{15, 16, 16, 16, 16, 15, 17, 15});
        // spawns.add(new int[]{16, 16, 16, 15, 15, 15, 15, 14});
        spawns.add(new int[]{16, 15, 15, 15, 15, 16, 14, 16});
        // spawns.add(new int[]{15, 15, 15, 16, 16, 16, 16, 17});

        // S block.
        spawns.add(new int[]{16, 16, 15, 16, 15, 15, 14, 15});
        // spawns.add(new int[]{15, 16, 15, 15, 16, 15, 16, 14});
        spawns.add(new int[]{15, 15, 16, 15, 16, 16, 17, 16});
        // spawns.add(new int[]{16, 15, 16, 16, 15, 16, 15, 17});

        // L block.
        // spawns.add(new int[]{15, 17, 15, 16, 15, 15, 16, 15});
        spawns.add(new int[]{14, 15, 15, 15, 16, 15, 16, 16});
        // spawns.add(new int[]{16, 14, 16, 15, 16, 16, 15, 16});
        spawns.add(new int[]{17, 16, 16, 16, 15, 16, 15, 15});

        // J block.
        // spawns.add(new int[]{16, 17, 16, 16, 16, 15, 15, 15});
        spawns.add(new int[]{17, 15, 16, 15, 15, 15, 15, 16});
        // spawns.add(new int[]{15, 14, 15, 15, 15, 16, 16, 16});
        spawns.add(new int[]{14, 16, 15, 16, 16, 16, 16, 15});

        nSpawns = spawns.size;
    }

    // 0s and 1s mean whether the side is closed.
    // The order is left side, top side, right side, bottom side.
    public final static HashMap<String, Integer> tileHashMap = new HashMap<String, Integer>() {{
        put("1111", 11);
        put("1110", 21);
        put("0111", 31);
        put("1011", 41);
        put("1101", 51);
        put("0110", 61);
        put("0011", 71);
        put("1100", 81);
        put("1001", 91);
        put("1000", 101);
        put("0001", 111);
        put("0010", 121);
        put("0100", 131);
        put("0101", 141);
        put("1010", 151);
    }};

    public final static HashMap<Power, Integer> specialTileHashMap = new HashMap<Power, Integer>() {{
        put(Power.T, 9);
        put(Power.Z, 29);
        put(Power.E, 49);
        put(Power.R, 69);
        put(Power.O, 89);
    }};

    public final int destroyTileId = 161;

}
