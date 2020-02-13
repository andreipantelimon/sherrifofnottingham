package com.tema1.main;

import com.tema1.common.Constants;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.LegalGoods;
import com.tema1.player.Merchant;
import com.tema1.player.PlayerHandler;

import java.util.ArrayList;
import java.util.Collections;


public class GameMaster {
    private int startIndex = 0, finishIndex = Constants.getCardToDraw();

    public final void playTheGame(final PlayerHandler playerHandler,
                            final GameInput gameInput, final GoodsFactory goods) {
        // Every player gets to be sheriff
        Constants.addCurrentRound(1);
        for (int i = 0; i < playerHandler.getPlayersList().size(); i++) {
            playerHandler.getPlayerById(i).setSheriff();
            // Other players draw cards
            for (Merchant m : playerHandler.getPlayersList()) {
                if (!m.isSheriff()) {
                    ArrayList<Integer> tempCards;
                    tempCards = new ArrayList<Integer>(
                            gameInput.getAssetIds().subList(startIndex, finishIndex));
                    m.setCards(tempCards);
                    startIndex = finishIndex;
                    finishIndex += Constants.getCardToDraw();
                }
            }

            // Merchants declare their bags

            for (Merchant m :playerHandler.getPlayersList()) {
                if (!m.isSheriff()) {
                    m.bagDeclare(goods, playerHandler);
                }
            }

            // Sheriff verifies merchants
            for (Merchant m :playerHandler.getPlayersList()) {
                if (m.isSheriff()) {
                    m.playAsSheriff(playerHandler, gameInput);
                }
            }

            // Computing stall

            for (Merchant m : playerHandler.getPlayersList()) {
                if (!m.isSheriff()) {
                    m.computeStall(goods);
                }
            }
            playerHandler.getPlayerById(i).setMerchant();
        }
    }

    public final void computeBonus(final GoodsFactory goods, final PlayerHandler playerHandler) {
        int[][] bonus = new int[Constants.getNoOfLegalCards()][2];

        // Profit is added
        for (Merchant m : playerHandler.getPlayersList()) {
            for (Integer item : m.getAllItems()) {
                m.addCoins(goods.getGoodsById(item).getProfit());
            }
        }

        // Kings and Queens bonus is computed from a 10x2 array with the lines index
        // being the id of the good and in position being id of the player.
        // On first column kings and on the second queens.
        for (int i = 0; i < Constants.getNoOfLegalCards(); i++) {
            for (int j = 0; j < 2; j++) {
                bonus[i][j] = -1;
            }
        }

        for (int i = 0; i < Constants.getNoOfLegalCards(); i++) {
            for (Merchant m : playerHandler.getPlayersList()) {
                for (Integer item : m.getAllItems()) {
                    if (i == item && bonus[i][0] == -1) {
                        bonus[i][0] = m.getId();
                    }
                }
            }
        }

        for (int i = 0; i < Constants.getNoOfLegalCards(); i++) {
            for (Merchant m : playerHandler.getPlayersList()) {
                for (Integer item : m.getAllItems()) {
                    if (i == item) {
                        int tempFreq = playerHandler.getPlayerById(bonus[i][0]).frequency(item);
                        if (m.frequency(item) > tempFreq) {
                            bonus[i][1] = bonus[i][0];
                            bonus[i][0] = m.getId();
                        } else {
                            if (bonus[i][1] == -1 && m.getId() != bonus[i][0]) {
                                bonus[i][1] = m.getId();
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < Constants.getNoOfLegalCards(); i++) {
            for (Merchant m : playerHandler.getPlayersList()) {
                for (Integer item : m.getAllItems()) {
                    if (i == item && m.getId() != bonus[i][0]) {
                        int tempFreq = playerHandler.getPlayerById(bonus[i][1]).frequency(item);
                        if (m.frequency(item) > tempFreq) {
                            bonus[i][1] = m.getId();
                        }
                    }
                }
            }
        }

        // Bonus is adeed
        for (int i = 0; i < Constants.getNoOfLegalCards(); i++) {
            if (bonus[i][0] != -1) {
                int tempKingBonus = ((LegalGoods) goods.getGoodsById(i)).getKingBonus();
                playerHandler.getPlayerById(bonus[i][0]).addCoins(tempKingBonus);
            }
            if (bonus[i][1] != -1) {
                int tempQueenBonus = ((LegalGoods) goods.getGoodsById(i)).getQueenBonus();
                playerHandler.getPlayerById(bonus[i][1]).addCoins(tempQueenBonus);
            }
        }
    }

    public final void printScoreboard(final PlayerHandler playerHandler,
                                      final GameInput gameInput) {
        Collections.sort(playerHandler.getPlayersList(), Merchant::compareTo);
        for (int i = 0; i < gameInput.getPlayerNames().size(); i++) {
            System.out.println(playerHandler.getPlayersList().get(i));
        }
    }


}
