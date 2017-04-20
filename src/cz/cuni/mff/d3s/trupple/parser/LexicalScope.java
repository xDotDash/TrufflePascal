package cz.cuni.mff.d3s.trupple.parser;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import cz.cuni.mff.d3s.trupple.language.nodes.BlockNode;
import cz.cuni.mff.d3s.trupple.language.nodes.ExpressionNode;
import cz.cuni.mff.d3s.trupple.language.nodes.root.PascalRootNode;
import cz.cuni.mff.d3s.trupple.language.nodes.StatementNode;
import cz.cuni.mff.d3s.trupple.language.runtime.PascalSubroutine;
import cz.cuni.mff.d3s.trupple.language.runtime.exceptions.PascalRuntimeException;
import cz.cuni.mff.d3s.trupple.parser.exceptions.LexicalException;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.IdentifiersTable;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.IdentifiersTableTP;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.TypeDescriptor;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.complex.FileDescriptor;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.complex.OrdinalDescriptor;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.complex.PointerDescriptor;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.compound.RecordDescriptor;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.constant.ConstantDescriptor;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.constant.LongConstantDescriptor;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.constant.OrdinalConstantDescriptor;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.subroutine.ReturnTypeDescriptor;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.subroutine.SubroutineDescriptor;
import java.util.*;

public class LexicalScope {

    private String name;
    private final LexicalScope outer;
    private int loopDepth;
    private final Set<String> publicIdentifiers;

    final IdentifiersTable localIdentifiers;
    final List<StatementNode> scopeInitializationNodes = new ArrayList<>();

    LexicalScope(LexicalScope outer, String name, boolean usingTPExtension) {
        this.name = name;
        this.outer = outer;
        this.publicIdentifiers = new HashSet<>();
        this.localIdentifiers = (usingTPExtension)? new IdentifiersTableTP() : new IdentifiersTable();
    }

    String getName() {
        return this.name;
    }

    LexicalScope getOuterScope() {
        return this.outer;
    }


    public IdentifiersTable getIdentifiersTable() {
        return this.localIdentifiers;
    }

    public FrameDescriptor getFrameDescriptor() {
        return this.localIdentifiers.getFrameDescriptor();
    }

    FrameSlot getLocalSlot(String identifier) {
        return this.localIdentifiers.getFrameSlot(identifier);
    }

    PascalSubroutine getSubroutine(String identifier) throws LexicalException {
        return this.localIdentifiers.getSubroutine(identifier);
    }

    FrameSlotKind getSlotKind(String identifier) {
        return this.localIdentifiers.getFrameSlotKind(identifier);
    }

    FrameSlot getReturnSlot() {
        return this.localIdentifiers.getFrameSlot(this.name);
    }

    TypeDescriptor getIdentifierDescriptor(String identifier) {
        return this.localIdentifiers.getIdentifierDescriptor(identifier);
    }

    TypeDescriptor getTypeDescriptor(String identifier) {
        return this.localIdentifiers.getTypeDescriptor(identifier);
    }

    ConstantDescriptor getConstant(String identifier) throws LexicalException {
        return this.localIdentifiers.getConstant(identifier);
    }

    public void setName(String identifier) {
        this.name = identifier;
    }

    void setSubroutineRootNode(String identifier, PascalRootNode rootNode) throws LexicalException {
        this.localIdentifiers.setSubroutineRootNode(identifier, rootNode);
    }

    void registerLabel(String identifier) throws LexicalException {
        this.localIdentifiers.addLabel(identifier);
    }

    void registerNewType(String identifier, TypeDescriptor typeDescriptor) throws LexicalException {
        this.localIdentifiers.addType(identifier, typeDescriptor);
    }

    boolean isReferenceParameter(String identifier, int parameterIndex) throws LexicalException {
        TypeDescriptor subroutineDescriptor = this.localIdentifiers.getIdentifierDescriptor(identifier);
        if (!(subroutineDescriptor instanceof SubroutineDescriptor)) {
            throw new LexicalException("Not a subroutine: " + identifier);
        }

        SubroutineDescriptor descriptor = (SubroutineDescriptor)subroutineDescriptor;
        return descriptor.isReferenceParameter(parameterIndex);
    }

    boolean isSubroutineParameter(String identifier, int parameterIndex) throws LexicalException {
        TypeDescriptor subroutineDescriptor = this.localIdentifiers.getIdentifierDescriptor(identifier);
        if (!(subroutineDescriptor instanceof SubroutineDescriptor)) {
            throw new LexicalException("Not a subroutine: " + identifier);
        }

        SubroutineDescriptor descriptor = (SubroutineDescriptor)subroutineDescriptor;
        return descriptor.isSubroutineParameter(parameterIndex);
    }

    boolean isParameterlessSubroutine(String identifier) {
        return this.localIdentifiers.isParameterlessSubroutine(identifier);
    }

    boolean isSubroutine(String identifier) {
        return this.localIdentifiers.isSubroutine(identifier);
    }

    boolean labelExists(String identifier) {
        return this.localIdentifiers.isLabel(identifier);
    }

    void verifyPassedArgumentsToSubroutine(String identifier, List<ExpressionNode> params) throws LexicalException {
        this.localIdentifiers.verifyPassedArgumentsToSubroutine(identifier, params);
    }

    boolean containsLocalIdentifier(String identifier) {
        return this.localIdentifiers.containsIdentifier(identifier) && !(this.localIdentifiers.getIdentifierDescriptor(identifier) instanceof ReturnTypeDescriptor);
    }

    boolean containsReturnType(String identifier) {
        return this.localIdentifiers.containsIdentifier(identifier) && (this.localIdentifiers.getIdentifierDescriptor(identifier) instanceof ReturnTypeDescriptor);
    }

    FrameSlot registerReferenceVariable(String identifier, TypeDescriptor typeDescriptor) throws LexicalException {
        return this.localIdentifiers.addReference(identifier, typeDescriptor);
    }

    FrameSlot registerLocalVariable(String identifier, TypeDescriptor typeDescriptor) throws LexicalException {
        return this.localIdentifiers.addVariable(identifier, typeDescriptor);
    }

    void addScopeInitializationNode(StatementNode initializationNode) {
        this.scopeInitializationNodes.add(initializationNode);
    }

    FrameSlot registerReturnVariable(String identifier, TypeDescriptor typeDescriptor) throws LexicalException {
        return this.localIdentifiers.addReturnVariable(identifier, typeDescriptor);
    }

    TypeDescriptor createArrayType(List<OrdinalDescriptor> ordinalDimensions, TypeDescriptor typeDescriptor) {
        return this.localIdentifiers.createArray(ordinalDimensions, typeDescriptor);
    }

    TypeDescriptor createEnumType(List<String> identifiers) throws LexicalException {
        return this.localIdentifiers.createEnum(identifiers);
    }

    FileDescriptor createFileDescriptor(TypeDescriptor contentTypeDescriptor) {
        return this.localIdentifiers.createFileDescriptor(contentTypeDescriptor);
    }

    RecordDescriptor createRecordDescriptor() {
        return this.localIdentifiers.createRecordDescriptor(this);
    }

    PointerDescriptor createPointerDescriptor(String innerTypeIdentifier) {
        return this.localIdentifiers.createPointerDescriptor(innerTypeIdentifier);
    }

    TypeDescriptor createSetType(OrdinalDescriptor baseType) {
        return this.localIdentifiers.createSetType(baseType);
    }

    void initializeAllUninitializedPointerDescriptors() throws LexicalException {
        this.localIdentifiers.initializeAllUninitializedPointerDescriptors();
    }

    void registerProcedureInterfaceIfNotForwarded(String identifier, List<FormalParameter> formalParameters) throws LexicalException {
        this.localIdentifiers.addProcedureInterfaceIfNotForwarded(identifier, formalParameters);
    }

    void registerFunctionInterfaceIfNotForwarded(String identifier, List<FormalParameter> formalParameters, TypeDescriptor returnTypeDescriptor) throws LexicalException {
        this.localIdentifiers.addFunctionInterfaceIfNotForwarded(identifier, formalParameters, returnTypeDescriptor);
    }

    void forwardProcedure(String identifier, List<FormalParameter> formalParameters) throws LexicalException {
        this.localIdentifiers.forwardProcedure(identifier, formalParameters);
    }

    void forwardFunction(String identifier, List<FormalParameter> formalParameters, TypeDescriptor returnTypeDescriptor) throws LexicalException {
        this.localIdentifiers.forwardFunction(identifier, formalParameters, returnTypeDescriptor);
    }

    public void registerBuiltinSubroutine(String identifier, SubroutineDescriptor descriptor) {
        try {
            this.localIdentifiers.addSubroutine(identifier, descriptor);
        } catch (LexicalException e) {
            throw new PascalRuntimeException("Could not register builtin subroutine: " + identifier);
        }
    }

    public void registerType(String identifier, TypeDescriptor type) {
        try {
            this.localIdentifiers.addType(identifier, type);
        } catch (LexicalException e) {
            // TODO: this is called from BuiltinUnitAbstr only, so this should not happen
        }
    }

    void registerConstant(String identifier, ConstantDescriptor constant) throws LexicalException {
        this.localIdentifiers.addConstant(identifier, constant);
    }

    OrdinalDescriptor createRangeDescriptor(OrdinalConstantDescriptor lowerBound, OrdinalConstantDescriptor upperBound)  throws LexicalException {
        if (!lowerBound.getClass().equals(upperBound.getClass())) {
            throw new LexicalException("Range type mismatch");
        }

        long lower = lowerBound.getOrdinalValue();
        long upper = upperBound.getOrdinalValue();

        if (lower > upper) {
            throw new LexicalException("Lower upper bound than lower bound");
        }

        return new OrdinalDescriptor.RangeDescriptor(lowerBound, upperBound);
    }

    OrdinalDescriptor createImplicitRangeDescriptor() {
        return new OrdinalDescriptor.RangeDescriptor(new LongConstantDescriptor(0), new LongConstantDescriptor(1));
    }

    BlockNode createInitializationNode() {
        InitializationNodeGenerator initNodeGenerator = new InitializationNodeGenerator(this.localIdentifiers);
        List<StatementNode> initializationNodes = initNodeGenerator.generate();
        initializationNodes.addAll(this.scopeInitializationNodes);

        return new BlockNode(initializationNodes.toArray(new StatementNode[initializationNodes.size()]));
    }

    void markAllIdentifiersPublic() {
        Map<String, TypeDescriptor> allIdentifiers = this.localIdentifiers.getAllIdentifiers();
        for (Map.Entry<String, TypeDescriptor> entry : allIdentifiers.entrySet()) {
            String currentIdentifier = entry.getKey();
            this.publicIdentifiers.add(currentIdentifier);
        }
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
}