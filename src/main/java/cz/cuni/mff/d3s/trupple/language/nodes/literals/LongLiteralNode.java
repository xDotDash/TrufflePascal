package cz.cuni.mff.d3s.trupple.language.nodes.literals;

import com.oracle.truffle.api.dsl.Specialization;

import cz.cuni.mff.d3s.trupple.language.nodes.ExpressionNode;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.TypeDescriptor;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.primitive.LongDescriptor;

/**
 * Node representing long literal.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link LongLiteralNodeGen}
 */
public abstract class LongLiteralNode extends ExpressionNode {

	private final long value;

	LongLiteralNode(long value) {
		this.value = value;
	}

	@Specialization
	public long execute() {
		return value;
	}

    @Override
    public TypeDescriptor getType() {
        return LongDescriptor.getInstance();
    }

}
