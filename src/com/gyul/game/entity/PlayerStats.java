package com.gyul.game.entity;

import java.util.HashMap;
import java.util.Map;

// 레벨 당 플레이어 능력치
public class PlayerStats {
    private int dmg;
    private int hp;
    
    public PlayerStats() {}
    public PlayerStats(int dmg, int hp) {
        this.dmg = dmg;
        this.hp = hp;
    }
    
    public Map<String, Integer> getLevelStats() {
        Map<String, Integer> stats = new HashMap<String, Integer>();
        stats.put("dmg", dmg);
        stats.put("hp", hp);
        
        return stats;
    }
}
