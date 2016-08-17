package cz.cuni.mff.d3s.trupple.exceptions;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class BreakException extends ControlFlowException {

	private static final long serialVersionUID = 1461738434684232542L;

	protected BreakException() {

	}

	public static BreakException SINGLETON = new BreakException();
}
