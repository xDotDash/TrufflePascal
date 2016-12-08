package cz.cuni.mff.d3s.trupple.language.parser;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import cz.cuni.mff.d3s.trupple.language.customtypes.ICustomType;
import cz.cuni.mff.d3s.trupple.language.nodes.StatementNode;
import cz.cuni.mff.d3s.trupple.language.parser.identifierstable.IdentifiersTable;
import cz.cuni.mff.d3s.trupple.language.parser.identifierstable.types.OrdinalDescriptor;
import cz.cuni.mff.d3s.trupple.language.parser.identifierstable.types.TypeDescriptor;
import cz.cuni.mff.d3s.trupple.language.runtime.PascalContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Notes:
 * constants must be separated from the localIdentifiers because local identifiers only store descriptors of their types while
 * constants need to hold their value through the process of compiling the source code (e.g.:so they can be used in case statements
 */

class LexicalScope {


    private final String name;
    private final LexicalScope outer;
    private final IdentifiersTable localIdentifiers;
    private int loopDepth;

    private final PascalContext context;
    private final Map<String, Object> localConstants;
    private final Map<String, ICustomType> customTypes;
    private final List<StatementNode> initializationNodes;


    private FrameSlot returnSlot;

    List<StatementNode> scopeNodes = new ArrayList<>();

    LexicalScope(LexicalScope outer, String name) {
        this.name = name;
        this.outer = outer;
        this.returnSlot = null;
        this.initializationNodes = new ArrayList<>();
        this.localConstants = new HashMap<>();
        this.customTypes = new HashMap<>();

        this.localIdentifiers = new IdentifiersTable();

        this.context = (outer != null)? new PascalContext(outer.context) : new PascalContext(null);
    }

    String getName() {
        return this.name;
    }

    LexicalScope getOuterScope() {
        return this.outer;
    }

    PascalContext getContext() {
        return this.context;
    }

    FrameDescriptor getFrameDescriptor() {
        return this.localIdentifiers.getFrameDescriptor();
    }

    FrameSlot getLocalSlot(String identifier) {
        return this.localIdentifiers.getFrameSlot(identifier);
    }

    boolean isVariable(String identifier) {
        return this.localIdentifiers.isVariable(identifier);
    }

    boolean isParameterlessSubroutine(String identifier) {
        return this.localIdentifiers.isParameterlessSubroutine(identifier);
    }

    boolean isSubroutine(String identifier) {
        return this.localIdentifiers.isSubroutine(identifier);
    }

    boolean containsLocalIdentifier(String identifier) {
        return this.localIdentifiers.containsIdentifier(identifier);
    }

    void registerLocalVariable(String identifier, String typeName) throws LexicalException {
        this.localIdentifiers.addVariable(typeName, identifier);
    }

    void registerLocalArrayVariable(String identifier, List<OrdinalDescriptor> ordinalDimensions, String returnTypeName) throws LexicalException {
        this.localIdentifiers.addArrayVariable(identifier, ordinalDimensions, returnTypeName);
    }

    void registerEnumType(String identifier, List<String> identifiers) throws LexicalException {
        this.localIdentifiers.addEnumType(identifier, identifiers);
    }

    void registerProcedureInterface(String identifier, List<FormalParameter> formalParameters) throws LexicalException {
        this.localIdentifiers.addProcedureInterface(identifier, formalParameters);
    }

    void registerFunctionInterface(String identifier, List<FormalParameter> formalParameters, String returnType) throws LexicalException {
        this.localIdentifiers.addFunctionInterface(identifier, formalParameters, returnType);
    }

    OrdinalDescriptor createRangeDescriptor(int lowerBound, int upperBound)  throws LexicalException {
        if (upperBound < lowerBound) {
            throw new LexicalException("Lower upper bound than lower bound.");
        }
        return new OrdinalDescriptor.RangeDescriptor(lowerBound, upperBound);
    }

    // TODO: make enums, integer, long, arrays,... ordinal
    OrdinalDescriptor createRangeDescriptorFromTypename(String typeName) throws LexicalException {
        TypeDescriptor typeDescriptor = this.localIdentifiers.getTypeDescriptor(typeName);
        if (typeDescriptor == null) {
            throw new LexicalException("Unknown type: " + typeName + ".");
        }
        else if (!(typeDescriptor instanceof OrdinalDescriptor)) {
            throw new LexicalException("The type is not ordinal: " + typeName + ".");
        }
        else {
            return (OrdinalDescriptor)typeDescriptor;
        }
    }

    OrdinalDescriptor createImplicitRangeDescriptor() {
        return new OrdinalDescriptor.RangeDescriptor(0, 1);
    }

    List<StatementNode> createInitializationNodes() {
        // TODO: implement this
        return null;
    }

    void increaseLoopDepth() {
        ++loopDepth;
    }

    void decreaseLoopDepth() throws LexicalException {
        if (loopDepth == 0) {
            throw new LexicalException("Cannot leave cycle.");
        } else {
            --loopDepth;
        }
    }

    boolean isInLoop() {
        return loopDepth > 0;
    }


    // THE FRAME DESCRIPTOR WILL BE CREATED AFTER THE PARSING IS FINISHED
    // INITIALIZATION NODES:
    //  PRIMITIVES:
    //   new ...
    //  ARRAY:
    //   PascalArray array = createMultidimensionalArray(ordinalDimensions, returnTypeName);
    //   this.addInitializationNode(InitializationNodeFactory.create(newSlot, array));
    // ----------------------------

    Map<String, ICustomType> getAllCustomTypes() {
        return this.customTypes;
    }

    Object getLocalConstant(String identifier) {
        return this.localConstants.get(identifier);
    }

    FrameSlot getReturnSlot() {
        return this.returnSlot;
    }

    FrameSlot getVisibleSlot(String identifier) {
        FrameSlot slot = null;
        LexicalScope scope = this;
        while(scope != null && slot == null){
            slot = scope.getLocalSlot(identifier);
            scope = scope.getOuterScope();
        }

        return slot;
    }

    void registerCustomType(String name, ICustomType customType) {
        this.customTypes.put(name, customType);
    }

    FrameSlot registerLocalConstant(String identifier, Object value) throws IllegalArgumentException {
        try {
            FrameSlot newSlot = frameDescriptor.addFrameSlot(identifier);
            this.localIdentifiers.put(identifier, newSlot);
            this.localConstants.put(identifier, value);
            return newSlot;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Duplicate variable: " + identifier + ".");
        }
    }

    void addInitializationNode(StatementNode initializationNode) {
        this.initializationNodes.add(initializationNode);
    }

    boolean containsLocalConstant(String identifier) {
        return this.localConstants.containsKey(identifier);
    }
}