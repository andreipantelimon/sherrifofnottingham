package com.tema1.player;

import com.tema1.goods.GoodsFactory;

import java.util.Comparator;

public class BasicCompare implements Comparator<Integer> {
    private GoodsFactory goods;
    public BasicCompare(final GoodsFactory goods) {
        this.goods = goods;
    }
    @Override
    public final int compare(final Integer o1, final Integer o2) {
        if (goods.getGoodsById(o1).getProfit() > goods.getGoodsById(o2).getProfit()) {
            return -1;
        } else {
            if (goods.getGoodsById(o1).getProfit() < goods.getGoodsById(o2).getProfit()) {
                return 1;
            } else {
                if (goods.getGoodsById(o1).getId() > goods.getGoodsById(o2).getId()) {
                    return -1;
                } else {
                    if (goods.getGoodsById(o1).getId() < goods.getGoodsById(o2).getId()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        }
    }
}
