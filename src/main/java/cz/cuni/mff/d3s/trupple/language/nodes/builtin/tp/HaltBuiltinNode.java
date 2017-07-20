package cz.cuni.mff.d3s.trupple.language.nodes.builtin.tp;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import cz.cuni.mff.d3s.trupple.language.nodes.ExpressionNode;
import cz.cuni.mff.d3s.trupple.language.nodes.statement.StatementNode;
import cz.cuni.mff.d3s.trupple.language.runtime.exceptions.HaltExceptionTP;

/**
 * Node representing Turbo Pascal's exit function. When the procedure is executed, it immediately stops the program's
 * execution returning integer value that was provided to the exit function as an argument.
 *
 * This node uses specializations which means that it is not used directly but completed node is generated by Truffle.
 * {@link HaltBuiltinNodeGen}
 */
@NodeInfo(shortName = "exit")
@NodeChild(value = "argument", type = ExpressionNode.class)
public abstract class HaltBuiltinNode extends StatementNode {

    @Specialization
	void exit(int returnValue) {
        throw new HaltExceptionTP(returnValue);
	}

}
