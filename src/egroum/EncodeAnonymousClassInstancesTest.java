package egroum;

import de.tu_darmstadt.stg.mudetect.model.AUG;
import org.junit.Before;
import org.junit.Test;

import static de.tu_darmstadt.stg.mudetect.model.AUGTestUtils.*;
import static egroum.AUGBuilderTestUtils.buildAUG;
import static egroum.EGroumDataEdge.Type.CONTAINS;
import static egroum.EGroumDataEdge.Type.PARAMETER;
import static org.junit.Assert.assertThat;

public class EncodeAnonymousClassInstancesTest {
    private AUG aug;

    @Before
    public void setUp() throws Exception {
        aug = buildAUG("void m() {" +
                "  SwingUtilities.invokeLater(new Runnable() {" +
                "    public void run() {" +
                "      new Object();" +
                "    }" +
                "  });" +
                "}");
    }

    @Test
    public void addsActionOnInstance() throws Exception {
        assertThat(aug, hasEdge(actionNodeWithLabel("Runnable.<init>"), PARAMETER, actionNodeWithLabel("SwingUtilities.invokeLater()")));
        assertThat(aug, hasEdge(dataNodeWithLabel("Runnable"), PARAMETER, actionNodeWithLabel("SwingUtilities.invokeLater()")));
    }

    @Test
    public void addsCodeFromAnonymousClassMethod() throws Exception {
        assertThat(aug, hasNode(actionNodeWithLabel("Object.<init>")));
    }

    @Test
    public void addsDataNodeForAnyonymousClassMethods() throws Exception {
        assertThat(aug, hasNode(dataNodeWithLabel("Runnable.run()")));
    }

    @Test
    public void addsContainsEdges() throws Exception {
        assertThat(aug, hasEdge(dataNodeWithLabel("Runnable"), CONTAINS, dataNodeWithLabel("Runnable.run()")));
        assertThat(aug, hasEdge(dataNodeWithLabel("Runnable.run()"), CONTAINS, actionNodeWithLabel("Object.<init>")));
    }
}
