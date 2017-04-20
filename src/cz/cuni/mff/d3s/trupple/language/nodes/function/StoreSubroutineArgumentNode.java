package cz.cuni.mff.d3s.trupple.language.nodes.function;

import com.oracle.truffle.api.frame.VirtualFrame;
import cz.cuni.mff.d3s.trupple.language.nodes.ExpressionNode;
import cz.cuni.mff.d3s.trupple.language.runtime.PascalSubroutine;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.TypeDescriptor;

// TODO: this may be removed and replaced with ReadVariableNode()
public class StoreSubroutineArgumentNode extends ExpressionNode {

    private final PascalSubroutine function;

    public StoreSubroutineArgumentNode(PascalSubroutine function) {
        this.function = function;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return function;
    }

    @Override
    public TypeDescriptor getType() {
        return null;
    }

}
