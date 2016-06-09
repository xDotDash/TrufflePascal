package pascal.language.nodes.literals;

import com.oracle.truffle.api.frame.VirtualFrame;

import pascal.language.nodes.ExpressionNode;

public class LogicLiteralNode extends ExpressionNode {
	
	private final boolean value;
	
	public LogicLiteralNode(boolean value){
		this.value = value;
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		return value;
	}
}
