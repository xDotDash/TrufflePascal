package cz.cuni.mff.d3s.trupple.language.nodes.control;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import cz.cuni.mff.d3s.trupple.exceptions.BreakException;
import cz.cuni.mff.d3s.trupple.language.nodes.ExpressionNode;
import cz.cuni.mff.d3s.trupple.language.nodes.StatementNode;

@NodeInfo(shortName = "repeat", description = "The node implementing a repeat loop")
public class RepeatNode extends StatementNode {

	@Child
	private ExpressionNode condition;
	@Child
	private StatementNode body;

	public RepeatNode(ExpressionNode condition, StatementNode body) {
		this.body = body;
		this.condition = condition;
	}

	@Override
	public void executeVoid(VirtualFrame frame) {
		body.executeVoid(frame);
		try {
			while (!condition.executeBoolean(frame)) {
				body.executeVoid(frame);
			}
		} catch (BreakException e) {
		} catch (UnexpectedResultException e) {
			// TODO HANDLE THIS ERROR
		}
	}
}