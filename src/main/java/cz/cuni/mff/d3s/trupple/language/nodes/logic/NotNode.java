package cz.cuni.mff.d3s.trupple.language.nodes.logic;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import cz.cuni.mff.d3s.trupple.language.nodes.arithmetic.UnaryNode;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.TypeDescriptor;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.primitive.BooleanDescriptor;

/**
 * Node representing logical not operation.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link NotNodeGen}
 */
@NodeInfo(shortName = "!")
public abstract class NotNode extends UnaryNode {

	@Override
	public abstract boolean executeBoolean(VirtualFrame frame);

	@Specialization
	boolean logicalNot(boolean child) {
		return !child;
	}

    @Override
    public TypeDescriptor getType() {
        return BooleanDescriptor.getInstance();
    }

    @Override
    public boolean verifyChildrenNodeTypes() {
        return this.getArgument().getType() == BooleanDescriptor.getInstance();
    }

}
