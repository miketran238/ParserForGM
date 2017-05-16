package de.tu_darmstadt.stg.mudetect.dot;

import de.tu_darmstadt.stg.mudetect.model.AUG;
import de.tu_darmstadt.stg.mudetect.model.Violation;
import org.junit.Test;

import java.util.HashSet;

import static de.tu_darmstadt.stg.mudetect.model.TestAUGBuilder.buildAUG;
import static de.tu_darmstadt.stg.mudetect.model.TestOverlapBuilder.*;
import static egroum.EGroumDataEdge.Type.ORDER;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class GenerateViolationDotGraphTest {

    @Test
    public void includesNodeLabel() throws Exception {
        AUG aug = buildAUG(":G:").withActionNode(":action:").build();
        Violation violation = new Violation(instance(aug), 1, "constant rank");

        assertDotGraphContains(violation, " [ label=\":action:\" shape=\"box\" ");
    }

    @Test
    public void includesEdgeLabel() throws Exception {
        AUG aug = buildAUG(":G:").withActionNodes(":a:", ":b:").withDataEdge(":a:", ORDER, ":b:").build();
        Violation violation = new Violation(instance(aug), 1, "constant rank");

        assertDotGraphContains(violation, " [ label=\"order\" style=\"dotted\" ");
    }

    @Test
    public void includesMissingNode() throws Exception {
        AUG aug = buildAUG().withActionNode(":action:").build();
        Violation violation = new Violation(emptyOverlap(aug), 1, "constant rank");

        assertDotGraphContains(violation, "1 [ label=\":action:\" shape=\"box\" color=\"red\" fontcolor=\"red\" ];");
    }

    @Test
    public void includesMissingEdge() throws Exception {
        AUG aug = buildAUG().withActionNodes(":a:", ":b:").withDataEdge(":a:", ORDER, ":b:").build();
        Violation violation = new Violation(someOverlap(aug, aug.vertexSet(), new HashSet<>()), 1, "constant rank");

        assertDotGraphContains(violation, " [ label=\"order\" style=\"dotted\" color=\"red\" fontcolor=\"red\" ];");
    }

    @Test
    public void rendersDataNodeAsEllipse() throws Exception {
        final AUG aug = buildAUG().withDataNode("D").build();
        final Violation violation = new Violation(instance(aug), 1, "constant rank");

        assertDotGraphContains(violation, "[ label=\"D\" shape=\"ellipse\" ");
    }

    private void assertDotGraphContains(Violation violation, String expectedDotGraphFragment) {
        String dotGraph = new ViolationDotExporter().toDotGraph(violation);

        assertThat(dotGraph, containsString(expectedDotGraphFragment));
    }
}
