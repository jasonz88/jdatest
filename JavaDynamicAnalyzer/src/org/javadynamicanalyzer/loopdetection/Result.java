package org.javadynamicanalyzer.loopdetection;

public class Result {
    private final int lambda;
    private final int mu;
    public Result(int lambda, int mu) {
        this.lambda = lambda;
        this.mu = mu;
    }
    public int getLambda() {
        return lambda;
    }
    public int getMu() {
        return mu;
    }
}
