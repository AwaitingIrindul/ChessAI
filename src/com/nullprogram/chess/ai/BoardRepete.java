package com.nullprogram.chess.ai;

import com.nullprogram.chess.Board;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Irindul on 22/05/2017.
 */
public class BoardRepete {

    private Map<Long, Integer> map;

    public BoardRepete() {
        map = new HashMap<>();
    }

    public int increment(Board b){
        long id = b.id();
        int value = 1;
        if(map.containsKey(id)){
            value = map.get(id) + 1;
            map.replace(id, value);
        } else {
            map.put(id, value);
        }

        return value;
    }

    public int decrement(Board b){
        long id = b.id();
        int value = 1;
        if(map.containsKey(id)){
            value = map.get(id) - 1;
            map.replace(id, value);
        } else {
            value--;
            map.put(id, value);
        }

        return value;
    }


    public boolean isRepetition(Board board) {
        long id = board.id();
        return map.containsKey(id) && map.get(id) > 0;
    }


    public boolean isDraw(Board board) {
        long id = board.id();
        return map.containsKey(id) && map.get(id) >= 3;
    }
}
