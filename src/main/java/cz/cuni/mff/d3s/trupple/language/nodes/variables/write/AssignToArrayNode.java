package cz.cuni.mff.d3s.trupple.language.nodes.variables.write;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import cz.cuni.mff.d3s.trupple.language.nodes.ExpressionNode;
import cz.cuni.mff.d3s.trupple.language.nodes.statement.StatementNode;
import cz.cuni.mff.d3s.trupple.language.nodes.variables.ReadIndexNode;
import cz.cuni.mff.d3s.trupple.language.runtime.customvalues.PCharValue;
import cz.cuni.mff.d3s.trupple.language.runtime.customvalues.PascalString;

@NodeChildren({
        @NodeChild(value = "arrayNode", type = ExpressionNode.class),
        @NodeChild(value = "indexNode", type = ReadIndexNode.class),
        @NodeChild(value = "valueNode", type = ExpressionNode.class)
})
public abstract class AssignToArrayNode extends StatementNode {

    @Specialization
    void assignLong(long[] array, int index, long value) {
        array[index] = value;
    }

    @Specialization
    void assignDouble(double[] array, int index, double value) {
        array[index] = value;
    }

    @Specialization
    void assignChar(char[] array, int index, char value) {
        array[index] = value;
    }

    @Specialization
    void assignBoolean(boolean[] array, int index, boolean value) {
        array[index] = value;
    }

    @Specialization
    void assignToString(PascalString string, int index, char value) {
        string.setValueAt(index, value);
    }

    @Specialization
    void assignToPChar(PCharValue string, int index, char value) {
        string.setValueAt(index, value);
    }

    @Specialization
    void assignObject(Object[] array, int index, Object value) {
        array[index] = value;
    }

}
