package cz.cuni.mff.d3s.trupple.language.nodes.builtin.arithmetic;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import cz.cuni.mff.d3s.trupple.language.nodes.ExpressionNode;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.TypeDescriptor;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.primitive.RealDescriptor;

/**
 * Node representing Pascal's built in subroutine cos.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link CosBuiltinNodeGen}
 */
@NodeInfo(shortName = "cos")
@NodeChild(value = "argument", type = ExpressionNode.class)
public abstract class CosBuiltinNode extends ExpressionNode {

    @Specialization
    double cos(double value) {
        return Math.cos(value);
    }

    @Override
    public TypeDescriptor getType() {
        return RealDescriptor.getInstance();
    }

}
