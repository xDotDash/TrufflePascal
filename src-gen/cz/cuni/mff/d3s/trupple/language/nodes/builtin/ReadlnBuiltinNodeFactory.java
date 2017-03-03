// CheckStyle: start generated
package cz.cuni.mff.d3s.trupple.language.nodes.builtin;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import cz.cuni.mff.d3s.trupple.language.nodes.ExpressionNode;
import cz.cuni.mff.d3s.trupple.language.runtime.PascalContext;
import java.util.Arrays;
import java.util.List;

@GeneratedBy(ReadlnBuiltinNode.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public final class ReadlnBuiltinNodeFactory implements NodeFactory<ReadlnBuiltinNode> {

    private static ReadlnBuiltinNodeFactory instance;

    private ReadlnBuiltinNodeFactory() {
    }

    @Override
    public Class<ReadlnBuiltinNode> getNodeClass() {
        return ReadlnBuiltinNode.class;
    }

    @Override
    public List getExecutionSignature() {
        return Arrays.asList(ExpressionNode.class);
    }

    @Override
    public List getNodeSignatures() {
        return Arrays.asList(Arrays.asList(ExpressionNode[].class, PascalContext.class));
    }

    @Override
    public ReadlnBuiltinNode createNode(Object... arguments) {
        if (arguments.length == 2 && (arguments[0] == null || arguments[0] instanceof ExpressionNode[]) && (arguments[1] == null || arguments[1] instanceof PascalContext)) {
            return create((ExpressionNode[]) arguments[0], (PascalContext) arguments[1]);
        } else {
            throw new IllegalArgumentException("Invalid create signature.");
        }
    }

    public static NodeFactory<ReadlnBuiltinNode> getInstance() {
        if (instance == null) {
            instance = new ReadlnBuiltinNodeFactory();
        }
        return instance;
    }

    public static ReadlnBuiltinNode create(ExpressionNode[] arguments, PascalContext context) {
        return new ReadlnBuiltinNodeGen(arguments, context);
    }

    @GeneratedBy(ReadlnBuiltinNode.class)
    public static final class ReadlnBuiltinNodeGen extends ReadlnBuiltinNode {

        private final PascalContext context;
        @Child private ExpressionNode arguments0_;
        @CompilationFinal private boolean seenUnsupported0;

        private ReadlnBuiltinNodeGen(ExpressionNode[] arguments, PascalContext context) {
            this.context = context;
            this.arguments0_ = arguments != null && 0 < arguments.length ? arguments[0] : null;
        }

        @Override
        public PascalContext getContext() {
            return this.context;
        }

        @Override
        public NodeCost getCost() {
            return NodeCost.MONOMORPHIC;
        }

        @Override
        public Object executeGeneric(VirtualFrame frameValue) {
            Object[] arguments0Value_;
            try {
                arguments0Value_ = expectObjectArray(arguments0_.executeGeneric(frameValue));
            } catch (UnexpectedResultException ex) {
                throw unsupported(ex.getResult());
            }
            return this.read(arguments0Value_);
        }

        @Override
        public void executeVoid(VirtualFrame frameValue) {
            executeGeneric(frameValue);
            return;
        }

        private UnsupportedSpecializationException unsupported(Object arguments0Value) {
            if (!seenUnsupported0) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                seenUnsupported0 = true;
            }
            return new UnsupportedSpecializationException(this, new Node[] {arguments0_}, arguments0Value);
        }

        private static Object[] expectObjectArray(Object value) throws UnexpectedResultException {
            if (value instanceof Object[]) {
                return (Object[]) value;
            }
            throw new UnexpectedResultException(value);
        }

    }
}
