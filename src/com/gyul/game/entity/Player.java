package com.gyul.game.entity;

import java.util.List;

import com.gyul.game.item.Consume;
import com.gyul.game.item.Inventory;
import com.gyul.game.item.Item;
import com.gyul.game.item.ItemUtils;
import com.gyul.game.item.Weapon;

public class Player extends Entity {
    private String userID;      // 플레이어 아이디
    private String mainClass;    // 직업
    private Inventory inventory; // 인벤토리
    
    private int exp;            // 경험치
    private int money;
    
    private int progress;
    private int weaponDmg;
    
    private int maxExp;
    
    private Weapon equipped;
    
    public Player() { }
    public Player(String userID, String mainClass) {
        this.userID = userID;
        this.mainClass = mainClass;

        initialize();
    }
    public Player(String name, int health, int atkDamage, int level, String userID, String mainClass, int exp, int money) {
        super(name, health, atkDamage, level);
        this.userID = userID;
        this.mainClass = mainClass;
        this.exp = exp;
        this.money = money;
        
        initialize();
    }
    // EntityDao.loadPlayer에서 생성자 호출됨
    public Player(String name, int health, int atkDamage, int level, String userID, String mainClass, int exp, int money, int progress) {
        super(name, health, atkDamage, level);
        this.userID = userID;
        this.mainClass = mainClass;
        this.exp = exp;
        this.money = money;
        this.progress = progress;
        
        initialize();
    }

    private void initialize() {
        inventory = new Inventory();
        inventory.loadInventory(userID);
        
        Item item = inventory.getEquippedItem(); // 현재 장착중인 아이템
        if (item != null) {
            Weapon w = (Weapon) item;
            weaponDmg = w.getAtk();
            equipped = w;
        }
        
        maxHP = EntityUtils.getStat(mainClass, level).getLevelStats().get("hp");
        maxExp = EntityUtils.getMaxEXP(level);
    }
    
    public String[] getPlayerInfo() {
        String[] info = new String[9];
        info[0] = name;
        info[1] = Integer.toString(atkDamage);
        info[2] = Integer.toString(level);
        info[3] = Integer.toString(exp);
        info[4] = Integer.toString(money);
        info[5] = Integer.toString(health);
        
        return info;
    }
    
    public List<String> getEquipList() {
        return inventory.getEquipList();
    }
    
    public List<String> getConsList() {
        return inventory.getConsList();
    }

    // only dev test
    public void showInventory() {
        List<String> equipList = inventory.getEquipList();
        List<String> consList = inventory.getConsList();

        for (String eq : equipList) {
            System.out.println(eq);
        }

        System.out.println("====================================");

        for (String co : consList) {
            System.out.println(co);
        }
    }
    
    public boolean equipOrRelease(int itemIdx) {
        boolean result = inventory.equipOrRelease(itemIdx); // true = 장착, false = 장착해제
        if (result) {
            Item item = inventory.getEquippedItem(); // 현재 장착중인 아이템
            if (item != null) {
                Weapon w = (Weapon) item;
                atkDamage -= weaponDmg;
                weaponDmg = w.getAtk();
                atkDamage += weaponDmg;
                minDamage = (int) (atkDamage * 0.7);
                maxDamage = (int) (atkDamage * 1.3);
            }
        }
        else {
            atkDamage -= weaponDmg;
            minDamage = (int) (atkDamage * 0.7);
            maxDamage = (int) (atkDamage * 1.3);
            weaponDmg = 0;
            equipped = null;
        }
        
        return result;
    }
    
    public boolean useConsume(Item item) {
        if (item instanceof Consume) {
            Consume cons = (Consume) item;
            int healp = cons.getHealPoint();
            heal(healp);
        }
        
        return inventory.removeItem(item);
    }
    
    public int getProgress() {
        return progress;
    }

    public void addMoney(int money) {        
        this.money += money;        
    }

    public void addExp(int exp) {
        this.exp += exp;
        if (this.exp >= maxExp) {
            levelUp();
        }
    }
    
    private void levelUp() {
        level++;
        exp -= maxExp;
        maxExp = EntityUtils.getMaxEXP(level);
        maxHP = EntityUtils.getStat(mainClass, level).getLevelStats().get("hp");
        atkDamage = EntityUtils.getStat(mainClass, level).getLevelStats().get("dmg") + weaponDmg;
        health = maxHP;
        inventory.addInventory(ItemUtils.getRandomWeapon(mainClass));
    }

    public int getExp() {
        return exp;
    }

    public int getMoney() {
        return money;
    }

    public String getUserID() {
        return userID;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Inventory getInventory() {
        return inventory;
    }
    
}
