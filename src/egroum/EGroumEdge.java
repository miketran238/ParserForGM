package egroum;

import java.util.HashSet;

import egroum.EGroumDataEdge.Type;

public abstract class EGroumEdge {
	protected int id;
	protected EGroumNode source;
	protected EGroumNode target;
	protected String label;
	
	public EGroumEdge(EGroumNode source, EGroumNode target) {
		this.source = source;
		this.target = target;
	}

	public int getId() {
		return id;
	}

	public abstract String getLabel();

	public EGroumNode getSource() {
		return source;
	}

	public EGroumNode getTarget() {
		return target;
	}

	public boolean isParameter() {
		return this instanceof EGroumDataEdge && ((EGroumDataEdge) this).type == Type.PARAMETER;
	}

	public boolean isDef() {
		return this instanceof EGroumDataEdge && ((EGroumDataEdge) this).type == Type.DEFINITION;
	}

	public boolean isRecv() {
		return this instanceof EGroumDataEdge && ((EGroumDataEdge) this).type == Type.RECEIVER;
	}

	public boolean isCond() {
		return this instanceof EGroumDataEdge && ((EGroumDataEdge) this).type == Type.CONDITION;
	}

	public void delete() {
		this.source.outEdges.remove(this);
		this.target.inEdges.remove(this);
		this.source = null;
		this.target = null;
	}

	public static void createEdge(EGroumNode source, EGroumNode target, EGroumEdge e) {
		if (e instanceof EGroumDataEdge)
			new EGroumDataEdge(source, target, ((EGroumDataEdge) e).type, ((EGroumDataEdge) e).label);
		if (e instanceof EGroumControlEdge)
			new EGroumControlEdge(source, target, ((EGroumControlEdge) e).label);
	}

	public boolean isDirect() {
		HashSet<EGroumNode> inter = this.source.getOutNodes();
		inter.retainAll(this.target.getInNodes());
		return inter.isEmpty();
	}
	
	@Override
	public String toString() {
		return source + "-" + getLabel() +"->" + target;
	}
}
