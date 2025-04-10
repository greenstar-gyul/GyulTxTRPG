package com.gyul.game.item;

public class Consume extends Item {
    private int healp;
    
    public Consume() { }
    public Consume(int code, String name, int buy, int sell, int healp) {
        super(code, name, buy, sell);
        this.healp = healp;
    }
    
    @Override
    public String toString() {
        return super.toString() + healp;
    }

    @Override
    public Item clone() {
        return new Consume(code, name, buy, sell, healp); // 얕은 복사 후, 필요한 경우 깊은 복사 추가
    }
    
    public int getHealPoint() {
        return healp;
    }
}
