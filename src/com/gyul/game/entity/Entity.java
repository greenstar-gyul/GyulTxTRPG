package com.gyul.game.entity;

// 모든 개체에 대한 슈퍼클래스
public class Entity {
    protected String name;
    protected int health;
    protected int atkDamage;
    protected int level;
    
    protected int minDamage;
    protected int maxDamage;
    
    protected int maxHP;
    
    public Entity() { }
    public Entity(String name, int health, int atkDamage) {
        this.name = name;
        this.health = health;
        this.atkDamage = atkDamage;
        minDamage = (int) (atkDamage * 0.7); // ex 3
        maxDamage = (int) (atkDamage * 1.3); // ex 6
    }
    public Entity(String name, int health, int atkDamage, int level) {
        this.name = name;
        this.health = health;
        this.atkDamage = atkDamage;
        this.level = level;
        minDamage = (int) (atkDamage * 0.7); // ex 3
        maxDamage = (int) (atkDamage * 1.3); // ex 6
    }
    
    public int attack(Entity target) {
//        System.out.println("최소 데미지: " + minDamage + " 최대 데미지: " + maxDamage);
        int damage = (int) (Math.random() * (maxDamage - minDamage + 1) + minDamage);
        target.damaged(damage);
//        target.health -= damage;
////        System.out.println(damage + "의 데미지를 줬다!");
//        if (target.health <= 0) {
//            target.death();
//        }

        return damage;
    }
    
    protected void damaged(int damage) {
        this.health -= damage;
//        System.out.println(name + "이(가) " + damage + "의 데미지를 받았다!");
        if (this.health <= 0) {
            this.death();
        }
    }
    
    public void death() {
//        System.out.println(name + ": 나 주거써 ㅠㅠ");
    }
    
    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getHP() {
        return health;
    }

    public int getAtk() {
        return atkDamage;
    }
    
    public void heal(int healp) {
        health += healp;
        if (health > maxHP)
            health = maxHP;
        
//        System.out.println(healp + "만큼 회복했다!" + " 현재 체력 : " + health);
    }
    
    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public int getMaxHP() {
        return maxHP;
    }
}
