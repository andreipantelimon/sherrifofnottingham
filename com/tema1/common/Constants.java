package com.tema1.common;

public final class Constants {
    // add/delete any constants you think you may use
    private static int currentRound = 0;
    public static final int CARD_TO_DRAW = 10;
    public static final int NO_OF_LEGAL_CARDS = 10;
    public static final int LOW_BRIBE = 5;
    public static final int HIGH_BRIBE = 10;
    public static final int LEGAL_PENALTY = 2;
    public static final int ILLEGAL_PENALTY = 4;
    public static final int MAX_BAG_SIZE = 8;
    public static final int STARTING_COINS = 80;
    public static final int MINIMUM_COINS_TO_VERIFY = 16;

    private Constants() {
        // trick
    }


    public static void addCurrentRound(final int x) {
        currentRound += x;
    }

    public static int getCurrentRound() {
        return currentRound;
    }

    public static int getCardToDraw() {
        return CARD_TO_DRAW;
    }

    public static int getNoOfLegalCards() {
        return NO_OF_LEGAL_CARDS;
    }

    public static int getLowBribe() {
        return LOW_BRIBE;
    }

    public static int getHighBribe() {
        return HIGH_BRIBE;
    }

    public static int getLegalPenalty() {
        return LEGAL_PENALTY;
    }

    public static int getIllegalPenalty() {
        return ILLEGAL_PENALTY;
    }

    public static int getMaxBagSize() {
        return MAX_BAG_SIZE;
    }

    public static int getStartingCoins() {
        return STARTING_COINS;
    }

    public static int getMinimumCoinsToVerify() {
        return MINIMUM_COINS_TO_VERIFY;
    }
}
