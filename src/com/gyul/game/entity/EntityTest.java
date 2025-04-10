package com.gyul.game.entity;

public class EntityTest {
    public static void main(String[] args) {
        Entity user = new Entity("홍길동", 10, 5);
        Entity mob1 = new Entity("늑대", 10, 1);
        
        user.attack(mob1);

        System.out.println("\n\nEnd Test");

        Player p1 = new Player("aaa", "검사");
        p1.showInventory();
    }
}
