package com.gyul.game.ui;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class ConsoleUIUtils {    

    private static Scanner scn = new Scanner(System.in);
    
    public static String getAnyInput(String msg) {
        System.out.print(msg);
        return scn.nextLine();
    }
    
    public static String getInputName(String msg, String errorMsg) {
        String userName;
        
        while (true) {
            System.out.print(msg);
            userName = scn.nextLine();
            
            if (userName.length() > 10 || userName.length() < 2) {
                System.out.println(errorMsg);
            }
            else {
                return userName;
            }
        }
    }
    
    public static String getInputPW(String msg, String errorMsg) {
        String userPW;
        
        while (true) {
            System.out.print(msg);
            userPW = scn.nextLine();
            
            if (userPW.length() > 20 || userPW.length() < 4) {
                System.out.println(errorMsg);
            }
            else {
                return userPW;
            }
        }
    }
    
    public static String getInputID(String msg, String errorMsg) {
        String userID;
        boolean isValid = false;
        
        while (true) {
            System.out.print(msg);
            userID = scn.nextLine();
            
            if (userID.length() > 8 || userID.length() < 2) {
                System.out.println(errorMsg);
            }
            else {
                isValid = true;
                for (char c : userID.toCharArray()) {
                    // 한글(가~힣, ㄱ~ㅎ, ㅏ~ㅣ)은 2칸으로 계산
                    if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES ||
                        Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_JAMO ||
                        Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO) {
                        System.out.println(errorMsg);
                        isValid = false;
                        break;
                    }
                }
                if (isValid)
                    return userID;
            }
        }
    }
    
    
    public static String getInputCode(String msg) {
        String menu;
        
        System.out.print(msg);
        menu = scn.nextLine();
        
        return menu;
    }
    
    public static int getIntMenu(String msg, String errorMsg) {
        int menu;
        
        while(true) {
            try {
                System.out.print(msg);
                menu = Integer.parseInt(scn.nextLine());
                return menu;
            } catch(Exception e) {
                System.out.println(errorMsg);
            }
        }
        
    }
    
    /**
     * 텍스트를 왼쪽에 정렬할 수 있도록 앞에 공백을 채운 문자열을 반환
     * 
     * @param text
     * 오른쪽 정렬할 텍스트(String)
     * 
     * @param width
     * 한줄에 차지할 글자 수(int)
     * 
     * @return
     * 텍스트의 앞에 공백이 채워진 문자열
     */
    public static String leftText(String text, int width) {
        int textLength = getTextWidth(text);
        int padding = width - textLength;
        return text + " ".repeat(padding);
    }
    
    /**
     * 텍스트를 오른쪽에 정렬할 수 있도록 앞에 공백을 채운 문자열을 반환
     * 
     * @param text
     * 오른쪽 정렬할 텍스트(String)
     * 
     * @param width
     * 한줄에 차지할 글자 수(int)
     * 
     * @return
     * 텍스트의 앞에 공백이 채워진 문자열
     */
    public static String rightText(String text, int width) {
        int textLength = getTextWidth(text);
        int padding = width - textLength;
        return " ".repeat(padding) + text;
    }
    
    /**
     * 텍스트를 가운데 정렬할 수 있도록 앞뒤 공백을 채운 문자열을 반환
     * 
     * @param text
     * 가운데 정렬할 텍스트(String)
     * 
     * @param width
     * 한줄에 차지할 글자 수(int)
     * 
     * @return
     * 텍스트의 앞 뒤에 공백이 채워진 문자열
     */
    public static String centerText(String text, int width) {
        int textLength = getTextWidth(text);
        int padding = (width - textLength) / 2;
        String result = " ".repeat(padding) + text + " ".repeat(width - textLength - padding);
        return result;
//        int pad = width - result.length();
//        return result.length() == width ? result : result.substring(0, width); 
    }

    /**
     * 텍스트가 실제로 차지하는 길이 반환
     * 한글은 2칸으로, 알파벳/숫자/특수문자는 1칸으로 계산됨
     * 
     * @param text
     * 길이를 구하고 싶은 텍스트(String)
     * 
     * @return
     * 텍스트의 실제 길이
     */
    public static int getTextWidth(String text) {
        double width = 0;
        for (char c : text.toCharArray()) {
            // 한글(가~힣, ㄱ~ㅎ, ㅏ~ㅣ)은 2칸으로 계산
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES ||
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_JAMO ||
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO) {
                width += 1.3461538461538461538461538;
            } else {
                width += 1;
            }
        }
//        System.out.println((int) width);
        return (int) width;
//        int width = 0;
//        for (char c : text.toCharArray()) {
//            if (c >= '가' && c <= '힣' || c >= 'ㄱ' && c <= 'ㅎ' || c >= 'ㅏ' && c <= 'ㅣ') { // 한글일 경우에는 글자크기 2로 계산
//                width += 1;
//            } else { // 영어, 숫자, 특수문자는 1로 계산
//                width += 1;
//            }
//        }
//        return width;
    }

    /**
     * JSON 파일을 불러와서 객체로 반환
     * 
     * @param jsonPath
     * JSON 파일의 경로
     * 
     * @return
     * 불러오기 성공 시 객체를,
     * 실패 시 null 반환
     * 
     */
    public static Map<String, Map<String, Map<String, String[]>>> readJson(String jsonPath) {
        try {
            Reader reader = new InputStreamReader(new FileInputStream(jsonPath), "UTF-8");
            Type type = new TypeToken<HashMap<String, HashMap<String, HashMap<String, String[]>>>>() {}.getType();
            Gson gson = new Gson();
            Map<String, Map<String, Map<String, String[]>>> json = gson.fromJson(reader, type);

            return json;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * String 배열에서 가장 긴 문자열의 길이 반환
     * 단, 한글은 2칸으로 계산
     * 
     * @param stringArr
     * 가장 긴 문자열의 길이를 알고 싶은 String 배열
     * 
     * @return
     * 가장 긴 문자열의 길이
     * stringArr이 null이거나 크기가 0이면 0 반환
     */
    public static int getMaxLength(String[] stringArr) {
        int length = 0;
        
        if (stringArr == null || stringArr.length == 0)
            return 0;
        
        for (String str : stringArr) {
            int tmpLen = getTextWidth(str);
            length = (length < tmpLen) ? tmpLen : length;
        }
        
        return length;
    }

    
}
