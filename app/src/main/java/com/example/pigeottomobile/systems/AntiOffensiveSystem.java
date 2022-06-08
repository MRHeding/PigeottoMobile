package com.example.pigeottomobile.systems;

public class AntiOffensiveSystem {
    final private String[] offensiveWords =
            {"nigger", "nigga", "idiot", "asshole", "noob", "dumb",
             "idiota", "debil", "konfident", "głupek", "ciota"};
    final private String[] vulgarWords =
            {"fuck", "cock", "bitch",
            "kurw", "pierdol", "jebać"};

    public boolean isTextOffensiveOrVulgar(String text){
        for (String s : offensiveWords){
            if (text.contains(s))
                return true;
        }
        for (String s : vulgarWords){
            if (text.contains(s))
                return true;
        }
        return false;
    }
}
