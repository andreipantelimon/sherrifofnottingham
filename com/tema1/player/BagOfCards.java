package com.tema1.player;

import com.tema1.goods.Goods;

import java.util.HashMap;
import java.util.Map;

public class BagOfCards {
    private Map<Goods, Integer> bagOfCards;
    private int declaredAs = -1;
    private int totalCount = 0;
    private int illegalCount = 0;
    private boolean isIllegal = false;
    private int legalCount = 0;

    public BagOfCards() {
        bagOfCards = new HashMap<Goods, Integer>();
    }

    public final Map<Goods, Integer> getBagOfCards() {
        return bagOfCards;
    }

    public final int getDeclaredAs() {
        return declaredAs;
    }

    public final void setDeclaredAs(final int declaredAs) {
        this.declaredAs = declaredAs;
    }

    public final String toString() {
        return "Bag: " + bagOfCards + " declared as: " + this.declaredAs;
    }

    public final void addCount(final int x) {
        this.totalCount += x;
    }

    public final int getTotalCount() {
        return this.totalCount;
    }

    public final boolean isIllegal() {
        return isIllegal;
    }

    public final void setIllegal(final boolean illegal) {
        isIllegal = illegal;
    }

    public final int getIllegalCount() {
        return illegalCount;
    }

    public final void addIllegalCount(final int x) {
        this.illegalCount += x;
    }

    public final void addLegalCount(final int x) {
        this.legalCount += x;
    }
}
