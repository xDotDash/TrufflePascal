package pascal.language.nodes.literals;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;

import pascal.language.PascalLanguage;
import pascal.language.nodes.ExpressionNode;
import pascal.language.runtime.PascalContext;
import pascal.language.runtime.PascalFunction;

@NodeInfo(shortName = "func")
public final class FunctionLiteralNode extends ExpressionNode{
	private final String value;
    //private final Node contextNode;
    @CompilationFinal private PascalFunction cachedFunction;
    @CompilationFinal private PascalContext cachedContext;
    
    private final PascalContext context;
    
    public FunctionLiteralNode(PascalContext context, String value){
    	this.value = value;
    	this.context = context;
    	//contextNode = PascalLanguage.INSTANCE.createFindContextNode1();
    }
    
    @Override
    public PascalFunction executeGeneric(VirtualFrame frame){
    	//PascalContext context = PascalLanguage.INSTANCE.findContext1(contextNode);
        if (context != cachedContext) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            this.cachedContext = context;
            this.cachedFunction = context.getFunctionRegistry().lookup(value);
        }
        return cachedFunction;
    }
}
