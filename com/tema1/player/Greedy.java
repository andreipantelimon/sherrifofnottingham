package com.tema1.player;

import com.tema1.common.Constants;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;
import com.tema1.main.GameInput;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Greedy extends Merchant {
    private String type = "GREEDY";

    public Greedy(final int id) {
        super(id);
    }

    /**
     * Bag declaration for greedy player.
     * @param goods
     * @param playerHandler
     */
    public void bagDeclare(final GoodsFactory goods, final PlayerHandler playerHandler) {
        this.briber.clear();
        if (Constants.getCurrentRound() % 2 == 0) {
            super.bagDeclare(goods, playerHandler);
            if (this.bag.getTotalCount() < Constants.getMaxBagSize()) {
                if (getExpensiveIllegal(goods) != -1) {
                    if (bag.getBagOfCards().size() == 0) {
                        bag.setDeclaredAs(0);
                        bag.setIllegal(true);
                        bag.getBagOfCards().put(goods.getGoodsById(getExpensiveIllegal(goods)), 1);
                        bag.addCount(1);
                    } else {
                        bag.setIllegal(true);
                        if (bag.getBagOfCards().get(goods.getGoodsById(
                                getExpensiveIllegal(goods))) != null) {
                            int tempCount = bag.getBagOfCards().get(
                                    goods.getGoodsById(getExpensiveIllegal(goods)));
                            bag.getBagOfCards().put(goods.getGoodsById(
                                    getExpensiveIllegal(goods)), tempCount + 1);
                        } else {
                            bag.getBagOfCards().put(goods.getGoodsById(
                                    getExpensiveIllegal(goods)), 1);
                        }
                        bag.addCount(1);
                    }
                }
            }
        } else {
            super.bagDeclare(goods, playerHandler);
        }
    }

    /**
     * Greedy player plays as sheriff.
     * @param playerHandler
     * @param gameInput
     */
    public void playAsSheriff(final PlayerHandler playerHandler, final GameInput gameInput) {
        ArrayList<Goods> toRemove = new ArrayList<Goods>();
        for (Merchant m : playerHandler.getPlayersList()) {
            if (!m.isSheriff()) {
                int ok = 0;
                for (Integer tempBriber : this.briber) {
                    if (m.getId() == tempBriber) {
                        ok = 1;
                        break;
                    }
                }
                if (ok == 0) {
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
                } else {
                    Iterator it = m.bag.getBagOfCards().entrySet().iterator();
                    while (it.hasNext()) {
                        // Pair is an object from the bag
                        Map.Entry pair = (Map.Entry) it.next();
                        Goods tempGood = (Goods) pair.getKey();
                        Integer tempCount = (Integer) pair.getValue();


                        for (int i = 0; i < tempCount; i++) {
                            m.tempStall.add(tempGood.getId());
                        }
                    }
                    this.addCoins(m.getBribe());
                    m.removeCoins(m.getBribe());
                }
            }
        }
    }

    /**
     * Getter for greedy type player.
     * @return String "GREEDY"
     */
    public String getType() {
        return this.type;
    }

    /**
     * To string for greedy player for table.
     * @return String entry for greedy in table
     */
    public String toString() {
        return this.id + " " + this.type + " " + this.coins;
    }

    private int getExpensiveIllegal(final GoodsFactory goods) {
        int maxProfitItem = -1;
        for (Integer item : this.cards) {
            if (goods.getGoodsById(item).getType() == GoodsType.Illegal) {
                maxProfitItem = item;
                break;
            }
        }
        for (Integer item : this.cards) {
            if (goods.getGoodsById(item).getType() == GoodsType.Illegal) {
                int tempProfit = goods.getGoodsById(item).getProfit();
                if (tempProfit > goods.getGoodsById(maxProfitItem).getProfit()) {
                    maxProfitItem = item;
                }
            }
        }
        return maxProfitItem;
    }
}
