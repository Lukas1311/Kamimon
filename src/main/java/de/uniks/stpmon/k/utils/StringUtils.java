package de.uniks.stpmon.k.utils;

public class StringUtils {

    public static String filterChatName(String groupName, String myUsername) {
        String option1 = myUsername + " + ";
        String option2 = " + " + myUsername;

        if (groupName.startsWith(option1)) {
            return groupName.substring(option1.length());
        }

        if (groupName.endsWith(option2)) {
            return groupName.substring(0, groupName.length() - option2.length());
        }

        return groupName;
    }


}
