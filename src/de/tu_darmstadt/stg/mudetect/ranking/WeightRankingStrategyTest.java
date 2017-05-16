package de.tu_darmstadt.stg.mudetect.ranking;

import de.tu_darmstadt.stg.mudetect.mining.Model;
import de.tu_darmstadt.stg.mudetect.ViolationRankingStrategy;
import de.tu_darmstadt.stg.mudetect.model.Overlap;
import de.tu_darmstadt.stg.mudetect.model.Overlaps;
import de.tu_darmstadt.stg.mudetect.model.Violation;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static de.tu_darmstadt.stg.mudetect.model.TestOverlapBuilder.someOverlap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class WeightRankingStrategyTest {
    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();

    @Test
    public void ranksViolations() throws Exception {
        Overlap violation1 = someOverlap();
        Overlap violation2 = someOverlap();

        Overlaps overlaps = new Overlaps();
        overlaps.addViolation(violation1);
        overlaps.addViolation(violation2);

        Model model = context.mock(Model.class);

        ViolationWeightFunction weightFunction = context.mock(ViolationWeightFunction.class);
        context.checking(new Expectations() {{
            allowing(weightFunction).getWeight(violation1, overlaps, model); will(returnValue(0.5f));
            allowing(weightFunction).getFormula(violation1, overlaps, model); will(returnValue("0.5"));
            allowing(weightFunction).getWeight(violation2, overlaps, model); will(returnValue(0.7f));
            allowing(weightFunction).getFormula(violation2, overlaps, model); will(returnValue("0.7"));
        }});

        ViolationRankingStrategy strategy = new WeightRankingStrategy(weightFunction);
        final List<Violation> violations = strategy.rankViolations(overlaps, model);

        assertThat(violations, contains(
                new Violation(violation2, 0.7f, "0.7"),
                new Violation(violation1, 0.5f, "0.5")));
    }

    @Test
    public void ranksViolationsWithSameConfidenceInAnyOrder() throws Exception {
        Overlap violation1 = someOverlap();
        Overlap violation2 = someOverlap();

        Overlaps overlaps = new Overlaps();
        overlaps.addViolation(violation1);
        overlaps.addViolation(violation2);

        Model model = context.mock(Model.class);

        ViolationWeightFunction weightFunction = context.mock(ViolationWeightFunction.class);
        context.checking(new Expectations() {{
            allowing(weightFunction).getWeight(violation1, overlaps, model); will(returnValue(1f));
            allowing(weightFunction).getFormula(violation1, overlaps, model); will(returnValue("1"));
            allowing(weightFunction).getWeight(violation2, overlaps, model); will(returnValue(1f));
            allowing(weightFunction).getFormula(violation2, overlaps, model); will(returnValue("1"));
        }});

        ViolationRankingStrategy strategy = new WeightRankingStrategy(weightFunction);
        final List<Violation> violations = strategy.rankViolations(overlaps, model);

        assertThat(violations, containsInAnyOrder(
                new Violation(violation2, 1f, "1"),
                new Violation(violation1, 1f, "1")));
    }


}
