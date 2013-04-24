package org.javadynamicanalyzer.graph.loopdetection;

import java.util.List;

public class FloydsAlgorithm {

    private final List<Integer> list;

    private FloydsAlgorithm(List<Integer> list) {
        this.list = list;
    }

    public static FloydsAlgorithm getInstance(List<Integer> list) {
        FloydsAlgorithm floyds = new FloydsAlgorithm(list);
        return floyds;
    }
    
    public Result calculate(int x0) {
        int mu = 0;
        int lambda = 1;

        int tortoise = f(x0);
        int hare = f(f(x0));
        while (tortoise != hare) {
            tortoise = f(tortoise);
            hare = f(f(hare));
        }
        tortoise = x0;
        while (tortoise != hare) {
            tortoise = f(tortoise);
            hare = f(hare);
            mu = mu + 1;
        }
        hare = f(tortoise);
        while (tortoise != hare) {
            hare = f(hare);
            lambda = lambda + 1;
        }
        return new Result(lambda, mu);
    }
    
    private int f(int x) {
        return list.get(x);
    }   
}