package com.gyul.game.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gyul.game.MainApp;
import com.gyul.game.entity.EntityDao;
import com.gyul.game.entity.EntityUtils;
import com.gyul.game.entity.Monster;
import com.gyul.game.entity.Player;
import com.gyul.game.item.Item;
import com.gyul.game.item.ItemUtils;

public class ConsoleGameUI {
    // 가로 72칸, 세로 18줄
    private int uiWidth = 70; // 실제 텍스트 공간(너비)
    private int uiHeight = 20; // 실제 텍스트 공간(높이)
    private Map<String, Map<String, Map<String, String[]>>> menuJson;
    private Map<String, Map<String, Map<String, String[]>>> eventJson;
    private int progress;
    private int beforeSelect = -1;
    private int select = -1;

    private Player player;
    private Monster monster;

    private EntityDao eDao = new EntityDao();

    public ConsoleGameUI() { } //

    public ConsoleGameUI(Player player) {
        this.player = player;
       progress = player.getProgress();
        // progress = 1001; // 개발 테스트 전용

        initialize();
    }
    
    private void initialize() {
        if (menuJson == null)
            menuJson = ConsoleUIUtils.readJson(MainApp.JSON_PATH + "menu.json");
        if (eventJson == null)
            eventJson = ConsoleUIUtils.readJson(MainApp.JSON_PATH + "events.json");
    }
    
    // 게임 메인 루프
    public void gameStart() {
        beforeSelect = -1;
        select = -1;
        boolean isSelect = false; // 선택지에서 선택해야하는 상태인지~~
        while (true) {
            if ((progress % 1000) >= 900) { // 마을이란 뜻
                beforeSelect = -1;
                select = hometown();

                if (select < 0) // 게임 종료
                    return;
                
                if (select == 0) {
                    progress += 2;
                }

                isSelect = false;
            }
            else { // 마을이 아님~~
                if (!isSelect) { 
                    beforeSelect = select;
                    select = gameMainUI();

                    if (select < 0) // 게임 오버 당한 상태임
                        return;

                    if (select == 0) { // 프롤로그(?)가 아닌데 도망쳤음
                        int chapter = (int) (progress / 1000);
                        progress = (chapter - 1) * 1000 + 999;
                    }
                    else if (select == 999) { // 챕터가 끝나고 마을로 왔음.
                        
                    }
                    else {
                        isSelect = true;
                    }
                } else {
                    resultUI(beforeSelect, select);
                    isSelect = false;
                }
//                System.out.println(progress);
            }
        }
    }

    // 마을 UI
    private int hometown() {
        int menu = -1;
        while (true) {
            hometownUI();
            menu = ConsoleUIUtils.getIntMenu("선택 >> ", "반드시 숫자를 입력해주세요!!");
            
            switch (menu) {
            case 1: // 여관 휴식
                hotel();
                break;
            case 2: // 던전 출발
                if (progress > 2000)
                    break;
                else
                    return 0;
            case 3: // 인벤토리 열기
                inventory();
                break;
            case 4: // 수련장 이용
                trainingCenter();
                break;
            case 7: // 저장하기
                player.setProgress(progress);
                eDao.savePlayer(player);
                break;
            case 8: // 저장하고 타이틀로
                player.setProgress(progress);
                eDao.savePlayer(player);
                return -1;
            case 9: // 타이틀로
                return -1;
            }
        }
    }
    
    // 수련장 이용하기
    private void trainingCenter() {
        if (player.getMoney() >= 10) {
            String[] msg = {
                    "수련장을 이용하고 15의 경험치를 획득했습니다.",
                    ""
            };
            
            showTrainingCenter(msg);
            player.addMoney(-10);
            player.addExp(15);
        }
        else {
            String[] msg = {
                    "수련장을 이용하려면 10골드가 필요합니다."
            };
            showTrainingCenter(msg);
        }
        
        
    }

    private void showTrainingCenter(String[] msg) {
        if (msg.length > 1) {
            Item potion = ItemUtils.getRandomPotion();
            if (potion != null) {
                msg[msg.length - 1] = potion.getName() + "을 1개 획득했습니다.";
                player.getInventory().addInventory(potion);
            }
        }
        
        int textMaxLen = ConsoleUIUtils.getMaxLength(msg);

        int menuPaddingHeight = uiHeight - 2                     // 텍스트 위아래 패딩            
                                         - msg.length;           // 메인 텍스트

        int menuPaddingTop = (menuPaddingHeight % 2 == 0) ? (menuPaddingHeight / 2) : (menuPaddingHeight / 2 + 1);
        int menuPaddingBottom = menuPaddingHeight / 2;

        System.out.println("┏" + "━".repeat(uiWidth) + "┓");

        for (int i = 0; i < menuPaddingTop; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText("┼" + "─".repeat(textMaxLen) + "┼", uiWidth));
        for (int i = 0; i < msg.length; i++) {
            if (i == msg.length - 1 && msg[i].isBlank())
                continue;
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText("│" + ConsoleUIUtils.centerText(msg[i], textMaxLen) + "│", uiWidth));
        }
        System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText("┼" + "─".repeat(textMaxLen) + "┼", uiWidth));
        
        
        for (int i = 0; i < menuPaddingBottom; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        System.out.println("┗" + "━".repeat(uiWidth) + "┛");

        ConsoleUIUtils.getAnyInput("아무 키 입력 ... >>");
    }
    
    private void hometownUI() {
        String chapterNum = Integer.toString(((int) (progress / 1000)) * 1000);
        String[] events = eventJson.get(chapterNum).get("999").get("Event");
        String[] selects = eventJson.get(chapterNum).get("999").get("Select");

        int paddingHeight = uiHeight - events.length // 내용 길이
                    - 3 // 내용, 선택지 구분선
                    - selects.length; // 선택지 길이
        int paddingTop = (paddingHeight % 2 == 0) ? (paddingHeight / 2) : (paddingHeight / 2 + 1);
        int paddingBottom = paddingHeight / 2;

        System.out.println("┏" + "━".repeat(uiWidth) + "┓");
        
        for (int i = 0; i < paddingTop; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        for (int i = 0; i< events.length; i++) {            
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(events[i], uiWidth));
        }
        
        System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText("=".repeat(50), uiWidth));
        System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        
        for (int i = 0; i< selects.length; i++) {            
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(selects[i], uiWidth));
        }
        
        for (int i = 0; i < paddingBottom; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        System.out.println("┗" + "━".repeat(uiWidth) + "┛");
    }
    
    // 여관~
    private void hotel() {
        String msg = "";
        if (player.getMoney() >= 100) {
            msg = "호텔에서 휴식을 취하고 체력을 회복했습니다.";
        }
        else {
            msg = "호텔을 이용하려면 100골드가 필요합니다.";
        }
        
        int paddingHeight = uiHeight - 1; // 내용 길이
        int paddingTop = (paddingHeight % 2 == 0) ? (paddingHeight / 2) : (paddingHeight / 2 + 1);
        int paddingBottom = paddingHeight / 2;

        System.out.println("┏" + "━".repeat(uiWidth) + "┓");

        for (int i = 0; i < paddingTop; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(msg, uiWidth));

        for (int i = 0; i < paddingBottom; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }

        System.out.println("┗" + "━".repeat(uiWidth) + "┛");
        
        if (player.getMoney() >= 100) {
            player.heal(player.getMaxHP() / 2);
            player.addMoney(-100);
        }
        
        ConsoleUIUtils.getAnyInput("계속하려면 아무 키나 입력 >> ");
    }
    
    // 이벤트 진행 중 UI
    private int gameMainUI() {
        String chapterNum = Integer.toString(((int) (progress / 1000)) * 1000);
        String eventNum = Integer.toString(progress % 1000);
        
//        System.out.println("beforeSelect: " + beforeSelect + " eventNum: " + eventNum + " string: " + "Event" + Integer.toString(beforeSelect));

        String[] events;
        if (beforeSelect > 0)
            events = eventJson.get(chapterNum).get(eventNum).get("Event" + Integer.toString(beforeSelect));
        else
            events = eventJson.get(chapterNum).get(eventNum).get("Event");

        if (events[0].equals("End of Chapter")) {
            progress = Integer.parseInt(chapterNum) + 999;
//            System.out.println(progress);
            endChapterUI(events);
            return 999;
        }

        String[] selects;
        if (beforeSelect > 0)
            selects = eventJson.get(chapterNum).get(eventNum).get("Select" + Integer.toString(beforeSelect));
        else
            selects = eventJson.get(chapterNum).get(eventNum).get("Select");
        
        // 전투 이벤트면 따로 진행
        if (events[0].equals("Battle")) {
            return battleUI();
        }
        else {
            int paddingHeight = uiHeight - events.length // 내용 길이
                    - 3 // 내용, 선택지 구분선
                    - selects.length; // 선택지 길이
            int paddingTop = (paddingHeight % 2 == 0) ? (paddingHeight / 2) : (paddingHeight / 2 + 1);
            int paddingBottom = paddingHeight / 2;
    
            System.out.println("┏" + "━".repeat(uiWidth) + "┓");
    
            for (int i = 0; i < paddingTop; i++) {
                System.out.println("┃" + " ".repeat(uiWidth) + "┃");
            }
    
            for (int i = 0; i < events.length; i++) {
                System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(events[i], uiWidth));
            }
    
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText("=".repeat(50), uiWidth));
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
    
            for (int i = 0; i < selects.length; i++) {
                System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(selects[i], uiWidth));
            }
    
            for (int i = 0; i < paddingBottom; i++) {
                System.out.println("┃" + " ".repeat(uiWidth) + "┃");
            }
    
            System.out.println("┗" + "━".repeat(uiWidth) + "┛");
    
            int select = ConsoleUIUtils.getIntMenu("선택 >> ", "숫자만 입력해주세요!!");
    
            while (select > selects.length || select < 0
                    || (chapterNum.equals("1000") && select == 0)) { // 프롤로그(?)면 도망 ㄴㄴ
                
                System.out.println("다시 입력해주세요!!");
                select = ConsoleUIUtils.getIntMenu("선택 >> ", "숫자만 입력해주세요!!");
            }
    
            return select;
        }
    }

    // 챕터가 끝났을 때 UI
    private void endChapterUI(String[] events) {
        int paddingHeight = uiHeight - events.length - 1; // 내용 길이
        int paddingTop = (paddingHeight % 2 == 0) ? (paddingHeight / 2) : (paddingHeight / 2 + 1);
        int paddingBottom = paddingHeight / 2;

        System.out.println("┏" + "━".repeat(uiWidth) + "┓");
    
        for (int i = 0; i < paddingTop; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }

        for (int i = 0; i < events.length; i++) {
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(events[i], uiWidth));
        }

        for (int i = 0; i < paddingBottom; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }

        System.out.println("┗" + "━".repeat(uiWidth) + "┛");

        ConsoleUIUtils.getAnyInput("마을로 들어가려면 아무거나 입력 >> ");
    }

    // 선택지 골랐을 때 나오는 글
    private void resultUI(int beforeSelect, int selectNum) {
        String chapterNum = Integer.toString(((int) (progress / 1000)) * 1000);
        String eventNum = Integer.toString(progress % 1000);

        String[] events;
        if (beforeSelect <= 0)
            events = eventJson.get(chapterNum).get(eventNum).get("Result" + selectNum);
        else 
            events = eventJson.get(chapterNum).get(eventNum).get(("Result" + beforeSelect) + selectNum);
        
        int paddingHeight = uiHeight - events.length;
        
        boolean isJump = false;
        if (events[0].equals("Jump")) { // 특정 이벤트로 건너뛰기.(progress의 수치 조정)
            isJump = true;
            paddingHeight -= 2;
        }


        boolean isReward = false;
        if (events[0].equals("Reward")) { // 이전 선택에 따른 보상이 있다면!!
            isReward = true;
            paddingHeight--;
        }

        int paddingTop = (paddingHeight % 2 == 0) ? (paddingHeight / 2) : (paddingHeight / 2 + 1);
        int paddingBottom = paddingHeight / 2;
        

        System.out.println("┏" + "━".repeat(uiWidth) + "┓");

        for (int i = 0; i < paddingTop; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }

        
        for (int i = 0; i < events.length; i++) {
            if (isReward && i == 0) // 0번째 값에는 Reward라는 문자열이 있기 때문에 생략.
                continue;
            if (isJump && i < 2)
                continue;
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(events[i], uiWidth));
        }

        for (int i = 0; i < paddingBottom; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }

        System.out.println("┗" + "━".repeat(uiWidth) + "┛");

        if (isReward) {
            int reward = Integer.parseInt(eventJson.get(chapterNum).get(eventNum).get(("Reward" + beforeSelect) + selectNum)[0]);
            player.addMoney(reward);
        }

        String buff = ConsoleUIUtils.getAnyInput("계속하려면 아무 키 입력... >> ");
        
        if (isJump)
            progress = Integer.parseInt(events[1]);
        else
            progress++;
    }

    // 전투 루프
    private int battleUI() {
        String chapterNum = Integer.toString(((int) (progress / 1000)) * 1000);
        String eventNum = Integer.toString(progress % 1000);

        String[] events;
        if (beforeSelect > 0)
            events = eventJson.get(chapterNum).get(eventNum).get("Event" + Integer.toString(beforeSelect));
        else
            events = eventJson.get(chapterNum).get(eventNum).get("Event");
        
        String[] selects;
        if (beforeSelect > 0)
            selects = eventJson.get(chapterNum).get(eventNum).get("Select" + Integer.toString(beforeSelect));
        else
            selects = eventJson.get(chapterNum).get(eventNum).get("Select");

        monster = EntityUtils.getMonster(events[1]).clone();

        String[] monsterInfoJson = new String[events.length - 2];
        monsterInfoJson[0] = events[2];
        monsterInfoJson[1] = events[3];
        monsterInfoJson[2] = events[4];

        String[] userInfoJson = menuJson.get("게임메뉴").get("인벤토리").get("유저정보");

        boolean myTurn = true;
        int select = -1;
        while (true) {
            int monsterHP = Integer.parseInt(monster.getMonsterInfo()[1]);
            int playerHP = Integer.parseInt(player.getPlayerInfo()[5]);
            if (playerHP <= 0) {
                String[] msg = {
                    "전투에서 패배했습니다 ㅠㅁㅠ",
                    "GAME OVER"
                };
                battleResultUI(msg);
                return -1; // 패배 ㅠㅠ Game Over
            }

            if (monsterHP <= 0) {
                String[] msg = {
                    "전투에서 승리했습니다!",
                    "보상으로 " + monster.getMonsterInfo()[5] + "골드를 획득했습니다."
                };
                battleResultUI(msg);

                int[] rewards = monster.dropReward();
                player.addMoney(rewards[0]);
                player.addExp(rewards[1]);
                
                return 1; // 승리!!
            }

            if (myTurn) {
                while (select <= 0 || select > selects.length) {
                    showBattleUI(monsterInfoJson, userInfoJson, selects, "행동을 선택해주세요.");
                    select = ConsoleUIUtils.getIntMenu("행동을 선택해주세요. >> ", "입력값이 숫자가 아니에요!!");
                }

                String log = "";
                // 추후 확장 고려
                switch (select) {
                case 1: // 공격
                    int damage = player.attack(monster);
                    log = monster.getName() + "에게 " + Integer.toString(damage) + "의 데미지를 주었다.";
                    showBattleUI(monsterInfoJson, userInfoJson, selects, log);
                    break;
                case 2: // 인벤토리
                    log = "인벤토리를 열고 턴을 넘깁니다.";
                    inventory();
                    showBattleUI(monsterInfoJson, userInfoJson, selects, log);
                    break;
                }
                
                myTurn = false;
                ConsoleUIUtils.getAnyInput("턴을 넘기려면 아무거나 입력 >> ");
                select = -1;
            }
            else {
                int damage = monster.attack(player);
                String log = monster.getName() + "에게 " + Integer.toString(damage) + "의 데미지를 받았다.";
                showBattleUI(monsterInfoJson, userInfoJson, selects, log);
                myTurn = true;
                ConsoleUIUtils.getAnyInput("계속 하려면 아무거나 입력 >> ");
            }

//            System.out.println("턴 넘김");
        }
    }

    // 전투 화면 UI
    private void showBattleUI(String[] monsterInfoJson, String[] userInfoJson, String[] selects, String log) {
        String[] monsterStatus = loadMonsterInfo(monsterInfoJson, monster.getMonsterInfo());
        String[] userStatus = loadUserInfo(userInfoJson, player.getPlayerInfo());

        int mInfoMaxLen = ConsoleUIUtils.getMaxLength(monsterStatus);
        int uInfoMaxLen = ConsoleUIUtils.getMaxLength(userStatus);
        int menuPaddingHeight = uiHeight - 2                            // 텍스트 위아래 패딩            
                                         - monsterStatus.length
                                         - 2                            // 텍스트 위아래 패딩
                                         - userStatus.length
                                         - 1                            // 메뉴 위 패딩
                                         - selects.length
                                         - 1;                           // 전투로그 1줄

        int menuPaddingTop = (menuPaddingHeight % 2 == 0) ? (menuPaddingHeight / 2 + 1) : (menuPaddingHeight / 2);
        int menuPaddingBottom = menuPaddingHeight / 2;
        
        System.out.println("┏" + "━".repeat(uiWidth) + "┓");

        System.out.printf("┃%s┃\n", ConsoleUIUtils.rightText("┼" + "─".repeat(mInfoMaxLen) + "┼", uiWidth));
        for (int i = 0; i < monsterStatus.length; i++) {
            System.out.printf("┃%s┃\n",
                    ConsoleUIUtils.rightText("│" + ConsoleUIUtils.leftText(monsterStatus[i], mInfoMaxLen) + "│", uiWidth));
        }
        System.out.printf("┃%s┃\n", ConsoleUIUtils.rightText("┼" + "─".repeat(mInfoMaxLen) + "┼", uiWidth));

        
        for (int i = 0; i < menuPaddingTop; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }

        System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(log, uiWidth));

        for (int i = 0; i < menuPaddingBottom; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }

        System.out.printf("┃%s┃\n", ConsoleUIUtils.leftText("┼" + "─".repeat(uInfoMaxLen) + "┼", uiWidth));
        for (int i = 0; i < userStatus.length; i++) {
            System.out.printf("┃%s┃\n",
                    ConsoleUIUtils.leftText("│" + ConsoleUIUtils.leftText(userStatus[i], uInfoMaxLen) + "│", uiWidth));
        }
        System.out.printf("┃%s┃\n", ConsoleUIUtils.leftText("┼" + "─".repeat(uInfoMaxLen) + "┼", uiWidth));

        System.out.println("┣" + "━".repeat(uiWidth) + "┫");
        
        String selectMenu = "  ";
        for (String s : selects) {
            selectMenu += s + "  ";
        }
        System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(selectMenu, uiWidth));

        System.out.println("┗" + "━".repeat(uiWidth) + "┛");
    }

    // 전투 결과 UI 출력
    private void battleResultUI(String[] msg) {
        int textMaxLen = ConsoleUIUtils.getMaxLength(msg);

        int menuPaddingHeight = uiHeight - 2                     // 텍스트 위아래 패딩            
                                         - msg.length;           // 메인 텍스트

        int menuPaddingTop = (menuPaddingHeight % 2 == 0) ? (menuPaddingHeight / 2) : (menuPaddingHeight / 2 + 1);
        int menuPaddingBottom = menuPaddingHeight / 2;

        System.out.println("┏" + "━".repeat(uiWidth) + "┓");

        for (int i = 0; i < menuPaddingTop; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText("┼" + "─".repeat(textMaxLen) + "┼", uiWidth));
        for (int i = 0; i < msg.length; i++) {
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText("│" + ConsoleUIUtils.centerText(msg[i], textMaxLen) + "│", uiWidth));
        }
        System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText("┼" + "─".repeat(textMaxLen) + "┼", uiWidth));
        
        
        for (int i = 0; i < menuPaddingBottom; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        System.out.println("┗" + "━".repeat(uiWidth) + "┛");

        ConsoleUIUtils.getAnyInput("아무 키 입력 ... >>");
    }

    private String[] loadMonsterInfo(String[] monsterInfoJson, String[] monsterInfo) {
        String[] monsterStatus = new String[monsterInfoJson.length];

        monsterStatus[0] = String.format(monsterInfoJson[0], monsterInfo[0]);
        monsterStatus[1] = String.format(monsterInfoJson[1], monsterInfo[1]);
        monsterStatus[2] = String.format(monsterInfoJson[2], monsterInfo[2]);

        return monsterStatus;
    }


    // 각 아이템들의 문자열을 공백으로 구분해서 String[] 리스트로 반환
    private List<String[]> loadItems(List<String> itemList) {
        List<String[]> items = new ArrayList<String[]>();
        for (String el : itemList) {
            String[] temps = el.split(" "); // 공백 구분해서 이름/공격력/레벨/장착여부 4개일거임 --> 이건 장비.
                                            // 소비라면 아마 이름/효과/개수 3개일거임
            items.add(temps);
        }
        return items;
    }

    private String[] loadUserInfo(String[] userInfoJson, String[] playerInfo) {
        String[] userInfo = new String[userInfoJson.length];
        userInfo[0] = String.format(userInfoJson[0], playerInfo[0]);
        userInfo[1] = String.format(userInfoJson[1], playerInfo[1]);
        userInfo[2] = String.format(userInfoJson[2], playerInfo[2]);
        userInfo[3] = String.format(userInfoJson[3], playerInfo[3]);
        userInfo[4] = String.format(userInfoJson[4], playerInfo[4]);
        userInfo[5] = String.format(userInfoJson[5], playerInfo[5]);
        return userInfo;
    }

    // 인벤토리 메인 UI
    public void inventory() {
        String[] userInfoJson = menuJson.get("게임메뉴").get("인벤토리").get("유저정보");
        String[] equipInfo = menuJson.get("게임메뉴").get("인벤토리").get("장비");
        String[] equipInvenSelection = menuJson.get("게임메뉴").get("인벤토리").get("메뉴");
        String[] consInfo = menuJson.get("게임메뉴").get("인벤토리").get("소비");
        String[] consInvenSelection = menuJson.get("게임메뉴").get("인벤토리").get("소비메뉴");

        String[] playerInfo = player.getPlayerInfo();
        String[] userInfo = loadUserInfo(userInfoJson, playerInfo);

//        int paddingHeight = uiHeight - 2 // 유저정보 위아래 공간
//                - userInfo.length // 유저정보(6줄)
//                - equipInfo.length // 장비 정보
//                - 1 // 구분선
//                - 5 // 5줄씩 출력
//                - 1 // 메뉴 윗여백 1줄
//                - 1; // 메뉴

        int infoMaxLen = ConsoleUIUtils.getMaxLength(userInfo) + 2;

        List<String> equipList = player.getEquipList();
        List<String[]> equips = loadItems(equipList);

        List<String> consList = player.getConsList();
        List<String[]> conss = loadItems(consList);

        String menu = "";
        int page = 0;
        int mode = 0; // 0 : 장비창, 1 : 소비창
        String log = "";
        while (!(menu.equals("Q") || menu.equals("q"))) {
            if (mode == 0)
                showEquipInven(userInfo, equipInfo, equipInvenSelection, log, infoMaxLen, equips, page);
            else
                showConsInven(userInfo, consInfo, consInvenSelection, log, infoMaxLen, conss, page);

            log = "";
            menu = ConsoleUIUtils.getInputCode("선택 >> ");

            if (menu.equals("N") || menu.equals("n")) {
                if (page < 1) {
                    page++;
                }
            } else if (menu.equals("P") || menu.equals("p")) {
                if (page > 0) {
                    page--;
                }
            }
            // 소모품은 나중에 합시다(소모품 사용 설정 안했음)
            else if (menu.equals("1")) {
                int itemIdx = 0 + page * 5;
                if (mode == 0) { // 장비 인벤토리 상태
                    if (itemIdx < equips.size()) {
                        if (player.getLevel() >= Integer.parseInt(equips.get(itemIdx)[2])) {
                            boolean result = player.equipOrRelease(itemIdx);
                            userInfo = loadUserInfo(userInfoJson, player.getPlayerInfo());
                            equipList = player.getEquipList();
                            equips = loadItems(equipList);
                            
                            if (result)
                                log = "[" + equips.get(itemIdx)[0] + "을 장착했습니다.]";
                            else 
                                log = "[" + equips.get(itemIdx)[0] + "을 장착 해제했습니다.]";
                        }
                        else {
                            log = "[레벨이 낮아 장착할 수 없습니다.]";
                        }
                    }
                }
                else { // 소비 인벤토리 상태
                    if (itemIdx < conss.size()) {
                        String consName = conss.get(itemIdx)[0];
                        Item item = ItemUtils.getItem(consName);
                        player.useConsume(item);
                        userInfo = loadUserInfo(userInfoJson, player.getPlayerInfo());
                        consList = player.getConsList();
                        conss = loadItems(consList);
                        log = "[" + consName + "]을 사용했습니다.";
                    }
                }
            } else if (menu.equals("2")) {
                int itemIdx = 1 + page * 5;
                if (mode == 0) { 
                    if (itemIdx < equips.size()) {
                        if (player.getLevel() >= Integer.parseInt(equips.get(itemIdx)[2])) {
                            boolean result = player.equipOrRelease(itemIdx);
                            userInfo = loadUserInfo(userInfoJson, player.getPlayerInfo());
                            equipList = player.getEquipList();
                            equips = loadItems(equipList);
                            
                            if (result)
                                log = "[" + equips.get(itemIdx)[0] + "을 장착했습니다.]";
                            else 
                                log = "[" + equips.get(itemIdx)[0] + "을 장착 해제했습니다.]";
                        }
                        else {
                            log = "[레벨이 낮아 장착할 수 없습니다.]";
                        }
                    }
                }
                else { 
                    if (itemIdx < conss.size()) {
                        String consName = conss.get(itemIdx)[0];
                        Item item = ItemUtils.getItem(consName);
                        player.useConsume(item);
                        userInfo = loadUserInfo(userInfoJson, player.getPlayerInfo());
                        consList = player.getConsList();
                        conss = loadItems(consList);
                        log = "[" + consName + "]을 사용했습니다.";
                    }
                }
            } else if (menu.equals("3")) {
                int itemIdx = 2 + page * 5;
                if (mode == 0) { 
                    if (itemIdx < equips.size()) {
                        if (player.getLevel() >= Integer.parseInt(equips.get(itemIdx)[2])) {
                            boolean result = player.equipOrRelease(itemIdx);
                            userInfo = loadUserInfo(userInfoJson, player.getPlayerInfo());
                            equipList = player.getEquipList();
                            equips = loadItems(equipList);
                            
                            if (result)
                                log = "[" + equips.get(itemIdx)[0] + "을 장착했습니다.]";
                            else 
                                log = "[" + equips.get(itemIdx)[0] + "을 장착 해제했습니다.]";
                        }
                        else {
                            log = "[레벨이 낮아 장착할 수 없습니다.]";
                        }
                    }
                }
                else { 
                    if (itemIdx < conss.size()) {
                        String consName = conss.get(itemIdx)[0];
                        Item item = ItemUtils.getItem(consName);
                        player.useConsume(item);
                        userInfo = loadUserInfo(userInfoJson, player.getPlayerInfo());
                        consList = player.getConsList();
                        conss = loadItems(consList);
                        log = "[" + consName + "]을 사용했습니다.";
                    }
                }
            } else if (menu.equals("4")) {
                int itemIdx = 3 + page * 5;
                if (mode == 0) { 
                    if (itemIdx < equips.size()) {
                        if (player.getLevel() >= Integer.parseInt(equips.get(itemIdx)[2])) {
                            boolean result = player.equipOrRelease(itemIdx);
                            userInfo = loadUserInfo(userInfoJson, player.getPlayerInfo());
                            equipList = player.getEquipList();
                            equips = loadItems(equipList);
                            
                            if (result)
                                log = "[" + equips.get(itemIdx)[0] + "을 장착했습니다.]";
                            else 
                                log = "[" + equips.get(itemIdx)[0] + "을 장착 해제했습니다.]";
                        }
                        else {
                            log = "[레벨이 낮아 장착할 수 없습니다.]";
                        }
                    }
                }
                else { 
                    if (itemIdx < conss.size()) {
                        String consName = conss.get(itemIdx)[0];
                        Item item = ItemUtils.getItem(consName);
                        player.useConsume(item);
                        userInfo = loadUserInfo(userInfoJson, player.getPlayerInfo());
                        consList = player.getConsList();
                        conss = loadItems(consList);
                        log = "[" + consName + "]을 사용했습니다.";
                    }
                }
            } else if (menu.equals("5")) {
                int itemIdx = 4 + page * 5;
                if (mode == 0) { 
                    if (itemIdx < equips.size()) {
                        if (player.getLevel() >= Integer.parseInt(equips.get(itemIdx)[2])) {
                            boolean result = player.equipOrRelease(itemIdx);
                            userInfo = loadUserInfo(userInfoJson, player.getPlayerInfo());
                            equipList = player.getEquipList();
                            equips = loadItems(equipList);
                            
                            if (result)
                                log = "[" + equips.get(itemIdx)[0] + "을 장착했습니다.]";
                            else 
                                log = "[" + equips.get(itemIdx)[0] + "을 장착 해제했습니다.]";
                        }
                        else {
                            log = "[레벨이 낮아 장착할 수 없습니다.]";
                        }
                    }
                }
                else { 
                    if (itemIdx < conss.size()) {
                        String consName = conss.get(itemIdx)[0];
                        Item item = ItemUtils.getItem(consName);
                        player.useConsume(item);
                        userInfo = loadUserInfo(userInfoJson, player.getPlayerInfo());
                        consList = player.getConsList();
                        conss = loadItems(consList);
                        log = "[" + consName + "]을 사용했습니다.";
                    }
                }
            }
            // 장비창 <-> 소비창 전환
            else if (menu.equals("C") || menu.equals("c")) {
                mode = (mode == 0) ? 1 : 0;
                page = 0;
            } else {

            }
        }
    }

    // 장비 인벤토리 보기
    private void showEquipInven(String[] userInfo, String[] equipInfo, String[] invenSelection, String log,
            int infoMaxLen, List<String[]> equips, int page) {
        System.out.println("┏" + "━".repeat(uiWidth) + "┓");

        System.out.printf("┃%s┃\n", ConsoleUIUtils.rightText("┼" + "─".repeat(infoMaxLen) + "┼", uiWidth));
        for (int i = 0; i < userInfo.length; i++) {
            System.out.printf("┃%s┃\n",
                    ConsoleUIUtils.rightText("│" + ConsoleUIUtils.leftText(userInfo[i], infoMaxLen) + "│", uiWidth));
        }
        System.out.printf("┃%s┃\n", ConsoleUIUtils.rightText("┼" + "─".repeat(infoMaxLen) + "┼", uiWidth));

        System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(log, uiWidth));
        System.out.println("┃" + " ".repeat(uiWidth) + "┃");

        for (int i = 0; i < equipInfo.length; i++) {
            System.out.printf("┃%s┃\n", ConsoleUIUtils.leftText(equipInfo[i], uiWidth));
        }
        System.out.printf("┃%s┃\n", ConsoleUIUtils.leftText("=".repeat(50), uiWidth));

        for (int i = 0; i < 5; i++) {
            int idx = page * 5 + i;
            if (page * 5 + i < equips.size()) {
                String name = ConsoleUIUtils.leftText(equips.get(idx)[0], 15);
                String atk = ConsoleUIUtils.centerText(equips.get(idx)[1], 6);
                String reqlv = ConsoleUIUtils.centerText(equips.get(idx)[2], 8);
                String equipped = ConsoleUIUtils.centerText(equips.get(idx)[3], 5);
                String text = name + " │ " + atk + " │ " + reqlv + " │ " + equipped;

                System.out.printf("┃%s┃\n", ConsoleUIUtils.leftText(text, uiWidth));
            } else {
                String text = " ".repeat(15) + " │ " + " ".repeat(6) + " │ " + " ".repeat(8) + " │ " + " ".repeat(5);

                System.out.printf("┃%s┃\n", ConsoleUIUtils.leftText(text, uiWidth));
            }
        }

        System.out.println("┣" + "━".repeat(uiWidth) + "┫");
        for (int i = 0; i < invenSelection.length; i++) {
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(invenSelection[i], uiWidth));
        }

        System.out.println("┗" + "━".repeat(uiWidth) + "┛");
    }

    // 소비 인벤토리 보기
    private void showConsInven(String[] userInfo, String[] consInfo, String[] invenSelection, String log,
            int infoMaxLen, List<String[]> conss, int page) {
        System.out.println("┏" + "━".repeat(uiWidth) + "┓");

        System.out.printf("┃%s┃\n", ConsoleUIUtils.rightText("┼" + "─".repeat(infoMaxLen) + "┼", uiWidth));
        for (int i = 0; i < userInfo.length; i++) {
            System.out.printf("┃%s┃\n",
                    ConsoleUIUtils.rightText("│" + ConsoleUIUtils.leftText(userInfo[i], infoMaxLen) + "│", uiWidth));
        }
        System.out.printf("┃%s┃\n", ConsoleUIUtils.rightText("┼" + "─".repeat(infoMaxLen) + "┼", uiWidth));

        System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(log, uiWidth));
        System.out.println("┃" + " ".repeat(uiWidth) + "┃");

        for (int i = 0; i < consInfo.length; i++) {
            System.out.printf("┃%s┃\n", ConsoleUIUtils.leftText(consInfo[i], uiWidth));
        }
        System.out.printf("┃%s┃\n", ConsoleUIUtils.leftText("=".repeat(50), uiWidth));

        for (int i = 0; i < 5; i++) {
            int idx = page * 5 + i;
            if (page * 5 + i < conss.size()) {
                String name = ConsoleUIUtils.leftText(conss.get(idx)[0], 15);
                String atk = ConsoleUIUtils.leftText("체력 " + conss.get(idx)[1] + "회복", 16);
                String reqlv = ConsoleUIUtils.leftText(conss.get(idx)[2], 8);
                String text = name + " │ " + atk + " │ " + reqlv;

                System.out.printf("┃%s┃\n", ConsoleUIUtils.leftText(text, uiWidth));
            } else {
                String text = " ".repeat(15) + " │ " + " ".repeat(16) + " │ " + " ".repeat(8);

                System.out.printf("┃%s┃\n", ConsoleUIUtils.leftText(text, uiWidth));
            }
        }

        System.out.println("┣" + "━".repeat(uiWidth) + "┫");
        for (int i = 0; i < invenSelection.length; i++) {
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(invenSelection[i], uiWidth));
        }

        System.out.println("┗" + "━".repeat(uiWidth) + "┛");
    }

}
