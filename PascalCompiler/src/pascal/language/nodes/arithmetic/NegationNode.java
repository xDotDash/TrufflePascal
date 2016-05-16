package pascal.language.nodes.arithmetic;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import pascal.language.nodes.UnaryNode;


@NodeInfo(shortName = "neg")
public abstract class NegationNode extends UnaryNode{

	@Specialization(rewriteOn = ArithmeticException.class)
    protected long add(long val) {
		return -val;
    }
}