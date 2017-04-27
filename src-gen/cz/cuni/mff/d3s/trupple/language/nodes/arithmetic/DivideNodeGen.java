// CheckStyle: start generated
package cz.cuni.mff.d3s.trupple.language.nodes.arithmetic;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.dsl.internal.SpecializationNode;
import com.oracle.truffle.api.dsl.internal.SpecializedNode;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import cz.cuni.mff.d3s.trupple.language.PascalTypesGen;
import cz.cuni.mff.d3s.trupple.language.nodes.ExpressionNode;

@GeneratedBy(DivideNode.class)
public final class DivideNodeGen extends DivideNode implements SpecializedNode {

    @Child private ExpressionNode leftNode_;
    @Child private ExpressionNode rightNode_;
    @CompilationFinal private Class<?> leftNodeType_;
    @CompilationFinal private Class<?> rightNodeType_;
    @Child private BaseNode_ specialization_;

    private DivideNodeGen(ExpressionNode leftNode, ExpressionNode rightNode) {
        this.leftNode_ = leftNode;
        this.rightNode_ = rightNode;
        this.specialization_ = UninitializedNode_.create(this);
    }

    @Override
    protected ExpressionNode getLeftNode() {
        return this.leftNode_;
    }

    @Override
    protected ExpressionNode getRightNode() {
        return this.rightNode_;
    }

    @Override
    public NodeCost getCost() {
        return specialization_.getNodeCost();
    }

    @Override
    public Object executeGeneric(VirtualFrame frameValue) {
        return specialization_.execute(frameValue);
    }

    @Override
    public void executeVoid(VirtualFrame frameValue) {
        specialization_.executeVoid(frameValue);
        return;
    }

    @Override
    public SpecializationNode getSpecializationNode() {
        return specialization_;
    }

    @Override
    public Node deepCopy() {
        return SpecializationNode.updateRoot(super.deepCopy());
    }

    public static DivideNode create(ExpressionNode leftNode, ExpressionNode rightNode) {
        return new DivideNodeGen(leftNode, rightNode);
    }

    @GeneratedBy(DivideNode.class)
    private abstract static class BaseNode_ extends SpecializationNode {

        @CompilationFinal protected DivideNodeGen root;

        BaseNode_(DivideNodeGen root, int index) {
            super(index);
            this.root = root;
        }

        @Override
        protected final void setRoot(Node root) {
            this.root = (DivideNodeGen) root;
        }

        @Override
        protected final Node[] getSuppliedChildren() {
            return new Node[] {root.leftNode_, root.rightNode_};
        }

        @Override
        public final Object acceptAndExecute(Frame frameValue, Object leftNodeValue, Object rightNodeValue) {
            return this.executeDouble_((VirtualFrame) frameValue, leftNodeValue, rightNodeValue);
        }

        public abstract double executeDouble_(VirtualFrame frameValue, Object leftNodeValue, Object rightNodeValue);

        public Object execute(VirtualFrame frameValue) {
            Object leftNodeValue_ = executeLeftNode_(frameValue);
            Object rightNodeValue_ = executeRightNode_(frameValue);
            return executeDouble_(frameValue, leftNodeValue_, rightNodeValue_);
        }

        public void executeVoid(VirtualFrame frameValue) {
            execute(frameValue);
            return;
        }

        @Override
        protected final SpecializationNode createNext(Frame frameValue, Object leftNodeValue, Object rightNodeValue) {
            if (PascalTypesGen.isImplicitDouble(leftNodeValue) && PascalTypesGen.isImplicitDouble(rightNodeValue)) {
                return DivNode_.create(root, leftNodeValue, rightNodeValue);
            }
            return null;
        }

        @Override
        protected final SpecializationNode createPolymorphic() {
            return PolymorphicNode_.create(root);
        }

        protected final BaseNode_ getNext() {
            return (BaseNode_) this.next;
        }

        protected final Object executeLeftNode_(Frame frameValue) {
            Class<?> leftNodeType_ = root.leftNodeType_;
            try {
                if (leftNodeType_ == long.class) {
                    return root.leftNode_.executeLong((VirtualFrame) frameValue);
                } else if (leftNodeType_ == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    Class<?> _type = Object.class;
                    try {
                        Object _value = root.leftNode_.executeGeneric((VirtualFrame) frameValue);
                        if (_value instanceof Long) {
                            _type = long.class;
                        } else {
                            _type = Object.class;
                        }
                        return _value;
                    } finally {
                        root.leftNodeType_ = _type;
                    }
                } else {
                    return root.leftNode_.executeGeneric((VirtualFrame) frameValue);
                }
            } catch (UnexpectedResultException ex) {
                root.leftNodeType_ = Object.class;
                return ex.getResult();
            }
        }

        protected final Object executeRightNode_(Frame frameValue) {
            Class<?> rightNodeType_ = root.rightNodeType_;
            try {
                if (rightNodeType_ == long.class) {
                    return root.rightNode_.executeLong((VirtualFrame) frameValue);
                } else if (rightNodeType_ == null) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    Class<?> _type = Object.class;
                    try {
                        Object _value = root.rightNode_.executeGeneric((VirtualFrame) frameValue);
                        if (_value instanceof Long) {
                            _type = long.class;
                        } else {
                            _type = Object.class;
                        }
                        return _value;
                    } finally {
                        root.rightNodeType_ = _type;
                    }
                } else {
                    return root.rightNode_.executeGeneric((VirtualFrame) frameValue);
                }
            } catch (UnexpectedResultException ex) {
                root.rightNodeType_ = Object.class;
                return ex.getResult();
            }
        }

    }
    @GeneratedBy(DivideNode.class)
    private static final class UninitializedNode_ extends BaseNode_ {

        UninitializedNode_(DivideNodeGen root) {
            super(root, 2147483647);
        }

        @Override
        public double executeDouble_(VirtualFrame frameValue, Object leftNodeValue, Object rightNodeValue) {
            return (double) uninitialized(frameValue, leftNodeValue, rightNodeValue);
        }

        static BaseNode_ create(DivideNodeGen root) {
            return new UninitializedNode_(root);
        }

    }
    @GeneratedBy(DivideNode.class)
    private static final class PolymorphicNode_ extends BaseNode_ {

        PolymorphicNode_(DivideNodeGen root) {
            super(root, 0);
        }

        @Override
        public SpecializationNode merge(SpecializationNode newNode, Frame frameValue, Object leftNodeValue, Object rightNodeValue) {
            return polymorphicMerge(newNode, super.merge(newNode, frameValue, leftNodeValue, rightNodeValue));
        }

        @Override
        public double executeDouble_(VirtualFrame frameValue, Object leftNodeValue, Object rightNodeValue) {
            return getNext().executeDouble_(frameValue, leftNodeValue, rightNodeValue);
        }

        static BaseNode_ create(DivideNodeGen root) {
            return new PolymorphicNode_(root);
        }

    }
    @GeneratedBy(methodName = "div(double, double)", value = DivideNode.class)
    private static final class DivNode_ extends BaseNode_ {

        private final Class<?> leftNodeImplicitType;
        private final Class<?> rightNodeImplicitType;

        DivNode_(DivideNodeGen root, Object leftNodeValue, Object rightNodeValue) {
            super(root, 1);
            this.leftNodeImplicitType = PascalTypesGen.getImplicitDoubleClass(leftNodeValue);
            this.rightNodeImplicitType = PascalTypesGen.getImplicitDoubleClass(rightNodeValue);
        }

        @Override
        public boolean isSame(SpecializationNode other) {
            return super.isSame(other) && this.leftNodeImplicitType == ((DivNode_) other).leftNodeImplicitType && this.rightNodeImplicitType == ((DivNode_) other).rightNodeImplicitType;
        }

        @Override
        public double executeDouble_(VirtualFrame frameValue, Object leftNodeValue, Object rightNodeValue) {
            if (PascalTypesGen.isImplicitDouble(leftNodeValue, leftNodeImplicitType) && PascalTypesGen.isImplicitDouble(rightNodeValue, rightNodeImplicitType)) {
                double leftNodeValue_ = PascalTypesGen.asImplicitDouble(leftNodeValue, leftNodeImplicitType);
                double rightNodeValue_ = PascalTypesGen.asImplicitDouble(rightNodeValue, rightNodeImplicitType);
                return root.div(leftNodeValue_, rightNodeValue_);
            }
            return getNext().executeDouble_(frameValue, leftNodeValue, rightNodeValue);
        }

        static BaseNode_ create(DivideNodeGen root, Object leftNodeValue, Object rightNodeValue) {
            return new DivNode_(root, leftNodeValue, rightNodeValue);
        }

    }
}
