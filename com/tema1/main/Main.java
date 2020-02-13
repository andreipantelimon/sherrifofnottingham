package com.tema1.main;

import com.tema1.goods.GoodsFactory;
import com.tema1.player.PlayerHandler;

public final class Main {
    private Main() {
        // just to trick checkstyle
    }


    public static void main(final String[] args) {
        GameInputLoader gameInputLoader = new GameInputLoader(args[0], args[1]);
        GameInput gameInput = gameInputLoader.load();

        PlayerHandler playerHandler = new PlayerHandler(gameInput);
        GameMaster gameMaster = new GameMaster();

        GoodsFactory goods = GoodsFactory.getInstance();

        for (int i = 0; i < gameInput.getRounds(); i++) {
            gameMaster.playTheGame(playerHandler, gameInput, goods);
        }

        gameMaster.computeBonus(goods, playerHandler);

        gameMaster.printScoreboard(playerHandler, gameInput);

    }
}
