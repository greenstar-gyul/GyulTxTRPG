package com.gyul.game.entity;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.gyul.game.MainApp;

public class EntityUtils {
    private static Map<String, Map<String, Monster>> monsterInfo;
    private static Map<Integer, Integer> expTable;
    private static Map<String, Map<Integer, PlayerStats>> playerStat;

    private static EntityUtils entityUtils = new EntityUtils();

    private EntityUtils() {
        Initialize();
    }

    public static EntityUtils getInstance() {
        return entityUtils;
    }

    private static void Initialize() {
        if (monsterInfo == null) {
            try {
                Reader reader = new InputStreamReader(new FileInputStream(MainApp.JSON_PATH + "monsters.json"), "UTF-8");
                Type type = new TypeToken<Map<String, Map<String, JsonObject>>>() {}.getType();
                Gson gson = new Gson();
                Map<String, Map<String, JsonObject>> rawData = gson.fromJson(reader, type);

                monsterInfo = new HashMap<>();
                for (Map.Entry<String, Map<String, JsonObject>> entry : rawData.entrySet()) {
                    String category = entry.getKey();
                    Map<String, Monster> monsterMap = new HashMap<>();

                    for (Map.Entry<String, JsonObject> monsterEntry : entry.getValue().entrySet()) {
                        String code = monsterEntry.getKey();
                        JsonObject obj = monsterEntry.getValue();
                        Monster monster = parseItem(category, obj);
                        monsterMap.put(code, monster);
                    }

                    monsterInfo.put(category, monsterMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (expTable == null) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<Integer, Integer>>() {}.getType();
                expTable = gson.fromJson(new FileReader(MainApp.JSON_PATH + "exp_table.json"), type);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (playerStat == null) {
            try {
                Reader reader = new InputStreamReader(new FileInputStream(MainApp.JSON_PATH + "players.json"), "UTF-8");
                Type type = new TypeToken<Map<String, Map<Integer, JsonObject>>>() {}.getType();
                Gson gson = new Gson();
                Map<String, Map<Integer, JsonObject>> rawData = gson.fromJson(reader, type);

                playerStat = new HashMap<>();
                for (Map.Entry<String, Map<Integer, JsonObject>> entry : rawData.entrySet()) {
                    String category = entry.getKey();
                    Map<Integer, PlayerStats> statMap = new HashMap<>();

                    for (Map.Entry<Integer, JsonObject> monsterEntry : entry.getValue().entrySet()) {
                        int level = monsterEntry.getKey();
                        JsonObject obj = monsterEntry.getValue();
                        PlayerStats stat = parseStat(category, obj);
                        statMap.put(level, stat);
                    }

                    playerStat.put(category, statMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private static PlayerStats parseStat(String category, JsonObject obj) {
        int dmg = obj.get("dmg").getAsInt();
        int hp = obj.get("hp").getAsInt();
        
        switch (category) {
        case "검사":
            return new PlayerStats(dmg, hp);
        case "궁수":
            return new PlayerStats(dmg, hp);
            
        default:
            throw new IllegalArgumentException("알 수 없는 직업 타입: " + category);
        }
    }

    private static Monster parseItem(String category, JsonObject obj) {
        String name = obj.get("name").getAsString();
        int health = obj.get("health").getAsInt();
        int damage = obj.get("damage").getAsInt();
        int level = obj.get("level").getAsInt();
        int exp = obj.get("exp").getAsInt();
        int drop = obj.get("drop").getAsInt();

        switch (category) {
            case "일반":
                return new Monster(name, health, damage, level, exp, drop);

            case "보스":
                return new Monster(name, health, damage, level, exp, drop);

            default:
                throw new IllegalArgumentException("알 수 없는 몬스터 타입: " + category);
        }
    }

    public static Monster getMonster(String name) {
        for (Map<String, Monster> monsters : monsterInfo.values()) {
            if (monsters.containsKey(name)) {
                return monsters.get(name);
            }
        }
        return null;
    }
    
    public static PlayerStats getStat(String mainClass, int level) {
        return playerStat.get(mainClass).get(level);
    }
    
    public static int getMaxEXP(int level) {
        return expTable.get(level);
    }

    public static Map<String, Map<String, Monster>> getAllMonsters() {
        return monsterInfo;
    }
}
