package com.uenkerlabs.arcade;
import java.util.Random;

/** Command-line smoke test, does not depend on Android or external test frameworks. */
public final class SlidePuzzleSmoke {
    private static void ensure(boolean ok, String message){
        if(!ok)throw new AssertionError(message);
    }
    private static int inversions(int[] a){
        int count=0;
        for(int i=0;i<a.length;i++)
            for(int j=i+1;j<a.length;j++)
                if(a[i]!=0 && a[j]!=0 && a[i]>a[j])count++;
        return count;
    }
    public static void main(String[] args){
        Random rng=new Random(20261111L);
        for(int attempt=0;attempt<10000;attempt++){
            SlidePuzzle puzzle=new SlidePuzzle();puzzle.shuffle(rng);
            int[] board=puzzle.snapshot();boolean[] seen=new boolean[9];
            for(int val:board){
                ensure(val>=0&&val<9,"Value out of range");
                ensure(!seen[val],"Duplicate tile");
                seen[val]=true;
            }
            ensure(inversions(board)%2==0,"Unsolvable board");
            ensure(!puzzle.solved(),"Shuffle returned solved board");
            ensure(puzzle.moves()==0,"Move count not reset");
            ensure(!puzzle.move(-1),"Negative index accepted");
            ensure(!puzzle.move(10),"Index >8 accepted");
            int oldBlank=puzzle.blankIndex(),neighbor=puzzle.neighbors().get(0);
            ensure(puzzle.move(neighbor),"Legal neighbor move denied");
            ensure(puzzle.blankIndex()==neighbor,"Blank not moved");
            ensure(puzzle.move(oldBlank),"Reverse legal move denied");
        }
        System.out.println("PASS: 10000 puzzle shuffles, parity, uniqueness and legal moves");
    }
}
