package com.nullprogram.chess.ai;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Irindul on 22/05/2017.
 */
public class TTMap<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = 1L;
    private int max_cap;

    public TTMap(int initial_cap, int max_cap, float loadFactor) {
        super(initial_cap, loadFactor, true);
        this.max_cap = max_cap;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > this.max_cap;
    }
}
