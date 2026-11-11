package com.uenkerlabs.arcade;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Minimal dependency-free 3x3 sliding puzzle, always scrambled by legal moves. */
public final class SlidePuzzle {
    private final int[] tiles = {1,2,3,4,5,6,7,8,0};
    private int moves;
    public int[] snapshot() { return tiles.clone(); }
    public int moves() { return moves; }
    public boolean solved() {
        for (int i=0;i<8;i++) if(tiles[i]!=i+1)return false;
        return tiles[8]==0;
    }
    public int blankIndex() {
        for (int i=0;i<tiles.length;i++)if(tiles[i]==0)return i;
        throw new IllegalStateException("No blank tile");
    }
    public boolean move(int index) {
        if(index<0||index>8)return false;
        int z=blankIndex();
        if(Math.abs(z/3-index/3)+Math.abs(z%3-index%3)!=1)return false;
        tiles[z]=tiles[index];tiles[index]=0;moves++;return true;
    }
    public List<Integer> neighbors() {
        int z=blankIndex(); List<Integer> a=new ArrayList<>();
        if(z/3>0)a.add(z-3);
        if(z/3<2)a.add(z+3);
        if(z%3>0)a.add(z-1);
        if(z%3<2)a.add(z+1);
        return a;
    }
    public void shuffle(Random random) {
        int previous=-1;
        for(int k=0;k<140;k++){
            List<Integer> options=neighbors();
            if(options.size()>1)options.remove(Integer.valueOf(previous));
            int currentBlank=blankIndex();
            move(options.get(random.nextInt(options.size())));
            previous=currentBlank;
        }
        if(solved())move(neighbors().get(0));
        moves=0;
    }
}
