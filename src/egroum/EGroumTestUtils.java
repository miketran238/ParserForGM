package egroum;

import java.awt.Label;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import utils.FileIO;
import utils.JavaASTUtil;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EGroumTestUtils {
	public static EGroumGraph buildGroumForMethod(String code) {
		return buildGroumForMethod(code, new AUGConfiguration());
	}

	public static EGroumGraph buildGroumForMethod(String code, AUGConfiguration configuration) {
		String classCode = "class C { " + code + "}";
		ArrayList<EGroumGraph> groums = buildGroumsForClass(classCode, configuration);
		assertThat(groums.size(), is(1));
		return groums.iterator().next();
	}

	public static ArrayList<EGroumGraph> buildGroumsForClass(String classCode) {
		return buildGroumsForClass(classCode, new AUGConfiguration());
	}

	public static ArrayList<EGroumGraph> buildGroumsForClass(String classCode, AUGConfiguration configuration) {
		EGroumBuilder builder = new EGroumBuilder(configuration);
		return builder.buildGroums(classCode, "test", "test", null);
	}

	public static void buildAndPrintGroumsForFile(String inputPath, String name, String[] classpaths, String outputPath) {
		EGroumBuilder gb = new EGroumBuilder(new AUGConfiguration(){{groum = true;}});
		inputPath = inputPath + "/" + name;
		String content = FileIO.readStringFromFile(inputPath);
		ASTNode ast = JavaASTUtil.parseSource(content, inputPath, name, classpaths);
		CompilationUnit cu = (CompilationUnit) ast;
		TypeDeclaration type = (TypeDeclaration) cu.types().get(0);
		for (MethodDeclaration m : type.getMethods()) {
			EGroumGraph g = gb.buildGroum(m, inputPath, type.getName().getIdentifier() + ".");
			String s = m.toString();
			s = s.replace("\n", "\\l");
			s = s.replace("\t", "    ");
			s = s.replace("\"", "\\\"");
			s += "\\l";
			s = "0 [label=\"" + s + "\"" + " shape=box style=dotted]";
			//			System.out.println(s);
			g.toGraphics(s, outputPath);
		}
	}

	public static void buildAndPrintGroumsForFile(String inputPath, String name, String[] classpaths, String outputPath, AUGConfiguration config) {
		EGroumBuilder gb = new EGroumBuilder(config);
		inputPath = inputPath + "/" + name;
		String content = FileIO.readStringFromFile(inputPath);
		ASTNode ast = JavaASTUtil.parseSource(content, inputPath, name, classpaths);
		CompilationUnit cu = (CompilationUnit) ast;
		TypeDeclaration type = (TypeDeclaration) cu.types().get(0);
		for (MethodDeclaration m : type.getMethods()) 
		{
			EGroumGraph g = gb.buildGroum(m, inputPath, type.getName().getIdentifier() + ".");
			String s = m.toString();
			s = s.replace("\n", "\\l");
			s = s.replace("\t", "    ");
			s = s.replace("\"", "\\\"");
			s += "\\l";
			s = "0 [label=\"" + s + "\"" + " shape=box style=dotted]";
			//System.out.println(s);

			//Method invocation nodes

			ArrayList<HashMap<String,String[]>> all = new ArrayList<HashMap<String, String[]>>();
			for(EGroumNode node: g.nodes)
			{
				//Only get action nodes
				if(node.getClass() == EGroumActionNode.class)
				{
					if(node.isMethodInvocation == true)
					{
						HashMap<String,ArrayList<String>> myHash = new HashMap<String,ArrayList<String>>();

						ArrayList<String> context = new ArrayList<String>();
						String methodName = "";
						
						
						for (EGroumEdge edge:node.getInEdges())
						{
							//Get the receiver of the method call
							if (edge.isRecv())
							{
								methodName = edge.source.getLabel() + node.getLabel().substring(
										node.getLabel().indexOf('.'));
							}

							//Get the param
							String param = "";
							if (edge.isParameter())
							{
								if (edge.source.isMethodInvocation)
								{
									param += "mi#";
								}
								if (edge.source.getClass() == EGroumDataNode.class)
								{
									param += "var#";
								}
								param += edge.source.getLabel();
								context.add(param);
							}
						}
						
						//Get the context
						int i = 1;
						node.findContext(i,context);
						myHash.put(methodName, context);
						System.out.println(methodName + " argument and context: " );
						for(String si:context)
						{
							System.out.print( si + ",");
						}
						System.out.println();
					}
				}
			}
			g.toGraphics(s, outputPath);
		}
	}
}
