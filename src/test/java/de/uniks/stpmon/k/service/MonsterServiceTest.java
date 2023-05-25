package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.models.Monster;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class MonsterServiceTest{
    @InjectMocks
    private MonsterService monsterService;

    @Test
    void testMonsterService() {
        // Create new monsters
        Monster monster1 = new Monster("1", null, null, null, null, null, null, null);
        Monster monster2 = new Monster("2", null, null, null, null, null, null, null);

        // Add monsters to the service
        monsterService.addMonster(monster1);
        monsterService.addMonster(monster2);

        // Check the number of monsters in the service
        assertEquals(monster1, monsterService.getMonster("1"));
        assertEquals(monster2, monsterService.getMonster("2"));

        // Check the number of monsters in the list
        assertEquals(2, monsterService.getMonsterList());

        // Remove a monster from the service
        monsterService.removeMonster("1");

        // Check if the monster has been removed and the number of remaining monsters
        assertNull(monsterService.getMonster("1"));
        assertEquals(1, monsterService.getMonsterList());
    }
}
