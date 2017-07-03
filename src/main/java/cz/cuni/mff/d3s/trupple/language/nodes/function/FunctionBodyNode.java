package cz.cuni.mff.d3s.trupple.language.nodes.function;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.NodeFields;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;

import cz.cuni.mff.d3s.trupple.language.nodes.ExpressionNode;
import cz.cuni.mff.d3s.trupple.language.nodes.statement.StatementNode;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.TypeDescriptor;

/**
 * Node representing body of a function. It contains a body node which is executed. The returning value is stored in a
 * write only variable of the same name as the function. The value of this variable is returned after the body node is
 * executed.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * The correct specialization is chosen by the return type of the function provided in the constructor of the generated
 * class.
 * {@link FunctionBodyNodeGen}
 */
@NodeFields({
    @NodeField(name = "slot", type = FrameSlot.class),
    @NodeField(name = "typeDescriptor", type = TypeDescriptor.class)
})
public abstract class FunctionBodyNode extends ExpressionNode {

	protected abstract FrameSlot getSlot();
    protected abstract TypeDescriptor getTypeDescriptor();

	@Child
	private StatementNode bodyNode;

	FunctionBodyNode(StatementNode bodyNode) {
		this.bodyNode = bodyNode;
	}

	@Specialization(guards = "isInt()", rewriteOn = FrameSlotTypeException.class)
    int executeIntFunction(VirtualFrame frame) throws FrameSlotTypeException {
	    bodyNode.executeVoid(frame);
        return frame.getInt(getSlot());
    }

    @Specialization(guards = "isLong()", rewriteOn = FrameSlotTypeException.class)
    long executeLongFunction(VirtualFrame frame) throws FrameSlotTypeException {
        bodyNode.executeVoid(frame);
        return frame.getLong(getSlot());
    }

    @Specialization(guards = "isBoolean()", rewriteOn = FrameSlotTypeException.class)
    boolean executeBooleanFunction(VirtualFrame frame) throws FrameSlotTypeException {
        bodyNode.executeVoid(frame);
        return frame.getBoolean(getSlot());
    }

    @Specialization(guards = "isChar()", rewriteOn = FrameSlotTypeException.class)
    char executeCharFunction(VirtualFrame frame) throws FrameSlotTypeException {
        bodyNode.executeVoid(frame);
        return (char) frame.getByte(getSlot());
    }

    @Specialization(guards = "isDouble()", rewriteOn = FrameSlotTypeException.class)
    double executeDoubleFunction(VirtualFrame frame) throws FrameSlotTypeException {
        bodyNode.executeVoid(frame);
        return frame.getDouble(getSlot());
    }

	@Specialization
    Object executeFunction(VirtualFrame frame) {
        bodyNode.executeVoid(frame);
        return frame.getValue(getSlot());
    }

    @Override
    public TypeDescriptor getType() {
        return this.getTypeDescriptor();
    }

}
