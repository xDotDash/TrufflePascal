package pascal.language.nodes.literals;

import com.oracle.truffle.api.frame.VirtualFrame;

import pascal.language.nodes.ExpressionNode;

public class CharLiteralNode extends ExpressionNode {
	
	private final char value;
	
	public CharLiteralNode(char value){
		this.value = value;
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		return value;
	}
}
