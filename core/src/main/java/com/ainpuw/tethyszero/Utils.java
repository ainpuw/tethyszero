package com.ainpuw.tethyszero;

public class Utils {
    public static int rand(int min, int max) {
        int random_number = (int) ((Math.random() * (max + 1 - min)) + min);
        return Math.min(max, random_number);  // In case random_number hits max + 1.
    }

    public static Speed randSpeed() {
        int r = rand(0, 7);
        if (r == 0)
            return Speed.UP;
        else if (r == 1)
            return Speed.DOWN;
        else if (r == 2)
            return Speed.LEFT;
        else if (r == 3)
            return Speed.RIGHT;
        else if (r == 4)
            return Speed.UPLEFT;
        else if (r == 5)
            return Speed.UPRIGHT;
        else if (r == 6)
            return Speed.DOWNLEFT;
        else
            return Speed.DOWNRIGHT;
    }

    public static Power randPower() {
        int r = rand(0, 4);
        if (r == 0)
            return Power.T;
        else if (r == 1)
            return Power.Z;
        else if (r == 2)
            return Power.E;
        else if (r == 3)
            return Power.R;
        else
            return Power.O;
    }
    public static int randTile() {
        return rand(11, 17);
    }
}
