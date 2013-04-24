package org.javadynamicanalyzer.graph.loopdetection;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestCycle {

    List<Integer> functionValues = new ArrayList<Integer>();
    
    List<Integer> verification = new ArrayList<Integer>();
    
    @Before
    public void setup() {
        functionValues.clear();
        Integer ints[] = new Integer[]{6, 6, 0, 1, 4, 3, 3, 4, 0};
        functionValues.addAll(Arrays.asList(ints));

        verification.clear();
        Integer ver[] = new Integer[]{3, 3, 3, 3, 1, 3, 3, 1, 3};
        verification.addAll(Arrays.asList(ver));
    }

   @Test
    public void testFloydsAlgorithm() {
        FloydsAlgorithm floyd = FloydsAlgorithm.getInstance(functionValues);
        // Test each index
        for (int x0 = 0; x0 < functionValues.size(); x0++) {
            Result result = floyd.calculate(x0);
            assertTrue("Length of cycle not as expected", verification.get(x0) == result.getLambda());
        }
    }

    @Test
    public void testBrentsAlgorithm() {
        BrentsAlgorithm brent = BrentsAlgorithm.getInstance(functionValues);
        // Test each index
        for (int x0 = 0; x0 < functionValues.size(); x0++) {
            Result result = brent.calculate(x0);
            assertTrue("Length of cycle not as expected", verification.get(x0) == result.getLambda());
            System.out.print(x0);
            System.out.print("->");
            System.out.println(result.getLambda());
        }
    }
}