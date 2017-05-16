package de.tu_darmstadt.stg.mudetect;

import de.tu_darmstadt.stg.mudetect.model.TestAUGBuilder;
import org.junit.Test;

import static de.tu_darmstadt.stg.mudetect.OverlapsFinderTestUtils.findOverlaps;
import static de.tu_darmstadt.stg.mudetect.model.TestAUGBuilder.buildAUG;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class FindNoOverlapsTest {
    @Test
    public void ignoresNonOverlappingTarget() throws Exception {
        TestAUGBuilder pattern = buildAUG().withActionNode("F.foo()");
        TestAUGBuilder target = buildAUG().withActionNode("B.bar()");

        assertNoOverlap(pattern, target);
    }

    @Test
    public void ignoresDateNodeOverlap() throws Exception {
        TestAUGBuilder pattern = buildAUG().withDataNode("Object");
        TestAUGBuilder target = buildAUG().withDataNode("Object");

        assertNoOverlap(pattern, target);
    }

    private void assertNoOverlap(TestAUGBuilder patternBuilder, TestAUGBuilder targetBuilder) {
        assertThat(findOverlaps(patternBuilder, targetBuilder), is(empty()));
    }

}
