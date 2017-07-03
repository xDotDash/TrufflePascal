package cz.cuni.mff.d3s.trupple.language.nodes.builtin.allocation;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import cz.cuni.mff.d3s.trupple.language.runtime.customvalues.PointerValue;
import cz.cuni.mff.d3s.trupple.language.nodes.ExpressionNode;
import cz.cuni.mff.d3s.trupple.language.nodes.statement.StatementNode;
import cz.cuni.mff.d3s.trupple.language.runtime.heap.HeapSlot;
import cz.cuni.mff.d3s.trupple.language.runtime.heap.PascalHeap;

/**
 * Node representing Pascal's built in new subroutine. It allocates new object on the heap.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link NewBuiltinNodeGen}
 */
@NodeInfo(shortName = "new")
@NodeChild(value = "argument", type = ExpressionNode.class)
public abstract class NewBuiltinNode extends StatementNode {

    @Specialization
    void allocate(PointerValue pointerValue) {
        Object newObject = pointerValue.getType().getDefaultValue();
        HeapSlot heapSlot = PascalHeap.getInstance().allocateNewObject(newObject);
        pointerValue.setHeapSlot(heapSlot);
    }
}
