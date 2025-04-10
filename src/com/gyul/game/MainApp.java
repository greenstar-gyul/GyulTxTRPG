package com.gyul.game;

import com.gyul.game.ui.ConsoleTitleUI;

public class MainApp {
     public static final String JSON_PATH = "./src/com/gyul/resources/";
//    public static final String JSON_PATH = "D:/예담수업자료/GyulTxTRPG/GyulTxTRPG/src/com/gyul/resources/";
    
    public static void main(String[] args) {
        ConsoleTitleUI mainTitle = ConsoleTitleUI.getInstance();
        mainTitle.mainMenu();
        
        System.out.println("End Game");
    }
}
