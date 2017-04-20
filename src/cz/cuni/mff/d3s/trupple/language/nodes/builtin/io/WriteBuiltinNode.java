package cz.cuni.mff.d3s.trupple.language.nodes.builtin.io;

import java.io.PrintStream;
import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import cz.cuni.mff.d3s.trupple.language.customvalues.FileValue;
import cz.cuni.mff.d3s.trupple.language.nodes.ExpressionNode;
import cz.cuni.mff.d3s.trupple.language.nodes.StatementNode;
import cz.cuni.mff.d3s.trupple.language.nodes.builtin.BuiltinNode;
import cz.cuni.mff.d3s.trupple.language.runtime.PascalContext;

@NodeInfo(shortName = "write")
@NodeChild(value = "arguments", type = ExpressionNode[].class)
@GenerateNodeFactory
public abstract class WriteBuiltinNode extends StatementNode {

	@Specialization
	public void write(Object[] values) {
        if (values[0] instanceof FileValue) {
            FileValue file = (FileValue) values[0];
            Object[] arguments = Arrays.copyOfRange(values, 1, values.length);
            file.write(arguments);
        } else {
            doWrite(PascalContext.getInstance().getOutput(), values);
        }
	}

	@TruffleBoundary
	private static void doWrite(PrintStream out, Object[] values) {
		for (Object value : values)
			out.print(value);
	}

}
