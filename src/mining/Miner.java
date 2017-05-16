/**
 * 
 */
package mining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;

import egroum.EGroumActionNode;
import egroum.EGroumDataEdge;
import egroum.EGroumDataNode;
import egroum.EGroumEdge;
import egroum.EGroumGraph;
import egroum.EGroumNode;
import egroum.EGroumDataEdge.Type;
import utils.FileIO;

/**
 * @author hoan
 * 
 */
public class Miner 
{
	//allData: contains all data for all methods, (String) key is method name,
	//value = HashSet of ArrayList (all the case the param was encountered with its context)
	//(arrayList of String) = param and context of the method
	HashMap<String, HashSet<ArrayList<String>>> allData = new HashMap<String, HashSet<ArrayList<String>>>();
	
	public static boolean EXTEND_SOURCE_DATA_NODES = true;
	private String projectName;
	private final Configuration config;
	public ArrayList<Lattice> lattices = new ArrayList<Lattice>();
	public ArrayList<Anomaly> anomalies = new ArrayList<>();
	
	public Miner(String projectName, Configuration config) {
		this.projectName = projectName;
		this.config = config;
	}
	
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Mining Graphical model
	 * @param groums arraylist of EG graph
	 * output and print out to a list of file
	 * each file for a method, each line has that method's param and context
	 */
	public void mineGM(ArrayList<EGroumGraph> groums) 
	{
		for (EGroumGraph groum: groums)
		{
			for(EGroumNode node: groum.getNodes())
			{
				//Only get action nodes
				if(node.getClass() == EGroumActionNode.class)
				{
					if(node.isMethodInvocation == true)
					{
						ArrayList<String> paramAndContext = new ArrayList<String>();
						String methodName = "";
						
						String param = "";
						for (EGroumEdge edge:node.getInEdges())
						{
							//Get the receiver of the method call
							if (edge.isRecv())
							{
								methodName = edge.getSource().getLabel() + node.getLabel().substring(
										node.getLabel().indexOf('.'));
							}

							//Get the param
							if (edge.isParameter())
							{
								if (!param.isEmpty())
									param += "|";
								if (edge.getSource().isMethodInvocation)
								{
									param += "mi#";
								}
								else if (edge.getSource().getClass() == EGroumDataNode.class)
								{
									if (edge.getSource().isLiteral())
									{
										param += "lit#";
									}
									else
									{
										param += "var#";
									}
								}		
								param += edge.getSource().getLabel();
							}
						}
						paramAndContext.add(param);
						//Get the context
						int i = 1;
						node.findContext(i,paramAndContext);
						if(allData.containsKey(methodName))
						{
							allData.get(methodName).add(paramAndContext);
						}
						else
						{
							HashSet<ArrayList<String>> temp = new HashSet<ArrayList<String>>();
							temp.add(paramAndContext);
							allData.put(methodName, temp);
						}
							
						System.out.println(methodName + " argument and context: " );
						for(String si:paramAndContext)
						{
							System.out.print( si + ",");
						}
						System.out.println();
					}
				}
			}
			//groum.toGraphics(s, outputPath);
		}
		
	}
	
	/**
	 * Print the allData to file
	 */
	public void printFile()
	{
		try {
            //Whatever the file path is.
            File statText = new File("F:/Study/Research/GraphModelForArgumentRecommendation/data.txt");
            FileWriter fw = new FileWriter(statText);
            BufferedWriter bw = new BufferedWriter(fw);
            for(String s:allData.keySet())
            {
            	bw.write(s + "\n");
            	for(ArrayList<String> al: allData.get(s))
            	{
            		for(int i = 1; i < al.size(); i++)
            		{
            			bw.write(al.get(i) + ",");
            		}
            		bw.write(al.get(0) + "\n");
            	}
            	bw.write("------------------------------------------------------" + "\n");
            }
            
            bw.close();
        } catch (IOException e) {
            System.err.println("Problem writing to the file statsTest.txt");
        }
	}
	
	public Set<Pattern> mine(ArrayList<EGroumGraph> groums) {
		for (EGroumGraph groum : groums) {
			//groum.deleteUnaryOperationNodes();
			groum.collapseLiterals();
		}
		HashSet<String> coreLabels = new HashSet<>();
		HashMap<String, HashSet<EGroumNode>> nodesOfLabel = new HashMap<String, HashSet<EGroumNode>>();
		for (EGroumGraph groum : groums) {
			for (EGroumNode node : groum.getNodes()) {
				node.setGraph(groum);
				String label = node.getLabel();
				HashSet<EGroumNode> nodes = nodesOfLabel.get(label);
				if (nodes == null)
					nodes = new HashSet<EGroumNode>();
				nodes.add(node);
				nodesOfLabel.put(label, nodes);
				if (node.isCoreAction()
						&& node.getAstNodeType() != ASTNode.ASSERT_STATEMENT
						&& node.getAstNodeType() != ASTNode.BREAK_STATEMENT
						&& node.getAstNodeType() != ASTNode.CONTINUE_STATEMENT
						&& node.getAstNodeType() != ASTNode.RETURN_STATEMENT
						&& node.getAstNodeType() != ASTNode.THROW_STATEMENT)
					coreLabels.add(label);
			}
		}
		Lattice l = new Lattice();
		l.setStep(1);
		lattices.add(l);
		for (String label : new HashSet<String>(nodesOfLabel.keySet())) {
			HashSet<EGroumNode> nodes = nodesOfLabel.get(label);
			if (nodes.size() < config.minPatternSupport) {
				// FIXME
				for (EGroumNode node : nodes) {
					boolean isDefAction = false;
					if (node instanceof EGroumActionNode) {
						for (EGroumEdge e : node.getOutEdges())
							if (e instanceof EGroumDataEdge && ((EGroumDataEdge) e).getType() == Type.DEFINITION) {
								isDefAction = true;
								break;
							}
					}
					if (!isDefAction)
						node.getGraph().delete(node);
				}
				nodesOfLabel.remove(label);
			}
			if (!coreLabels.contains(label))
				nodesOfLabel.remove(label);
		}
		for (String label : nodesOfLabel.keySet()) {
			HashSet<EGroumNode> nodes = nodesOfLabel.get(label);
			HashSet<Fragment> fragments = new HashSet<>();
			for (EGroumNode node : nodes) {
				Fragment f = new Fragment(node, config);
				fragments.add(f);
			}
			Pattern p = new Pattern(fragments, fragments.size());
			extend(p);
		}
		System.out.println("Done mining.");
		Lattice.filter(lattices, config.minPatternSize);
		System.out.println("Done filtering.");

		report();

		return getPatterns();
	}

	private void report() {
		if (config.outputPath != null) {
			File dir = new File(config.outputPath, this.projectName + "-" + (System.currentTimeMillis() / 1000));
			for (int step = config.minPatternSize; step <= lattices.size(); step++) {
				Lattice lat = lattices.get(step - 1);
				int c = 0;
				for (Pattern p : lat.getPatterns()) {
					c++;
					File patternDir = new File(dir.getAbsolutePath() + "/" + step + "/" + c + "_" + p.getId());
					if (!patternDir.exists())
						patternDir.mkdirs();
					Fragment rf = p.getRepresentative();
					rf.toGraphics(patternDir.getAbsolutePath(), rf.getId() + "");
					StringBuilder sb = new StringBuilder();
					for (Fragment f : p.getFragments()) {
						String fileName = f.getGraph().getFilePath();
						String name = f.getGraph().getName();
						sb.append(fileName + "," + name + "\n");
						/*String[] parts = name.split(",");
						sb.append("https://github.com/" + projectName + "/commit/"
								+ parts[0].substring(0, parts[0].indexOf('.')) + "/"
								+ parts[1]
								+ "\n");*/
					}
					FileIO.writeStringToFile(sb.toString(),
							patternDir.getAbsolutePath() + "/locations.txt");
				}
			}
			System.out.println("Done reporting.");
		}
	}
	
	private Set<Pattern> getPatterns() {
		Set<Pattern> patterns = new HashSet<>();
		for (int step = config.minPatternSize - 1; lattices.size() > step; step++) {
			Lattice lattice = lattices.get(step);
			patterns.addAll(lattice.getPatterns());
		}
		return patterns;
	}

	private void extend(Pattern pattern) {
		int patternSize = 0;
		if (pattern.getSize() >= config.maxPatternSize)
			for(EGroumNode node : pattern.getRepresentative().getNodes())
				if(node.isCoreAction())
					patternSize++;
		if(patternSize >= config.maxPatternSize) {
			pattern.add2Lattice(lattices);
			return;
		}
		HashMap<String, HashMap<Fragment, HashSet<ArrayList<EGroumNode>>>> labelFragmentExtendableNodes = new HashMap<>();
		for (Fragment f : pattern.getFragments()) {
			HashMap<String, HashSet<ArrayList<EGroumNode>>> xns = f.extend();
			for (String label : xns.keySet()) {
				HashMap<Fragment, HashSet<ArrayList<EGroumNode>>> fens = labelFragmentExtendableNodes.get(label);
				if (fens == null) {
					fens = new HashMap<>();
					labelFragmentExtendableNodes.put(label, fens);
				}
				fens.put(f, xns.get(label));
			}
		}
		for (String label : new HashSet<String>(labelFragmentExtendableNodes.keySet())) {
			HashMap<Fragment, HashSet<ArrayList<EGroumNode>>> fens = labelFragmentExtendableNodes.get(label);
			if (fens.size() < config.minPatternSupport)
				labelFragmentExtendableNodes.remove(label);
		}
		HashSet<Fragment> group = new HashSet<>(), frequentFragments = new HashSet<>();
		int xfreq = config.minPatternSupport - 1;
		boolean extensible = false;
		for (String label : labelFragmentExtendableNodes.keySet()) {
			HashMap<Fragment, HashSet<ArrayList<EGroumNode>>> fens = labelFragmentExtendableNodes.get(label);
			HashSet<Fragment> xfs = new HashSet<>();
			for (Fragment f : fens.keySet()) {
				for (ArrayList<EGroumNode> ens : fens.get(f)) {
					Fragment xf = new Fragment(f, ens);
					xfs.add(xf);
				}
			}
			boolean isGiant = isGiant(xfs, pattern, label);
			System.out.println("\tTrying with label " + label + ": " + xfs.size());
			HashSet<Fragment> g = new HashSet<>();
			int freq = mine(g, xfs, pattern, isGiant, frequentFragments);
			if (freq > xfreq) {
				extensible = true;
				group = g;
				xfreq = freq;
			} else if (freq == -1)
				extensible = true;
		}
		System.out.println("Done trying all labels");
		if (extensible) {
			HashSet<Fragment> inextensibles = new HashSet<>(pattern.getFragments());
			for (Fragment xf : frequentFragments) {
				inextensibles.remove(xf.getGenFragmen());
			}
			Pattern ip = null;
			if (inextensibles.size() >= config.minPatternSupport) {
				int freq = computeFrequency(inextensibles, false);
				if (freq >= config.minPatternSupport && !Lattice.contains(lattices, inextensibles)) {
					ip = new Pattern(inextensibles, freq);
					ip.subPattern = pattern.subPattern;
					ip.add2Lattice(lattices);
					pattern.getFragments().removeAll(inextensibles);
				}
			} else if (xfreq >= config.minPatternSupport && !inextensibles.isEmpty() /*&& inextensibles.size() <= 2*/){
				// report anomalies
				double rareness = 1 - inextensibles.size() * 1.0 / pattern.getFreq();
				anomalies.add(new Anomaly(rareness, pattern.getFreq(), inextensibles, group));
			}
			if (xfreq >= config.minPatternSupport) {
				Pattern xp = new Pattern(group, xfreq);
				ArrayList<String> labels = new ArrayList<>();
				Fragment rep = null, xrep = null;
				for (Fragment f : group) {
					xrep = f;
					break;
				}
				for (Fragment f : pattern.getFragments()) {
					rep = f;
					break;
				}
				if (rep != null && xrep != null) {
					for (int j = rep.getNodes().size(); j < xrep.getNodes().size(); j++)
						labels.add(xrep.getNodes().get(j).getLabel());
					System.out.println("{Extending pattern of size " + rep.getNodes().size()
							+ " " + rep.getNodes()
							+ " occurences: " + pattern.getFragments().size()
							+ " frequency: " + pattern.getFreq()
							+ " with label " + labels
							+ " occurences: " + group.size()
							+ " frequency: " + xfreq
							+ " patterns: " + Pattern.nextID 
							+ " fragments: " + Fragment.numofFragments 
							+ " next fragment: " + Fragment.nextFragmentId);
				}
				if (ip == null)
					xp.subPattern = pattern.subPattern;
				else
					xp.subPattern = ip;
				extend(xp);
				System.out.println("}");
			}
			pattern.clear();
		} else
			pattern.add2Lattice(lattices);
	}

	private boolean isGiant(HashSet<Fragment> xfs, Pattern pattern, String label) {
		return /*(EGroumNode.isMethod(label) || EGroumNode.isLiteral(label)) && */isGiant(xfs, pattern);
	}

	private boolean isGiant(HashSet<Fragment> xfs, Pattern pattern) {
		return pattern.getSize() > 1 
				&& (xfs.size() > config.maxPatternSupport || xfs.size() > pattern.getFragments().size() * pattern.getSize() * pattern.getSize());
	}

	private int mine(HashSet<Fragment> result, HashSet<Fragment> fragments, Pattern pattern, boolean isGiant, HashSet<Fragment> frequentFragments) {
		HashMap<Integer, HashSet<Fragment>> buckets = new HashMap<>();
		for (Fragment f : fragments) {
			int h = f.getVectorHashCode();
			HashSet<Fragment> bucket = buckets.get(h);
			if (bucket == null) {
				bucket = new HashSet<>();
				buckets.put(h, bucket);
			}
			bucket.add(f);
		}
		HashSet<HashSet<Fragment>> groups = new HashSet<>();
		for (int h : buckets.keySet()) {
			HashSet<Fragment> bucket = buckets.get(h);
			group(groups, bucket);
		}
		HashSet<Fragment> group = new HashSet<>();
		int xfreq = config.minPatternSupport - 1;
		boolean extensible = false;
		for (HashSet<Fragment> g : groups) {
			int freq = computeFrequency(g, isGiant && isGiant(g, pattern));
			if (freq >= config.minPatternSupport)
				frequentFragments.addAll(g);
			if (freq > xfreq) {
				extensible = true;
				if (!Lattice.contains(lattices, g)) {
					group = g;
					xfreq = freq;
				}
			}
		}
		result.addAll(group);
		if (extensible && xfreq < config.minPatternSupport)
			return -1;
		return xfreq;
	}

	private int computeFrequency(HashSet<Fragment> fragments, boolean isGiant) {
		HashMap<EGroumGraph, ArrayList<Fragment>> fragmentsOfGraph = new HashMap<EGroumGraph, ArrayList<Fragment>>();
		for (Fragment f : fragments) {
			EGroumGraph g = f.getGraph();
			ArrayList<Fragment> fs = fragmentsOfGraph.get(g);
			if (fs == null)
				fs = new ArrayList<Fragment>();
			fs.add(f);
			fragmentsOfGraph.put(g, fs);
		}
		int freq = 0;
		for (EGroumGraph g : fragmentsOfGraph.keySet()) {
			ArrayList<Fragment> fs = fragmentsOfGraph.get(g);
			int i = 0;
			while (i < fs.size()) {
				Fragment f = fs.get(i);
				int j = i + 1;
				while (j < fs.size()) {
					if (f.overlap(fs.get(j))) {
						if (isGiant)
							fragments.remove(fs.get(j));
						fs.remove(j);
					}
					else
						j++;
				}
				i++;
			}
			freq += i;
		}	
		return freq;
	}

	private void group(HashSet<HashSet<Fragment>> groups, HashSet<Fragment> bucket) {
		while (!bucket.isEmpty()) {
			Fragment f = null;
			for (Fragment fragment : bucket) {
				f = fragment;
				break;
			}
			group(f, groups, bucket);
		}
	}

	private void group(Fragment f, HashSet<HashSet<Fragment>> groups, HashSet<Fragment> bucket) {
		HashSet<Fragment> group = new HashSet<>();
		HashSet<Fragment> fs = new HashSet<>();
		group.add(f);
		fs.add(f.getGenFragmen());
		bucket.remove(f);
		for (Fragment g : new HashSet<Fragment>(bucket)) {
			if (f.getVector().equals(g.getVector())) {
				group.add(g);
				fs.add(g.getGenFragmen());
				bucket.remove(g);
			}
		}
		if (fs.size() >= config.minPatternSupport && group.size() >= config.minPatternSupport) {
			removeDuplicates(group);
			if (group.size() >= config.minPatternSupport)
				groups.add(group);
		}
	}

	private void removeDuplicates(HashSet<Fragment> group) {
		ArrayList<Fragment> l = new ArrayList<>(group);
		int i = 0;
		while (i < l.size() - 1) {
			Fragment f = l.get(i);
			int j = i + 1;
			while (j < l.size()) {
				if (f.isSameAs(l.get(j)))
					l.remove(j);
				else
					j++;
			}
			i++;
		}
		group.retainAll(l);
	}
}
