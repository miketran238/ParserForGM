package egroum;

import de.tu_darmstadt.stg.mudetect.model.AUG;
import org.junit.Test;

import static de.tu_darmstadt.stg.mudetect.model.AUGTestUtils.*;
import static egroum.AUGBuilderTestUtils.buildAUG;
import static egroum.EGroumDataEdge.Type.PARAMETER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

public class EncodeConditionJunctionTest {

    @Test
    public void encodesDisjunction() throws Exception {
        AUG aug = buildAUG(
                "void m(String s) { if (s.isEmpty() || s.contains(\"foo\")) s.getBytes(); }",
                new AUGConfiguration() {{ encodeConditionalOperators = true; }});

        assertThat(aug, hasEdge(actionNodeWithLabel("String.isEmpty()"), PARAMETER, actionNodeWithLabel("<c>")));
        assertThat(aug, hasEdge(actionNodeWithLabel("String.contains()"), PARAMETER, actionNodeWithLabel("<c>")));
        assertThat(aug, hasSelEdge(actionNodeWithLabel("<c>"), actionNodeWithLabel("String.getBytes()")));
        assertThat(aug, hasSelEdge(actionNodeWithLabel("String.isEmpty()"), actionNodeWithLabel("String.getBytes()")));
        assertThat(aug, hasSelEdge(actionNodeWithLabel("String.contains()"), actionNodeWithLabel("String.getBytes()")));
    }

    @Test
    public void encodesConjunction() throws Exception {
        AUG aug = buildAUG(
                "void m(String s) { if (s.isEmpty() && s.contains(\"foo\")) s.getBytes(); }",
                new AUGConfiguration() {{ encodeConditionalOperators = true; }});

        assertThat(aug, hasEdge(actionNodeWithLabel("String.isEmpty()"), PARAMETER, actionNodeWithLabel("<c>")));
        assertThat(aug, hasEdge(actionNodeWithLabel("String.contains()"), PARAMETER, actionNodeWithLabel("<c>")));
        assertThat(aug, hasSelEdge(actionNodeWithLabel("<c>"), actionNodeWithLabel("String.getBytes()")));
        assertThat(aug, hasSelEdge(actionNodeWithLabel("String.isEmpty()"), actionNodeWithLabel("String.getBytes()")));
        assertThat(aug, hasSelEdge(actionNodeWithLabel("String.contains()"), actionNodeWithLabel("String.getBytes()")));
    }

    @Test
    public void ignoreJunctions() throws Exception {
        AUG aug = buildAUG(
                "void m(String s) { if (s.isEmpty() || s.contains(\"foo\") && s.startsWith(\"bar\")) s.getBytes(); }",
                new AUGConfiguration() {{ encodeConditionalOperators = false; }});

        assertThat(aug, not(hasNode(actionNodeWithLabel("<c>"))));
        assertThat(aug, hasSelEdge(actionNodeWithLabel("String.isEmpty()"), actionNodeWithLabel("String.getBytes()")));
        assertThat(aug, hasSelEdge(actionNodeWithLabel("String.contains()"), actionNodeWithLabel("String.getBytes()")));
        assertThat(aug, hasSelEdge(actionNodeWithLabel("String.startsWith()"), actionNodeWithLabel("String.getBytes()")));
    }
}
