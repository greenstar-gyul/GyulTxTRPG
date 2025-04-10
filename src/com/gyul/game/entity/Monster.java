package com.gyul.game.entity;

public class Monster extends Entity {
    private int exp;
    private int drop;
    
    public Monster() { }
    public Monster(String name, int health, int atkDamage, int level, int exp, int drop) {
        super(name, health, atkDamage, level);
        this.exp = exp;
        this.drop = drop;
    }
    
    public String[] getMonsterInfo() {
        String[] monsterInfo = new String[6];
        monsterInfo[0] = name;
        monsterInfo[1] = Integer.toString(health);
        monsterInfo[2] = Integer.toString(atkDamage);
        monsterInfo[3] = Integer.toString(level);
        monsterInfo[4] = Integer.toString(exp);
        monsterInfo[5] = Integer.toString(drop);
        
        return monsterInfo;
    }
    
    public Monster clone() {
        return new Monster(this.name, this.health, this.atkDamage, this.level, this.exp, this.drop);
    }

    public int[] dropReward() {
        int[] rewards = {this.drop, this.exp};
        return rewards;
    }
}
