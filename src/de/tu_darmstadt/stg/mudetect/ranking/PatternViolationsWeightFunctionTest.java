package de.tu_darmstadt.stg.mudetect.ranking;

import de.tu_darmstadt.stg.mudetect.model.AUG;
import de.tu_darmstadt.stg.mudetect.model.Overlap;
import de.tu_darmstadt.stg.mudetect.model.Overlaps;
import de.tu_darmstadt.stg.mudetect.mining.Pattern;
import org.junit.Before;
import org.junit.Test;

import static de.tu_darmstadt.stg.mudetect.model.TestAUGBuilder.someAUG;
import static de.tu_darmstadt.stg.mudetect.model.TestOverlapBuilder.someOverlap;
import static de.tu_darmstadt.stg.mudetect.mining.TestPatternBuilder.somePattern;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PatternViolationsWeightFunctionTest {

    private Pattern aPattern;
    private Overlap violation;
    private Overlaps overlaps;

    @Before
    public void setup() {
        AUG aTarget = someAUG();

        aPattern = somePattern();

        violation = someOverlap(aPattern, aTarget);
        overlaps = new Overlaps();
        overlaps.addViolation(violation);
    }

    @Test
    public void withoutOtherViolationOfPattern() throws Exception {
        ViolationWeightFunction weightFunction = new PatternViolationsWeightFunction();

        float weight = weightFunction.getWeight(violation, overlaps, null);

        assertThat(weight, is(1f));
    }

    @Test
    public void withOtherViolationOfSamePattern() throws Exception {
        overlaps.addViolation(someOverlap(aPattern, someAUG()));
        ViolationWeightFunction weightFunction = new PatternViolationsWeightFunction();

        float weight = weightFunction.getWeight(violation, overlaps, null);

        assertThat(weight, is(0.5f));
    }

    @Test
    public void withViolationOfOtherPattern() throws Exception {
        overlaps.addViolation(someOverlap(somePattern(), someAUG()));
        ViolationWeightFunction weightFunction = new PatternViolationsWeightFunction();

        float weight = weightFunction.getWeight(violation, overlaps, null);

        assertThat(weight, is(1f));
    }
}
