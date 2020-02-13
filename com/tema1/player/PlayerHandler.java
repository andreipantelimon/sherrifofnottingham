package com.tema1.player;

import com.tema1.main.GameInput;

import java.util.ArrayList;

public class PlayerHandler {
    private ArrayList<Merchant> playersList = new ArrayList<Merchant>();

    public PlayerHandler(final GameInput gameInput) {
        int tempId = 0;

        //Declares and adds the players to the array playersList
        for (int i = 0; i < gameInput.getPlayerNames().size(); i++) {
            if (gameInput.getPlayerNames().get(i).equals("basic")) {
                playersList.add(new Merchant(tempId));
                tempId++;
            }
            if (gameInput.getPlayerNames().get(i).equals("bribed")) {
                playersList.add(new Bribe(tempId));
                tempId++;
            }
            if (gameInput.getPlayerNames().get(i).equals("greedy")) {
                playersList.add(new Greedy(tempId));
                tempId++;
            }
        }
    }

    public final ArrayList<Merchant> getPlayersList() {
        return this.playersList;
    }

    public final  Merchant getPlayerById(final int id) {
        for (Merchant m : playersList) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }
}
