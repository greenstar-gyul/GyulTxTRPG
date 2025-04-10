package com.gyul.game.item;

import java.util.Map;

public class Item implements Cloneable {
    
    
    
    protected int dataCode;   // DB에 있는 data_code
    protected int code;       // JSON에 있는 아이템 코드(db에는 item_code로 저장)
    protected String name;
    protected int buy;
    protected int sell;

    protected static Map<String, Map<Integer, Item>> itemInfo;

    public Item() { }

    public Item(int code, String name, int buy, int sell) {
        this.code = code;
        this.name = name;
        this.buy = buy;
        this.sell = sell;
    }

    
    public void setItemCode(int code) {
        this.code = code;
    }
    
    public void setDataCode(int code) {
        this.dataCode = code;
    }

    public int getItemCode() {
        return code;
    }

    @Override
    public int hashCode() {
        return dataCode;
    }
    
    @Override
    public String toString() {
        return name + " ";
    }

    public Item clone() {
        return new Item(code, name, buy, sell);
    }
    
    public String[] getItemInfo() {
        String[] info = new String[3];
        
        return info;
    }
    
    public String getName() {
        return name;
    }
    
    // TODO only dev
    public void test() {
        System.out.println();
    }
    
}
