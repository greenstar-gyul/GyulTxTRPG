package com.gyul.game.item;

public class Weapon extends Item {
    private int atk;
    private int reqlv;
    private int equipCode;
    
    private boolean isEquipped;
    
    public Weapon() { }
    public Weapon(int atk, int reqlv) {
        this.atk = atk;
        this.reqlv = reqlv;
    }
    public Weapon(int code, String name, int buy, int sell, int atk, int reqlv) {
        super(code, name, buy, sell);
        this.atk = atk;
        this.reqlv = reqlv;
    }
    
    public int getEquipCode() {
        return equipCode;
    }
    
    public void setEquipCode(int code) {
        equipCode = code;
    }

    public void setIsEquipped(boolean isEquipped) {
        this.isEquipped = isEquipped;
    }
    
    public boolean getIsEquipped() {
        return isEquipped;
    }
    
    public int getAtk() {
        return atk;
    }
    
    @Override
    public String toString() {
        return super.toString() + atk + " " + reqlv + " " + (isEquipped ? "Y" : "N");
    }

    @Override
    public Item clone() {
        return new Weapon(code, name, buy, sell, atk, reqlv);
    }
    
    @Override
    public String[] getItemInfo() {
        String[] info = new String[4];
        info[0] = name;
        info[1] = Integer.toString(atk);
        info[2] = Integer.toString(reqlv);
        info[3] = isEquipped ? "Y" : "N";
        
        return info;
    }
    
    @Override
    public int hashCode() {
        return equipCode;
    }
}
