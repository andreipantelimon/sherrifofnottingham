package com.tema1.player;

import com.tema1.common.Constants;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;
import com.tema1.goods.IllegalGoods;
import com.tema1.main.GameInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;


/**
 * Clasa Playerului Basic.
 * @return a basic player
 */
public class Merchant {
    protected boolean isSheriff = false;
    protected int id = -1;
    protected int coins = Constants.getStartingCoins();
    protected int bribe = 0;
    private String type = "BASIC";
    protected ArrayList<Integer> cards;
    protected BagOfCards bag;
    protected ArrayList<Integer> tempStall;
    protected ArrayList<Integer> allItems;
    protected ArrayList<Integer> briber;

    public Merchant(final int id) {
        this.id = id;
        tempStall = new ArrayList<Integer>();
        allItems = new ArrayList<Integer>();
        briber = new ArrayList<Integer>();
    }

    /**
     * Bag declaration for basic player.
     * @param goods
     * @param playerHandler
     */
    public void bagDeclare(final GoodsFactory goods, final PlayerHandler playerHandler) {
        boolean allIllegal = true;
        this.bag = new BagOfCards();
        this.briber.clear();
        this.tempStall.clear();
        for (Integer card : this.cards) {
            if (!goods.getGoodsById(card).getType().equals(GoodsType.Illegal)) {
                allIllegal = false;
                break;
            }
        }

        if (allIllegal) {
            this.declareAsIllegal(goods);
        } else {
            this.declareAsLegal(goods);
        }
    }

    private void declareAsIllegal(final GoodsFactory goods) {
        // Finds the most expensive illegal card and his index
        int maxValue = Integer.MIN_VALUE, maxIndex = 0;
        for (Integer card : this.cards) {
            if (goods.getGoodsById(card).getProfit() > maxValue) {
                maxValue = goods.getGoodsById(card).getProfit();
                maxIndex = card;
            }
        }
        for (Integer card : this.cards) {
            if (card == maxIndex) {
                this.cards.remove(card);
                break;
            }
        }
        bag.setDeclaredAs(0);
        bag.setIllegal(true);
        bag.getBagOfCards().put(goods.getGoodsById(maxIndex), 1);
        bag.addCount(1);
    }

    private void declareAsLegal(final GoodsFactory goods) {
        // Finds the most frequent element and counts the number of apparitions
        ArrayList<Integer> legalCards = new ArrayList<Integer>();
        for (int i = 0; i < this.cards.size(); i++) {
            if (goods.getGoodsById(this.cards.get(i)).getType().equals(GoodsType.Legal)) {
                legalCards.add(this.cards.get(i));
            }
        }
        Collections.sort(legalCards, new BasicCompare(goods));
        int maxCount = 0;
        int mostFrequent = legalCards.get(0);
        int currentCount = 1;

        for (int i = 1; i < legalCards.size(); i++) {
            if (legalCards.get(i) == legalCards.get(i - 1)) {
                currentCount++;
            } else {
                if (currentCount > maxCount) {
                    maxCount = currentCount;
                    mostFrequent = legalCards.get(i - 1);
                }
                currentCount = 1;
            }
        }
        if (currentCount > maxCount) {
            maxCount = currentCount;
            mostFrequent = legalCards.get(legalCards.size() - 1);
        }

        // Adds the item in the bag

        bag.setDeclaredAs(mostFrequent);
        if (bag.getBagOfCards().size() + maxCount > Constants.getMaxBagSize()) {
            int tempCount = Constants.getMaxBagSize() - bag.getBagOfCards().size();
            bag.getBagOfCards().put(goods.getGoodsById(mostFrequent), tempCount);
            bag.addCount(tempCount);
        } else {
            bag.getBagOfCards().put(goods.getGoodsById(mostFrequent), maxCount);
            bag.addCount(maxCount);
        }
    }

    /**
     * Basic player playing as sheriff method.
     * @param playerHandler
     * @param gameInput
     */
    public void playAsSheriff(final PlayerHandler playerHandler, final GameInput gameInput) {
        ArrayList<Goods> toRemove = new ArrayList<Goods>();
        for (Merchant m : playerHandler.getPlayersList()) {
            if (!m.isSheriff()) {
                Iterator it = m.bag.getBagOfCards().entrySet().iterator();
                while (it.hasNext()) {
                    // Pair is an item from the bag
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

    public final void computeStall(final GoodsFactory goods) {
        // Adds the elements that passed the inspection
        // on the stall and adds bonus elements for illegal ones
        ArrayList<Integer> stall = new ArrayList<Integer>(tempStall);
        tempStall.clear();
        for (Integer item : stall) {
            this.allItems.add(item);
            if (goods.getGoodsById(item).getType() == GoodsType.Illegal) {
                Map<Goods, Integer> tempBonusGoods =
                        ((IllegalGoods) goods.getGoodsById(item)).getIllegalBonus();
                Iterator it = tempBonusGoods.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    Goods tempGood = (Goods) pair.getKey();
                    Integer tempCount = (Integer) pair.getValue();
                    for (int i = 0; i < tempCount; i++) {
                        allItems.add(tempGood.getId());
                    }
                }
            }
        }
    }

    public final int frequency(final int x) {
        // Finds the frequency of x in allItems
        int count = 0;
        for (int i = 0; i < allItems.size(); i++) {
            if (allItems.get(i) == x) {
                count++;
            }
        }
        if (count == 0) {
            return -1;
        } else {
            return count;
        }
    }

    /**
     * Converts to string necessary data for table.
     * @return Basic player entry in table
     */
    public String toString() {
        return this.id + " " + this.type + " " + this.coins;
    }

    public final void setCards(final ArrayList<Integer> cards) {
        this.cards = cards;
    }

    public final void addCoins(final int x) {
        this.coins += x;
    }

    public final void removeCoins(final int x) {
        this.coins -= x;
    }

    public final int getId() {
        return this.id;
    }

    /**
     * Basic player type.
     * @return String "BASIC"
     */
    public String getType() {
        return this.type;
    }

    public final int getCoins() {
        return this.coins;
    }

    public final void setSheriff() {
        this.isSheriff = true;
    }

    public final void setMerchant() {
        this.isSheriff = false;
    }

    public final boolean isSheriff() {
        if (this.isSheriff) {
            return true;
        }
        return false;
    }

    public final ArrayList<Integer> getAllItems() {
        return this.allItems;
    }

    public final int compareTo(final Merchant m) {
        int comparePoints = m.getCoins();

        return comparePoints - this.coins;
    }

    public final int getBribe() {
        return bribe;
    }
}
