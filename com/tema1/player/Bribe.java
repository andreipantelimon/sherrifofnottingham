package com.tema1.player;

import com.tema1.common.Constants;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;
import com.tema1.main.GameInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class Bribe extends Merchant {

    private String type = "BRIBED";

    public Bribe(final int id) {
        super(id);
    }

    /**
     * Gets the type for bribed player.
     * @return String "BRIBED"
     */
    public String getType() {
        return this.type;
    }

    /**
     * To string for bribed player.
     * @return Bribed player entry in table.
     */
    public String toString() {
        return this.id + " " + this.type + " " + this.coins;
    }

    /**
     * Bribed player declares the bag.
     * @param goods
     * @param playerHandler
     */
    public void bagDeclare(final GoodsFactory goods, final PlayerHandler playerHandler) {
        boolean allLegal = true;
        this.bag = new BagOfCards();
        this.briber.clear();
        for (Integer card : this.cards) {
            if (!goods.getGoodsById(card).getType().equals(GoodsType.Legal)) {
                allLegal = false;
                break;
            }
        }
        if (allLegal || this.coins <= Constants.getLowBribe()) {
            super.bagDeclare(goods, playerHandler);
        } else {
            bribeStrategy(goods, playerHandler);
            bag.setDeclaredAs(0);
        }
    }

    /**
     * Bribed player plays as sheriff.
     * @param playerHandler
     * @param gameInput
     */
    public void playAsSheriff(final PlayerHandler playerHandler, final GameInput gameInput) {
        int leftPlayerId = -1, rightPlayerId = -1;
        if (gameInput.getPlayerNames().size() == 2) {
            if (this.getId() - 1 < 0) {
                leftPlayerId = this.getId() + 1;
            }
            if (this.getId() - 1 >= 0) {
                leftPlayerId = this.getId() - 1;
            }
            this.verifyPlayer(leftPlayerId, playerHandler, gameInput);
        } else {
            if (this.getId() - 1 < 0) {
                leftPlayerId = gameInput.getPlayerNames().size() - 1;
                if (this.getId() + 1 > gameInput.getPlayerNames().size() - 1) {
                    rightPlayerId = 0;
                } else {
                    rightPlayerId = this.getId() + 1;
                }
                this.verifyPlayer(leftPlayerId, playerHandler, gameInput);
                this.verifyPlayer(rightPlayerId, playerHandler, gameInput);
            } else {
                leftPlayerId = this.getId() - 1;
                this.verifyPlayer(leftPlayerId, playerHandler, gameInput);
                if (this.getId() + 1 > gameInput.getPlayerNames().size() - 1) {
                    rightPlayerId = 0;
                    this.verifyPlayer(rightPlayerId, playerHandler, gameInput);
                } else {
                    rightPlayerId = this.getId() + 1;
                    this.verifyPlayer(rightPlayerId, playerHandler, gameInput);
                }
            }
        }
        for (Merchant m : playerHandler.getPlayersList()) {
            if (!m.isSheriff() && m.getId() != leftPlayerId && m.getId() != rightPlayerId) {
                Iterator it = m.bag.getBagOfCards().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    Goods tempGood = (Goods) pair.getKey();
                    Integer tempCount = (Integer) pair.getValue();
                    for (int i = 0; i < tempCount; i++) {
                        m.tempStall.add(tempGood.getId());
                    }
                }
            }
        }
        // Adds the bribe
        for (Merchant m : playerHandler.getPlayersList()) {
            for (Integer briberPlayer : this.briber) {
                if (m.getId() == briberPlayer
                        && m.getId() != leftPlayerId && m.getId() != rightPlayerId) {
                    if (m.getBribe() != 0) {
                        this.addCoins(m.getBribe());
                        m.removeCoins(m.getBribe());
                    }
                }
            }
        }
    }

    private void verifyPlayer(final int id, final PlayerHandler playerHandler,
                              final GameInput gameInput) {
        ArrayList<Goods> toRemove = new ArrayList<Goods>();
        for (Merchant m : playerHandler.getPlayersList()) {
            if (m.getId() == id) {
                Iterator it = m.bag.getBagOfCards().entrySet().iterator();
                while (it.hasNext()) {
                    // Pair is an object from the bag
                    Map.Entry pair = (Map.Entry) it.next();
                    Goods tempGood = (Goods) pair.getKey();
                    Integer tempCount = (Integer) pair.getValue();

                    if (this.getCoins() >= Constants.getMinimumCoinsToVerify()) {
                        if (tempGood.getId() == m.bag.getDeclaredAs()) {
                            if (!m.bag.isIllegal()) {
                                this.removeCoins(tempGood.getPenalty() * tempCount);
                                m.addCoins(tempGood.getPenalty() * tempCount);
                            }
                            for (int i = 0; i < tempCount; i++) {
                                m.tempStall.add(tempGood.getId());
                            }
                        } else {
                            // Seizes the item
                            this.addCoins(tempGood.getPenalty() * tempCount);
                            m.removeCoins(tempGood.getPenalty() * tempCount);
                            for (int i = 0; i < tempCount; i++) {
                                gameInput.addSeized(tempGood.getId());
                                toRemove.add(tempGood);
                            }
                        }
                    } else {
                        for (int i = 0; i < tempCount; i++) {
                            m.tempStall.add(tempGood.getId());
                        }
                    }
                }
                for (Goods good : toRemove) {
                    if (good != null) {
                        m.bag.getBagOfCards().remove(good);
                    }
                }
            }
        }
    }

    private void bribeStrategy(final GoodsFactory goods, final PlayerHandler playerHandler) {
        Collections.sort(this.cards, new BasicCompare(goods));
        int bribe;
        int tempCoins = this.coins;

        for (Integer card : this.cards) {
            if (this.bag.getTotalCount() < Constants.getMaxBagSize()) {
                if (goods.getGoodsById(card).getType().equals(GoodsType.Illegal)) {
                    if (this.coins > Constants.getLowBribe()
                            && bag.getIllegalCount() <= 2
                            && tempCoins > Constants.getIllegalPenalty()) {
                        this.bagAdd(goods, card);
                        bag.addIllegalCount(1);
                        bag.setIllegal(true);
                        tempCoins -= Constants.getIllegalPenalty();
                        continue;
                    }
                    if (this.coins > Constants.getHighBribe()
                            && bag.getIllegalCount() > 2
                            && tempCoins > Constants.getIllegalPenalty()) {
                        this.bagAdd(goods, card);
                        bag.addIllegalCount(1);
                        bag.setIllegal(true);
                        tempCoins -= Constants.getIllegalPenalty();
                    }
                }
                if (goods.getGoodsById(card).getType().equals(GoodsType.Legal)) {
                    if (tempCoins > Constants.getLegalPenalty()) {
                        this.bagAdd(goods, card);
                        this.bag.addLegalCount(1);
                        tempCoins -= Constants.getLegalPenalty();
                    }
                }
            }
        }
        if (this.bag.getIllegalCount() <= 2 && this.bag.getIllegalCount() != 0) {
            bribe = Constants.getLowBribe();
        } else {
            bribe = Constants.getHighBribe();
        }
        this.bribe = bribe;
        for (Merchant m : playerHandler.getPlayersList()) {
            if (m.isSheriff() && this.bag.isIllegal()) {
                if (m.getType().equals("GREEDY") || m.getType().equals("BRIBED")) {
                    m.briber.add(this.getId());
                }
            }
        }
    }

    private void bagAdd(final GoodsFactory goods, final int card) {
        boolean ok = false;
        Iterator it = this.bag.getBagOfCards().entrySet().iterator();
        while (it.hasNext() && !ok) {
            // Pair is an object from the bag
            Map.Entry pair = (Map.Entry) it.next();
            Goods tempGood = (Goods) pair.getKey();
            Integer tempCount = (Integer) pair.getValue();
            if (goods.getGoodsById(card).equals(tempGood)) {
                bag.getBagOfCards().put(goods.getGoodsById(card), tempCount + 1);
                bag.addCount(1);
                ok = true;
            }
        }
        if (!ok) {
            bag.getBagOfCards().put(goods.getGoodsById(card), 1);
            bag.addCount(1);
        }
    }
}
