package com.gyul.game.ui;

/*
 * UI가 어떻게 보이는지 테스트하기 위한 클래스!
 * 실제 메인 메소드가 아님--
 */

class Test {
    int code;
    String name;
    
    Test() {
        code = 10;
        name = "Test";
    }
    
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return code;
    }
}

public class ConsoleUITestMain {
    public static void main(String[] args) {
//        ConsoleTitleUI test = new ConsoleTitleUI();
//        Scanner scn = new Scanner(System.in);
//        test.loginComplete("abcdefghij");
//        System.out.println();
//        test.loginComplete("ABCDEFGHIJ");
//        System.out.println();
//        test.loginComplete("가나다라마바사아자차");
//        System.out.println();
//        test.loginComplete("ab");
//        
         // test.mainMenu();
        // String input = scn.nextLine();
        // test.loginComplete("모험가하하하하하하하");

//        try {
//            Reader reader = new InputStreamReader(new FileInputStream("GyulTxTRPG/src/resources/menu.json"), "UTF-8");
//            Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
//            Gson gson = new Gson();
//            Map<String, Map<String, String>> json = gson.fromJson(reader, type);
//
//            String title = json.get("메인메뉴").get("제목");
//            String text = json.get("메인메뉴").get("안내문구");
//            String menu = json.get("메인메뉴").get("메뉴");
//            test.mainMenu(title, text, menu);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // json = 

//        System.out.println();
//        test.registerMenu();
//        System.out.println();
//        test.registerNickMenu();
//        System.out.println();
//        test.loginComplete("알아서뭐해알아서뭐해");
//        System.out.println();
//        test.loadGameMenu();
        
//        test.mainMenu();
//        System.out.println();
//        test.registerMenu();
//        System.out.println();
//        test.registerNickMenu();
//        System.out.println();
//        test.loginComplete("대충열글자닉네임이다");
//        System.out.println();
//        test.loadGameMenu();
        
//        Map<Test, Integer> testing = new HashMap<>();
//        testing.put(new Test(), 1);
//        testing.replace(new Test(), testing.get(new Test()) + 1);
//        System.out.println(testing.get(new Test()));
        
//        List<Test> t = new ArrayList<>();
//        t.add(new Test());
//        System.out.println(t.get(0).code);
//        for (Test a : t)
//            a.code = 1;
//        System.out.println(t.get(0).code);
        
    //    // 인벤토리 테스트
    //    Player p1 = new Player("홍길동", 50, 10, 1, "aaa", "검사", 0, 10);
    //    ConsoleGameUI uiTest = new ConsoleGameUI(p1);

    //    // p1.showInventory();
    //    uiTest.inventory();
    // //    uiTest.gameStart();
        
        System.out.println("Test End");
    }
}
