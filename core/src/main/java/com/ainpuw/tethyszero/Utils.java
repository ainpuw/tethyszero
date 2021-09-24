package com.ainpuw.tethyszero;

import java.util.Stack;

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
        int r1 = rand(0, 9);
        if (r1 <= 7)
            return Power.T;
        else {
            int r = rand(1, 4);
            if (r == 1)
                return Power.Z;
            else if (r == 2)
                return Power.E;
            else if (r == 3)
                return Power.R;
            else
                return Power.O;
        }
    }

    public static Power randNonTPower() {
        int r = rand(1, 4);
        if (r == 1)
            return Power.Z;
        else if (r == 2)
            return Power.E;
        else if (r == 3)
            return Power.R;
        else
            return Power.O;
    }

    public static int [] maxRectangle(int[][] matrix) {
        int maxArea = 0;
        int lowerLeftX = 0;
        int lowerLeftY = 0;
        int upperRightX = 0;
        int upperRightY = 0;

        int[] height = new int[matrix[0].length];
        for (int i = 0; i < matrix[0].length; i++){
            if (matrix[0][i] == 1)
                height[i] = 1;
        }
        int [] result = maximalRectangleLargestInLine(height);
        if (result[0] > maxArea) {
            maxArea = result[0];
            lowerLeftY = result[2];
            upperRightY = result[3];
        }

        for (int i = 1; i < matrix.length; i++){
            maximalRectangleResetHeight(matrix, height, i);
            result = maximalRectangleLargestInLine(height);
            if (result[0] > maxArea) {
                maxArea = result[0];
                lowerLeftX = i - result[1] + 1;  // Minus block height plus 1.
                lowerLeftY = result[2];
                upperRightX = i;
                upperRightY = result[3];
            }
        }

        return new int[]{maxArea, lowerLeftX, lowerLeftY, upperRightX, upperRightY};
    }

    private static void maximalRectangleResetHeight(int[][] matrix, int[] height, int idx){
        for (int i = 0; i < matrix[0].length; i++){
            if (matrix[idx][i] == 1) height[i] += 1;
            else height[i] = 0;
        }
    }

    private static int [] maximalRectangleLargestInLine(int[] height) {
        if (height == null || height.length == 0)
            return new int[]{0, 0, 0, 0};

        int len = height.length;
        Stack<Integer> s = new Stack<Integer>();
        int maxArea = 0;
        int dx = 0;
        int ly = 0;
        int ry = 0;
        for (int i = 0; i <= len; i++) {
            int h = (i == len ? 0 : height[i]);
            if (s.isEmpty() || h >= height[s.peek()]) {
                s.push(i);
            } else {
                int tp = s.pop();
                int newArea = height[tp] * (s.isEmpty() ? i : i - 1 - s.peek());
                if (newArea > maxArea) {
                    maxArea = newArea;
                    dx = height[tp];
                    if (s.isEmpty()) {
                        ly = 0;
                        ry = i - 1;
                    } else {
                        ly = s.peek() + 1;
                        ry = i - 1;
                    }
                }
                i--;
            }
        }
        return new int[]{maxArea, dx, ly, ry};
    }
}
