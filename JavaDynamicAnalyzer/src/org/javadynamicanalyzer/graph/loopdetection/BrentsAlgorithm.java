package org.javadynamicanalyzer.graph.loopdetection;

import java.util.List;

public class BrentsAlgorithm {

    private final List<Integer> list;

    private BrentsAlgorithm(List<Integer> list) {
        this.list = list;
    }

    public static BrentsAlgorithm getInstance(List<Integer> list) {
        BrentsAlgorithm brents = new BrentsAlgorithm(list);
        return brents;
    }

    public Result calculate(int x0) {
        int mu = 0;
        int lambda = 1;

        int power = 1;
        int tortoise = x0;
        int hare = f(x0);

        while((tortoise != hare)) {
            if (power == lambda) {
                tortoise = hare;
                power = power * 2;
                lambda = 0;
            }
            hare = f(hare);
            lambda++;
        }
        tortoise = hare = x0;
        for  (int i = 0; i < lambda; i++) {
            hare = f(hare);
        }
        while((tortoise != hare)) {
            tortoise = f(tortoise);
            hare = f(hare);
            mu++;
        }
        return new Result(lambda, mu);
    }

    private int f(int x) {
        return list.get(x);
    }
}