package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.controller.sidebar.HybridController;
import de.uniks.stpmon.k.controller.sidebar.MainWindow;
import de.uniks.stpmon.k.models.EncounterSlot;
import de.uniks.stpmon.k.models.Opponent;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class BattleLogService {
    @Inject
    SessionService sessionService;

    VBox textBox;


    private final Map<EncounterSlot, Opponent> lastOpponents = new HashMap<>();
    private final List<Map.Entry<EncounterSlot, Opponent>> opponentUpdates = new ArrayList<>();

    private boolean encounterIsOver = false;

    @Inject
    public BattleLogService(){

    }

    /**
     * This method is called by the listener in BattleLogController every time a update comes in
     * @param slot
     * @param opp
     */
    public void queueUpdate(EncounterSlot slot, Opponent opp){
        // Skip first value because it is always the existing value
        if (!lastOpponents.containsKey(slot)) {
            // Cache the first value
            lastOpponents.put(slot, opp);
            return;
        }
        queueOpponent(slot, opp);
        lastOpponents.put(slot, opp);
    }

    /**
     *  Queues the updated opponents, so the user can click through the actions that happen in the encounter
     * @param slot
     * @param opp
     */
    private void queueOpponent(EncounterSlot slot, Opponent opp){
        if (sessionService.hasNoEncounter()) {
            return;
        }
        opponentUpdates.add(new AbstractMap.SimpleEntry<>(slot, opp));
        //check if this battleLog needs to start
        if(textBox.getChildren().size() == 0){
            //shows next actions
            showActions();
        }
    }

    private void showActions(){
        //check if more actions need to be handled
        if(moreActions()){

        }else{
            //check if round or encounter is over
            if(encounterIsOver){

            }
        }
    }

    private boolean moreActions(){

    }

    public void setVBox(VBox vBox){
        this.textBox = vBox;
    }

    public void clearService(){
        lastOpponents.clear();
    }
}
