package com.company;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.Arrays;

public class Map {
    private static final int NOTDEFINED=-1;
    private static final int LAND = 0;
    private static final int WATER=1;
    static final String ANSI_RESET = "\u001B[0m";

    private int[][] cells;

    private Map(int[][] cells) {
        this.cells = cells;
    }


    public void printmap() {
        int width = cells.length;
        int height = cells[0].length;
        for (int i = 0; i < height; ++i) {
            if ((i & 1) == 1) System.out.print(" ");
            for (int j = 0; j < width; ++j) {
                if(cells[i][j]==0) System.out.print("\u001B[32m@ \u001B[0m");
                else System.out.print("\u001b[34m@ \u001B[0m");
            }
            System.out.println();
        }
    }


    public static Map createMap(int height, int width, int mode) {
        assert height >= 1 && width >= 1;
        int landcnt = 0;
        Map map = new Map(new int[height][width]);
        while ((double) landcnt / (width * height) < 0.2 || (double) landcnt / (height * width) > 0.5) {
            map.initMap(height, width);
            if (mode == 1) {
                for (int i = 0; i < 7; ++i) {
                    map.removeWater();
                }
            }
            landcnt = map.cntCell(LAND);
        }
        if (mode == 0) {
            for (int i = 0; i < 7; ++i) {
                map.removeWater();
            }
        }
        return map;
    }
    static AbstractMap.SimpleEntry<Integer,Integer> pair(int first, int second){
        return new AbstractMap.SimpleEntry<>(first, second);
    }

    private void initMap(int height, int width) {
        cells=new int[height][width];
        for(int[] row:cells){
            Arrays.fill(row,NOTDEFINED);
        }
        double randomCoef = 1.999;

        ArrayDeque<AbstractMap.SimpleEntry<Integer, Integer>> q = new ArrayDeque<>();
        q.addFirst(pair(height / 2, width / 2));

        while (!q.isEmpty()) {
            AbstractMap.SimpleEntry<Integer, Integer> p = q.removeLast();
            int x = p.getKey();
            int y = p.getValue();
            if (x >= height || x < 0) continue;
            if (y >= width || y < 0) continue;
            if (cells[x][y] == NOTDEFINED) {
                if (x == width / 2 && y == height / 2) {
                    cells[x][y] = LAND;
                } else {
                    int cell = (int) (Math.random() * randomCoef);
                    if (cell == 0) {
                        cells[x][y] = LAND;
                    } else cells[x][y] = WATER;
                }
            } else continue;
            if (cells[x][y]==WATER) continue;
            q.addFirst(pair(x - 1, y));
            q.addFirst(pair(x + 1, y));
            q.addFirst(pair(x, y - 1));
            q.addFirst(pair(x, y + 1));
            if ((x & 1) == 1) {
                q.addFirst(pair(x - 1, y + 1));
                q.addFirst(pair(x + 1, y + 1));
            } else {
                q.addFirst(pair(x - 1, y - 1));
                q.addFirst(pair(x + 1, y - 1));
            }
        }
        changeCells(NOTDEFINED, WATER);
    }



    private void changeCells(int cellTypeOld, int cellTypeNew) {
        for (int[] row : cells) {
            for (int i = 0; i < row.length; ++i) {
                if (row[i] == cellTypeOld)
                    row[i] = cellTypeNew;
            }
        }
    }

    private int cntCell(int cellType) {
        int res = 0;
        for (int[] row : cells) {
            for (int cell : row) {
                if (cell == cellType) ++res;
            }
        }
        return res;
    }

    private void removeWater() {
        int[][] neighbours_map = neighboursMap(WATER);
        for (int i = 0; i < neighbours_map.length; ++i) {
            for (int j = 0; j < neighbours_map[0].length; ++j) {
                if (cells[i][j] == WATER && neighbours_map[i][j] < 3) {
                    cells[i][j] = LAND;
                }
            }
        }
    }


    private int[][] neighboursMap(int cellType) {
        int[][] neighboursMap = new int[cells.length][cells[0].length];
        for (int i = 0; i < neighboursMap.length; ++i) {
            for (int j = 0; j < neighboursMap[0].length; ++j) {
                neighboursMap[i][j] = cntNeighboursWater(i, j);
            }
        }
        return neighboursMap;
    }

    private int cntNeighboursWater(int x, int y) {
        int res = 0;
        if (safeAccess(x - 1, y) == WATER) ++res;
        if (safeAccess(x + 1, y) == WATER) ++res;
        if (safeAccess(x, y + 1) == WATER) ++res;
        if (safeAccess(x, y - 1) == WATER) ++res;
        if ((x & 1) == 1) {
            if ( safeAccess(x - 1, y + 1) == WATER) ++res;
            if ( safeAccess(x + 1, y + 1) == WATER) ++res;
        } else {
            if (safeAccess(x - 1, y - 1) == WATER) ++res;
            if (safeAccess(x + 1, y - 1) == WATER) ++res;
        }
        return res;
    }

    private int safeAccess(int x, int y) {
        try {
            if (cells[x][y] == NOTDEFINED) return WATER;
            return cells[x][y];
        } catch (IndexOutOfBoundsException e) {
            return LAND;
        }
    }
}

