package com.gyul.game.ui;

import java.util.Map;

import com.gyul.game.MainApp;
import com.gyul.game.entity.EntityDao;
import com.gyul.game.entity.Player;

public class ConsoleTitleUI {
    // 가로 72칸, 세로 18줄
    private int uiWidth = 70;                            // 실제 텍스트 공간(너비)
    private int uiHeight = 20;                           // 실제 텍스트 공간(높이)
    private Map<String, Map<String, Map<String, String[]>>> menuJson;
    
    
    
    private static ConsoleTitleUI mainTitle = new ConsoleTitleUI();
    
    private ConsoleDao cDao = new ConsoleDao();
    private EntityDao eDao = new EntityDao();
    
    private ConsoleTitleUI() { initialize(); }
    
    public static ConsoleTitleUI getInstance() {
        return mainTitle;
    }
    
    private void initialize() {
        if (menuJson == null) {
            menuJson = ConsoleUIUtils.readJson(MainApp.JSON_PATH + "menu.json");
        }
    }
    
    public void mainMenu() {        
        String[] mainTitles = menuJson.get("타이틀").get("메인메뉴").get("제목");
        String[] mainContents = menuJson.get("타이틀").get("메인메뉴").get("안내문구");
        String[] mainSelections = menuJson.get("타이틀").get("메인메뉴").get("메뉴");
        
        int paddingHeight = uiHeight - (2 + 1)                  // 타이틀 감싸는 블럭 높이 + 타이틀 윗여백 1줄
                                     - mainTitles.length        // 타이틀
                                     - mainContents.length      // 메인 텍스트
                                     - 1                        // 메인 메뉴 윗여백 1줄
                                     - mainSelections.length    // 메인 메뉴
                                     - 1;                       // 메뉴 구분 선
        
        int titleMaxLen = ConsoleUIUtils.getMaxLength(mainTitles) + 2;   // 타이틀 박스 크기 계산용

        int menu = 0;
        while (true) {
            switch (menu) {
            case 1: // 처음부터
                
                registerMenu();
                mainMenuUI(titleMaxLen, mainTitles, mainContents, mainSelections, paddingHeight);
                break;
            case 2: // 이어하기(불러오기)
                
                loadGameMenu();
                mainMenuUI(titleMaxLen, mainTitles, mainContents, mainSelections, paddingHeight);
                break;
            case 9: // 게임종료
                
                // 게임종료 UI 불러오기
                return;
            default:
                mainMenuUI(titleMaxLen, mainTitles, mainContents, mainSelections, paddingHeight);
                break;
            }
            menu = ConsoleUIUtils.getIntMenu("메뉴 입력 >> ", "다시 입력해주세요.");
        }
        // 메뉴 입력 메소드 호출 위치
        
    }
    
    private void mainMenuUI(int titleMaxLen, String[] mainTitles, String[] mainContents, String[] mainSelections, int paddingHeight) {
        System.out.println("┏" + "━".repeat(uiWidth) + "┓");
        System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        
        System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText("┼" + "─".repeat(titleMaxLen) + "┼", uiWidth));
        for (int i = 0; i < mainTitles.length; i++) {
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText("│" + ConsoleUIUtils.centerText(mainTitles[i], titleMaxLen) + "│", uiWidth));
        }
        System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText("┼" + "─".repeat(titleMaxLen) + "┼", uiWidth));
        
        for (int i = 0; i < paddingHeight; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        
        for (int i = 0; i < mainContents.length; i++) {
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(mainContents[i], uiWidth));
        }
        
        System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        
        System.out.println("┣" + "━".repeat(uiWidth) + "┫");
        for (int i = 0; i < mainSelections.length; i++) {
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(mainSelections[i], uiWidth));
        }
        System.out.println("┗" + "━".repeat(uiWidth) + "┛");
    }
    
    // 처음부터 메뉴 선택했을 때, 아이디 생성
    private void registerMenu() {
        String[] regiContents = menuJson.get("타이틀").get("유저등록").get("계정문구");
        String[] regiRules = menuJson.get("타이틀").get("유저등록").get("계정규칙");
        
        int menuPaddingHeight = uiHeight - 3                     // 구분선 위아래 패딩
                                         - regiContents.length   // 메인 텍스트
                                         - regiRules.length;     // 계정 규칙
        
        int menuPaddingTop = (menuPaddingHeight % 2 == 0) ? (menuPaddingHeight / 2) : (menuPaddingHeight / 2 + 1);
        int menuPaddingBottom = menuPaddingHeight / 2;
        
        System.out.println("┏" + "━".repeat(uiWidth) + "┓");
        
        for (int i = 0; i < menuPaddingTop; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        for (int i = 0; i < regiContents.length; i++) {            
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(regiContents[i], uiWidth));
        }
        
        System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText("=".repeat(50), uiWidth));
        System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        
        for (int i = 0; i < regiRules.length; i++) {            
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(regiRules[i], uiWidth));
        }
        
        for (int i = 0; i < menuPaddingBottom; i++) {            
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        System.out.println("┗" + "━".repeat(uiWidth) + "┛");
        
        String userID = "";
        boolean isDupl = true;
        while (isDupl) {
            userID = ConsoleUIUtils.getInputID("ID 입력 >> ", "ID는 2글자 이상 8글자 이하의 영문자나 숫자여야합니다.");

            isDupl = cDao.findID(userID);
            if (isDupl) {
                System.out.println("이미 존재하는 ID입니다.\n다른 ID를 입력해주세요.");
            }
        }
        String userPW = ConsoleUIUtils.getInputPW("PW 입력 >> ", "PW는 4글자 이상 20글자 이하여야합니다.");
        String userName = registerNickMenu();
        
        String userClass = selectClassMenu();
        
        boolean isComplete = cDao.registUser(userID, userPW, userName, userClass);
        if (isComplete) {
            loginComplete(userID, userName);
        }
    }

    // 아이디와 비밀번호 생성 후 닉네임 입력
    private String registerNickMenu() {
        String[] regiContents = menuJson.get("타이틀").get("유저등록").get("이름문구");
        String[] regiRules = menuJson.get("타이틀").get("유저등록").get("이름규칙");
        
        int menuPaddingHeight = uiHeight - 3                     // 구분선 위아래 패딩
                                         - regiContents.length   // 메인 텍스트
                                         - regiRules.length;     // 계정 규칙
        
        int menuPaddingTop = (menuPaddingHeight % 2 == 0) ? (menuPaddingHeight / 2) : (menuPaddingHeight / 2 + 1);
        int menuPaddingBottom = menuPaddingHeight / 2;
        
        System.out.println("┏" + "━".repeat(uiWidth) + "┓");
        
        for (int i = 0; i < menuPaddingTop; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        for (int i = 0; i< regiContents.length; i++) {            
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(regiContents[i], uiWidth));
        }
        
        System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText("=".repeat(50), uiWidth));
        System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        
        for (int i = 0; i< regiRules.length; i++) {            
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(regiRules[i], uiWidth));
        }
        
        for (int i = 0; i < menuPaddingBottom; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        System.out.println("┗" + "━".repeat(uiWidth) + "┛");
        
        String userName = ConsoleUIUtils.getInputName("이름 입력 >> ", "이름은 2글자 이상 10글자 이하여야합니다.");
        return userName;
    }
    
    private String selectClassMenu() {
        String[] regiContents = menuJson.get("타이틀").get("유저등록").get("직업문구");
        String[] regiRules = menuJson.get("타이틀").get("유저등록").get("직업목록");
        
        int menuPaddingHeight = uiHeight - 3                     // 구분선 위아래 패딩
                                         - regiContents.length   // 메인 텍스트
                                         - regiRules.length;     // 계정 규칙
        
        int menuPaddingTop = (menuPaddingHeight % 2 == 0) ? (menuPaddingHeight / 2) : (menuPaddingHeight / 2 + 1);
        int menuPaddingBottom = menuPaddingHeight / 2;
        
        System.out.println("┏" + "━".repeat(uiWidth) + "┓");
        
        for (int i = 0; i < menuPaddingTop; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        for (int i = 0; i< regiContents.length; i++) {            
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(regiContents[i], uiWidth));
        }
        
        System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText("=".repeat(50), uiWidth));
        System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        
        for (int i = 0; i< regiRules.length; i++) {            
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(regiRules[i], uiWidth));
        }
        
        for (int i = 0; i < menuPaddingBottom; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        System.out.println("┗" + "━".repeat(uiWidth) + "┛");
        
        int select = ConsoleUIUtils.getIntMenu("직업 선택 >> ", "반드시 숫자를 입력해주세요!");
        while (select < 1 || select > regiRules.length) {
            System.out.println("다시 입력하세요!!");
            select = ConsoleUIUtils.getIntMenu("직업 선택 >> ", "반드시 숫자를 입력해주세요!");
        }
        
        String job = "";
        switch(select) {
        case 1:
            job = "검사";
            break;
        case 2:
            job = "궁수";
            break;
        }
        
        return job;
    }

    private void loginComplete(String userID, String nick) {
        String[] loginContents = menuJson.get("타이틀").get("로그인완료").get("문구");
        
        int menuPaddingHeight = uiHeight - loginContents.length;    // 문구
        int menuPaddingTop = (menuPaddingHeight % 2 == 0) ? (menuPaddingHeight / 2) : (menuPaddingHeight / 2 + 1);
        int menuPaddingBottom = menuPaddingHeight / 2;

        loginContents[0] = String.format(loginContents[0], nick);
        
        System.out.println("┏" + "━".repeat(uiWidth) + "┓");
        
        for (int i = 0; i < menuPaddingTop; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        for (int i = 0; i < loginContents.length; i++) {            
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(loginContents[i], uiWidth));
        }
        
        for (int i = 0; i < menuPaddingBottom; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        System.out.println("┗" + "━".repeat(uiWidth) + "┛");
        ConsoleUIUtils.getAnyInput("시작하려면 아무 키 입력 >> ");
        startMainLoop(userID);
    }
    
    private void loadGameMenu() {
        String[] loadContents = menuJson.get("타이틀").get("불러오기").get("안내문구");
        String[] loginErrors = menuJson.get("타이틀").get("오류").get("로그인");
        
        int menuPaddingHeight = uiHeight - loadContents.length;    // 문구
        int menuPaddingTop = (menuPaddingHeight % 2 == 0) ? (menuPaddingHeight / 2) : (menuPaddingHeight / 2 + 1);
        int menuPaddingBottom = menuPaddingHeight / 2;
        
        System.out.println("┏" + "━".repeat(uiWidth) + "┓");
        
        for (int i = 0; i < menuPaddingTop; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        for (int i = 0; i < loadContents.length; i++) {            
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(loadContents[i], uiWidth));
        }
        
        for (int i = 0; i < menuPaddingBottom; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        System.out.println("┗" + "━".repeat(uiWidth) + "┛");
        
        String userID = ConsoleUIUtils.getInputID("ID 입력 >> ", "ID는 2글자 이상 8글자 이하의 영문자나 숫자여야합니다.");
        String userPW = ConsoleUIUtils.getInputPW("PW 입력 >> ", "PW는 4글자 이상 20글자 이하입니다.");
        
        String userName = cDao.login(userID, userPW);
        if (userName != null) {
            
            loginComplete(userID, userName);
        }
        else {
            showErrorUI(loginErrors);
            ConsoleUIUtils.getAnyInput("타이틀로 돌아가려면 아무 키 입력 >> ");
        }
        
    }
    
    private void showErrorUI(String[] errorMsgs) {
        int menuPaddingHeight = uiHeight - errorMsgs.length;    // 문구
        int menuPaddingTop = (menuPaddingHeight % 2 == 0) ? (menuPaddingHeight / 2) : (menuPaddingHeight / 2 + 1);
        int menuPaddingBottom = menuPaddingHeight / 2;
        
        System.out.println("┏" + "━".repeat(uiWidth) + "┓");
        
        for (int i = 0; i < menuPaddingTop; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        for (int i = 0; i < errorMsgs.length; i++) {            
            System.out.printf("┃%s┃\n", ConsoleUIUtils.centerText(errorMsgs[i], uiWidth));
        }
        
        for (int i = 0; i < menuPaddingBottom; i++) {
            System.out.println("┃" + " ".repeat(uiWidth) + "┃");
        }
        
        System.out.println("┗" + "━".repeat(uiWidth) + "┛");
    }
    
    // 게임 내 진행 정보 UI
    private void startMainLoop(String userID) {
        Player player = eDao.loadPlayer(userID);
        
        ConsoleGameUI mainGame = new ConsoleGameUI(player);
        mainGame.gameStart();
    }
    
}
