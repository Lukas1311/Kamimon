package de.uniks.stpmon.k.controller.chat;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static de.uniks.stpmon.k.utils.StringUtils.filterChatName;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class StringUtilsTest {

    @Test
    public void testReplacements() {
        String case1 = "Alice + Bob";
        String case2 = "Bob + Alice";
        String case3 = "KGM Groupchat";

        assertEquals("Bob", filterChatName(case1, "Alice"));
        assertEquals("Bob", filterChatName(case2, "Alice"));
        assertEquals(case3, filterChatName(case3, "Alice"));


    }

}
