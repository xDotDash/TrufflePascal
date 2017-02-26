
package cz.cuni.mff.d3s.trupple.language.parser.tp;

import com.oracle.truffle.api.nodes.RootNode;
import cz.cuni.mff.d3s.trupple.language.nodes.*;
import cz.cuni.mff.d3s.trupple.language.parser.*;
import cz.cuni.mff.d3s.trupple.language.parser.identifierstable.types.OrdinalDescriptor;
import cz.cuni.mff.d3s.trupple.language.parser.identifierstable.types.TypeDescriptor;
import com.oracle.truffle.api.source.Source;
import java.util.ArrayList;
import java.util.List;

public class Parser implements IParser {
	public static final int _EOF = 0;
	public static final int _identifier = 1;
	public static final int _stringLiteral = 2;
	public static final int _integerLiteral = 3;
	public static final int _doubleLiteral = 4;
	public static final int maxT = 65;

	static final boolean _T = true;
	static final boolean _x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;
	private final NodeFactory factory;
    public PascalRootNode mainNode;

	

	public Parser() {
		this.factory = new NodeFactory(this);
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void Pascal() {
		if (la.kind == 5) {
			Program();
			if (la.kind == 7) {
				ImportsSection();
			}
			Declarations();
			MainFunction();
		} else if (la.kind == 62) {
			Unit();
		} else SynErr(66);
	}

	void Program() {
		Expect(5);
		Expect(1);
		factory.startPascal(t); 
		Expect(6);
	}

	void ImportsSection() {
		Expect(7);
		Expect(1);
		factory.importUnit(t); 
		while (la.kind == 8) {
			Get();
			Expect(1);
			factory.importUnit(t); 
		}
		Expect(6);
	}

	void Declarations() {
		if (la.kind == 22) {
			ConstantDefinitions();
		}
		if (la.kind == 9) {
			TypeDefinitions();
		}
		if (la.kind == 23) {
			VariableDeclarations();
		}
		while (la.kind == 25 || la.kind == 27) {
			Subroutine();
		}
	}

	void MainFunction() {
		StatementNode blockNode = Block();
		mainNode = factory.finishMainFunction(blockNode); 
		Expect(28);
	}

	void Unit() {
		Expect(62);
		Expect(1);
		factory.startUnit(t); 
		Expect(6);
		Expect(63);
		InterfaceSection();
		Expect(64);
		factory.leaveUnitInterfaceSection(); 
		ImplementationSection();
		Expect(30);
		factory.endUnit(); 
		Expect(28);
	}

	void ConstantDefinitions() {
		Expect(22);
		ConstantDefinition();
		Expect(6);
		while (la.kind == 1) {
			ConstantDefinition();
			Expect(6);
		}
	}

	void TypeDefinitions() {
		Expect(9);
		TypeDefinition();
		Expect(6);
		while (la.kind == 1) {
			TypeDefinition();
			Expect(6);
		}
	}

	void VariableDeclarations() {
		Expect(23);
		VariableLineDeclaration();
		Expect(6);
		while (la.kind == 1) {
			VariableLineDeclaration();
			Expect(6);
		}
	}

	void Subroutine() {
		if (la.kind == 25) {
			Procedure();
			Expect(6);
		} else if (la.kind == 27) {
			Function();
			Expect(6);
		} else SynErr(67);
	}

	void TypeDefinition() {
		Expect(1);
		Token identifier = t; 
		Expect(10);
		TypeDescriptor typeDescriptor = Type();
		factory.registerNewType(identifier, typeDescriptor); 
	}

	TypeDescriptor  Type() {
		TypeDescriptor  typeDescriptor;
		typeDescriptor = null; 
		if (la.kind == 1) {
			Get();
			typeDescriptor = factory.getTypeDescriptor(t); 
		} else if (la.kind == 17) {
			typeDescriptor = EnumDefinition();
		} else if (la.kind == 13 || la.kind == 14) {
			List<OrdinalDescriptor> ordinalDimensions  = ArrayDefinition();
			while (continuesArray()) {
				Expect(11);
				List<OrdinalDescriptor> additionalDimensions  = ArrayDefinition();
				ordinalDimensions.addAll(additionalDimensions); 
			}
			Expect(11);
			Expect(1);
			typeDescriptor = factory.createArray(ordinalDimensions, t); 
		} else if (la.kind == 12) {
			Get();
			Expect(11);
			OrdinalDescriptor ordinal = Ordinal();
			typeDescriptor = factory.createSetType(ordinal); 
		} else SynErr(68);
		return typeDescriptor;
	}

	TypeDescriptor  EnumDefinition() {
		TypeDescriptor  typeDescriptor;
		List<String> enumIdentifiers = new ArrayList<>(); 
		Expect(17);
		Expect(1);
		enumIdentifiers.add(t.val.toLowerCase()); 
		while (la.kind == 8) {
			Get();
			Expect(1);
			enumIdentifiers.add(t.val.toLowerCase()); 
		}
		Expect(18);
		typeDescriptor = factory.registerEnum(enumIdentifiers); 
		return typeDescriptor;
	}

	List<OrdinalDescriptor>  ArrayDefinition() {
		List<OrdinalDescriptor>  ordinalDimensions;
		if (la.kind == 13) {
			Get();
		}
		Expect(14);
		ordinalDimensions = new ArrayList<>(); 
		Expect(15);
		OrdinalDescriptor ordinalDescriptor = null; 
		ordinalDescriptor = Ordinal();
		ordinalDimensions.add(ordinalDescriptor); 
		while (la.kind == 8) {
			Get();
			ordinalDescriptor = Ordinal();
			ordinalDimensions.add(ordinalDescriptor); 
		}
		Expect(16);
		return ordinalDimensions;
	}

	OrdinalDescriptor  Ordinal() {
		OrdinalDescriptor  ordinal;
		ordinal = null; 
		if (la.kind == 3 || la.kind == 20 || la.kind == 21) {
			int lowerBound, upperBound; 
			lowerBound = SignedIntegerLiteral();
			Expect(19);
			upperBound = SignedIntegerLiteral();
			ordinal = factory.createSimpleOrdinalDescriptor(lowerBound, upperBound); 
		} else if (StartOf(1)) {
			TypeDescriptor typeDescriptor = Type();
			ordinal = factory.castTypeToOrdinalType(typeDescriptor); 
		} else SynErr(69);
		return ordinal;
	}

	int  SignedIntegerLiteral() {
		int  value;
		value = 0; 
		if (la.kind == 20) {
			Get();
			value = SignedIntegerLiteral();
		} else if (la.kind == 21) {
			Get();
			value = SignedIntegerLiteral();
			value = -value; 
		} else if (la.kind == 3) {
			Get();
			value = Integer.parseInt(t.val); 
		} else SynErr(70);
		return value;
	}

	void ConstantDefinition() {
		Expect(1);
		Token identifier = t; 
		Expect(10);
		if (StartOf(2)) {
			NumericConstant(identifier);
		} else if (la.kind == 2) {
			String value = StringLiteral();
			factory.registerStringOrCharConstant(identifier, value); 
		} else if (la.kind == 59 || la.kind == 60) {
			boolean value = LogicLiteral();
			factory.registerBooleanConstant(identifier, value); 
		} else if (la.kind == 1 || la.kind == 20 || la.kind == 21) {
			IdentifierConstant(identifier);
		} else SynErr(71);
	}

	void NumericConstant(Token identifier) {
		if (la.kind == 20 || la.kind == 21) {
			if (la.kind == 20) {
				Get();
			} else {
				Get();
			}
			Token sign = t; 
			if (la.kind == 3) {
				Get();
				factory.registerSignedIntegerConstant(identifier, sign, t); 
			} else if (la.kind == 4) {
				Get();
				factory.registerSignedRealConstant(identifier, sign, t); 
			} else SynErr(72);
		} else if (la.kind == 3 || la.kind == 4) {
			if (la.kind == 3) {
				Get();
				factory.registerIntegerConstant(identifier, t); 
			} else {
				Get();
				factory.registerRealConstant(identifier, t); 
			}
		} else SynErr(73);
	}

	String  StringLiteral() {
		String  value;
		Expect(2);
		value = factory.createStringFromToken(t); 
		return value;
	}

	boolean  LogicLiteral() {
		boolean  result;
		result = false; 
		if (la.kind == 59) {
			Get();
			result = true; 
		} else if (la.kind == 60) {
			Get();
			result = false; 
		} else SynErr(74);
		return result;
	}

	void IdentifierConstant(Token identifier) {
		if (la.kind == 20 || la.kind == 21) {
			if (la.kind == 20) {
				Get();
			} else {
				Get();
			}
			Token sign = t; 
			Expect(1);
			factory.registerSignedConstantFromIdentifier(identifier, sign, t); 
		} else if (la.kind == 1) {
			Get();
			factory.registerConstantFromIdentifier(identifier, t); 
		} else SynErr(75);
	}

	void VariableLineDeclaration() {
		List<String> identifiers = new ArrayList<>(); 
		Expect(1);
		identifiers.add(t.val.toLowerCase()); 
		while (la.kind == 8) {
			Get();
			Expect(1);
			identifiers.add(t.val.toLowerCase()); 
		}
		Expect(24);
		TypeDescriptor typeDescriptor = Type();
		factory.registerVariables(identifiers, typeDescriptor); 
	}

	void Procedure() {
		Expect(25);
		Expect(1);
		Token identifierToken = t; 
		List<FormalParameter> formalParameters = new ArrayList<>(); 
		if (la.kind == 17) {
			formalParameters = FormalParameterList();
		}
		Expect(6);
		if (la.kind == 26) {
			Get();
			factory.forwardProcedure(identifierToken, formalParameters); 
		} else if (StartOf(3)) {
			factory.startProcedure(identifierToken, formalParameters); 
			Declarations();
			StatementNode bodyNode = Block();
			factory.finishProcedure(bodyNode); 
		} else SynErr(76);
	}

	void Function() {
		Expect(27);
		Expect(1);
		Token identifierToken = t; 
		List<FormalParameter> formalParameters = new ArrayList<>(); 
		if (la.kind == 17) {
			formalParameters = FormalParameterList();
		}
		Expect(24);
		Expect(1);
		Token returnTypeToken = t; 
		Expect(6);
		if (la.kind == 26) {
			Get();
			factory.forwardFunction(identifierToken, formalParameters, returnTypeToken); 
		} else if (StartOf(3)) {
			factory.startFunction(identifierToken, formalParameters, returnTypeToken); 
			Declarations();
			StatementNode bodyNode = Block();
			factory.finishFunction(bodyNode); 
		} else SynErr(77);
	}

	List<FormalParameter>  FormalParameterList() {
		List<FormalParameter>  formalParameters;
		Expect(17);
		formalParameters = new ArrayList<>(); 
		List<FormalParameter> newParameters = new ArrayList<>(); 
		newParameters = FormalParameter();
		factory.appendFormalParameter(newParameters, formalParameters); 
		while (la.kind == 6) {
			Get();
			newParameters = FormalParameter();
			factory.appendFormalParameter(newParameters, formalParameters); 
		}
		Expect(18);
		return formalParameters;
	}

	StatementNode  Block() {
		StatementNode  blockNode;
		Expect(29);
		List<StatementNode> bodyNodes = new ArrayList<>(); 
		if (StartOf(4)) {
			StatementSequence(bodyNodes);
		}
		Expect(30);
		blockNode = factory.createBlockNode(bodyNodes); 
		return blockNode;
	}

	List<FormalParameter>  FormalParameter() {
		List<FormalParameter>  formalParameter;
		List<String> identifiers = new ArrayList<>(); 
		boolean isReference = false; 
		if (la.kind == 23) {
			Get();
			isReference = true; 
		}
		Expect(1);
		identifiers.add(t.val.toLowerCase()); 
		while (la.kind == 8) {
			Get();
			Expect(1);
			identifiers.add(t.val.toLowerCase()); 
		}
		Expect(24);
		Expect(1);
		formalParameter = factory.createFormalParametersList(identifiers, t.val.toLowerCase(), isReference); 
		return formalParameter;
	}

	void StatementSequence(List<StatementNode> body ) {
		StatementNode statement = Statement();
		body.add(statement); 
		while (la.kind == 6) {
			Get();
			statement = Statement();
			body.add(statement); 
		}
	}

	StatementNode  Statement() {
		StatementNode  statement;
		statement = null; 
		switch (la.kind) {
		case 6: case 30: case 35: case 42: {
			statement = factory.createNopStatement(); 
			break;
		}
		case 1: case 2: case 3: case 4: case 15: case 17: case 20: case 21: case 48: case 59: case 60: case 61: {
			statement = Expression();
			break;
		}
		case 44: {
			statement = IfStatement();
			break;
		}
		case 36: {
			statement = ForLoop();
			break;
		}
		case 43: {
			statement = WhileLoop();
			break;
		}
		case 41: {
			statement = RepeatLoop();
			break;
		}
		case 34: {
			statement = CaseStatement();
			break;
		}
		case 31: {
			Get();
			statement = factory.createBreak(); 
			break;
		}
		case 29: {
			statement = Block();
			break;
		}
		case 33: {
			statement = ReadStatement();
			break;
		}
		case 32: {
			Get();
			statement = factory.createRandomizeNode(); 
			break;
		}
		default: SynErr(78); break;
		}
		return statement;
	}

	ExpressionNode  Expression() {
		ExpressionNode  expression;
		expression = LogicTerm();
		while (la.kind == 46) {
			Get();
			Token op = t; 
			ExpressionNode right = LogicTerm();
			expression = factory.createBinaryExpression(op, expression, right); 
		}
		return expression;
	}

	StatementNode  IfStatement() {
		StatementNode  statement;
		Expect(44);
		ExpressionNode condition = Expression();
		Expect(45);
		StatementNode thenStatement = Statement();
		StatementNode elseStatement = null; 
		if (la.kind == 35) {
			Get();
			elseStatement = Statement();
		}
		statement = factory.createIfStatement(condition, thenStatement, elseStatement); 
		return statement;
	}

	StatementNode  ForLoop() {
		StatementNode  statement;
		factory.startLoop(); 
		Expect(36);
		boolean ascending = true; 
		Expect(1);
		Token variableToken = t; 
		Expect(37);
		ExpressionNode startValue = Expression();
		if (la.kind == 38) {
			Get();
			ascending = true; 
		} else if (la.kind == 39) {
			Get();
			ascending = false; 
		} else SynErr(79);
		ExpressionNode finalValue = Expression();
		Expect(40);
		StatementNode loopBody = Statement();
		statement = factory.createForLoop(ascending, variableToken, startValue, finalValue, loopBody); 
		factory.finishLoop(); 
		return statement;
	}

	StatementNode  WhileLoop() {
		StatementNode  statement;
		factory.startLoop(); 
		Expect(43);
		ExpressionNode condition = Expression();
		Expect(40);
		StatementNode loopBody = Statement();
		statement = factory.createWhileLoop(condition, loopBody); 
		factory.finishLoop(); 
		return statement;
	}

	StatementNode  RepeatLoop() {
		StatementNode  statement;
		factory.startLoop(); 
		Expect(41);
		List<StatementNode> bodyNodes = new ArrayList<>(); 
		StatementSequence(bodyNodes);
		Expect(42);
		ExpressionNode condition = Expression();
		statement = factory.createRepeatLoop(condition, factory.createBlockNode(bodyNodes)); 
		factory.finishLoop(); 
		return statement;
	}

	StatementNode  CaseStatement() {
		StatementNode  statement;
		Expect(34);
		ExpressionNode caseExpression = Expression();
		Expect(11);
		CaseStatementData caseData = CaseList();
		caseData.caseExpression = caseExpression; 
		Expect(30);
		statement = factory.createCaseStatement(caseData); 
		return statement;
	}

	StatementNode  ReadStatement() {
		StatementNode  statement;
		Expect(33);
		statement = null; 
		if (StartOf(5)) {
			statement = factory.createReadLine(); 
		} else if (la.kind == 17) {
			Get();
			List<String> identifiers = new ArrayList<>(); 
			if (la.kind == 18) {
				Get();
				statement = factory.createReadLine(); 
			} else if (la.kind == 1) {
				Get();
				identifiers.add(t.val.toLowerCase()); 
				while (la.kind == 8) {
					Get();
					Expect(1);
					identifiers.add(t.val.toLowerCase()); 
				}
				Expect(18);
				statement = factory.createReadLine(identifiers); 
			} else SynErr(80);
		} else SynErr(81);
		return statement;
	}

	CaseStatementData  CaseList() {
		CaseStatementData  data;
		data = new CaseStatementData(); 
		ExpressionNode caseConstant = Expression();
		data.indexNodes.add(caseConstant); 
		Expect(24);
		StatementNode caseStatement = Statement();
		data.statementNodes.add(caseStatement); 
		while (!caseEnds()) {
			Expect(6);
			caseConstant = Expression();
			data.indexNodes.add(caseConstant); 
			Expect(24);
			caseStatement = Statement();
			data.statementNodes.add(caseStatement); 
		}
		if (la.kind == 6) {
			Get();
		}
		if (la.kind == 35) {
			Get();
			data.elseNode = Statement();
		}
		if (la.kind == 6) {
			Get();
		}
		return data;
	}

	ExpressionNode  LogicTerm() {
		ExpressionNode  expression;
		expression = SignedLogicFactor();
		while (la.kind == 47) {
			Get();
			Token op = t; 
			ExpressionNode right = SignedLogicFactor();
			expression = factory.createBinaryExpression(op, expression, right); 
		}
		return expression;
	}

	ExpressionNode  SignedLogicFactor() {
		ExpressionNode  expression;
		expression = null; 
		if (la.kind == 48) {
			Get();
			Token op = t; 
			ExpressionNode right = SignedLogicFactor();
			expression = factory.createUnaryExpression(op, right); 
		} else if (StartOf(6)) {
			expression = LogicFactor();
		} else SynErr(82);
		return expression;
	}

	ExpressionNode  LogicFactor() {
		ExpressionNode  expression;
		expression = Arithmetic();
		if (StartOf(7)) {
			switch (la.kind) {
			case 49: {
				Get();
				break;
			}
			case 50: {
				Get();
				break;
			}
			case 51: {
				Get();
				break;
			}
			case 52: {
				Get();
				break;
			}
			case 10: {
				Get();
				break;
			}
			case 53: {
				Get();
				break;
			}
			case 54: {
				Get();
				break;
			}
			}
			Token op = t; 
			ExpressionNode right = Arithmetic();
			expression = factory.createBinaryExpression(op, expression, right); 
		}
		return expression;
	}

	ExpressionNode  Arithmetic() {
		ExpressionNode  expression;
		expression = Term();
		while (la.kind == 20 || la.kind == 21) {
			if (la.kind == 20) {
				Get();
			} else {
				Get();
			}
			Token op = t; 
			ExpressionNode right = Term();
			expression = factory.createBinaryExpression(op, expression, right); 
		}
		return expression;
	}

	ExpressionNode  Term() {
		ExpressionNode  expression;
		expression = SignedFactor();
		while (StartOf(8)) {
			if (la.kind == 55) {
				Get();
			} else if (la.kind == 56) {
				Get();
			} else if (la.kind == 57) {
				Get();
			} else {
				Get();
			}
			Token op = t; 
			ExpressionNode right = SignedFactor();
			expression = factory.createBinaryExpression(op, expression, right); 
		}
		return expression;
	}

	ExpressionNode  SignedFactor() {
		ExpressionNode  expression;
		expression = null; 
		if (la.kind == 20 || la.kind == 21) {
			if (la.kind == 20) {
				Get();
			} else {
				Get();
			}
			Token unOp = t; 
			expression = SignedFactor();
			expression = factory.createUnaryExpression(unOp, expression); 
		} else if (StartOf(9)) {
			expression = Factor();
		} else SynErr(83);
		return expression;
	}

	ExpressionNode  Factor() {
		ExpressionNode  expression;
		expression = null; 
		switch (la.kind) {
		case 61: {
			expression = Random();
			break;
		}
		case 1: {
			Get();
			if (la.kind == 15 || la.kind == 17 || la.kind == 37) {
				expression = MemberExpression(t);
			} else if (StartOf(10)) {
				expression = factory.createExpressionFromSingleIdentifier(t); 
			} else SynErr(84);
			break;
		}
		case 17: {
			Get();
			expression = Expression();
			Expect(18);
			break;
		}
		case 2: {
			String value = ""; 
			value = StringLiteral();
			expression = factory.createCharOrStringLiteral(value); 
			break;
		}
		case 4: {
			Get();
			expression = factory.createFloatLiteral(t); 
			break;
		}
		case 3: {
			Get();
			expression = factory.createNumericLiteral(t); 
			break;
		}
		case 59: case 60: {
			boolean val; 
			val = LogicLiteral();
			expression = factory.createLogicLiteral(val); 
			break;
		}
		case 15: {
			expression = SetConstructor();
			break;
		}
		default: SynErr(85); break;
		}
		return expression;
	}

	ExpressionNode  Random() {
		ExpressionNode  expression;
		Expect(61);
		expression = null; 
		if (StartOf(10)) {
			expression = factory.createRandomNode(); 
		} else if (la.kind == 17) {
			Get();
			if (la.kind == 18) {
				Get();
				expression = factory.createRandomNode(); 
			} else if (la.kind == 3) {
				Get();
				expression = factory.createRandomNode(t); 
				Expect(18);
			} else SynErr(86);
		} else SynErr(87);
		return expression;
	}

	ExpressionNode  MemberExpression(Token identifierName) {
		ExpressionNode  expression;
		expression = null; 
		if (la.kind == 17) {
			expression = SubroutineCall(identifierName);
		} else if (la.kind == 37) {
			Get();
			ExpressionNode value = Expression();
			expression = factory.createAssignment(identifierName, value); 
		} else if (la.kind == 15) {
			expression = ArrayAccessing(identifierName);
		} else SynErr(88);
		return expression;
	}

	ExpressionNode  SetConstructor() {
		ExpressionNode  expression;
		expression = null; 
		Expect(15);
		if (la.kind == 16) {
			expression = factory.createSetConstructorNode(new ArrayList<>()); 
		} else if (StartOf(11)) {
			List<ExpressionNode> valueNodes = new ArrayList<ExpressionNode>(); 
			ExpressionNode valueNode = Expression();
			valueNodes.add(valueNode); 
			while (la.kind == 8) {
				Get();
				valueNode = Expression();
				valueNodes.add(valueNode); 
			}
			expression = factory.createSetConstructorNode(valueNodes); 
		} else SynErr(89);
		Expect(16);
		return expression;
	}

	ExpressionNode  SubroutineCall(Token identifierToken) {
		ExpressionNode  expression;
		Expect(17);
		ExpressionNode functionNode = factory.createFunctionLiteralNode(identifierToken); 
		List<ExpressionNode> parameters = new ArrayList<>(); 
		if (StartOf(11)) {
			parameters = ActualParameters(identifierToken);
		}
		Expect(18);
		expression = factory.createCall(functionNode, parameters); 
		return expression;
	}

	ExpressionNode  ArrayAccessing(Token identifierName) {
		ExpressionNode  expression;
		expression = null; 
		List<ExpressionNode> indexingNodes = new ArrayList<>(); 
		ExpressionNode indexingNode = null; 
		Expect(15);
		indexingNode = Expression();
		indexingNodes.add(indexingNode); 
		while (la.kind == 8) {
			Get();
			indexingNode = Expression();
			indexingNodes.add(indexingNode); 
		}
		Expect(16);
		if (StartOf(10)) {
			expression = factory.createReadArrayValue(identifierName, indexingNodes); 
		} else if (la.kind == 37) {
			Get();
			ExpressionNode value = Expression();
			expression = factory.createArrayIndexAssignment( 
			identifierName, indexingNodes, value); 
		} else SynErr(90);
		return expression;
	}

	List<ExpressionNode>  ActualParameters(Token subroutineToken ) {
		List<ExpressionNode>  parameters;
		parameters = new ArrayList<>(); 
		int currentParameter = 0; 
		ExpressionNode parameter = ActualParameter(currentParameter, subroutineToken);
		parameters.add(parameter); 
		while (la.kind == 8) {
			Get();
			parameter = ActualParameter(++currentParameter, subroutineToken);
			parameters.add(parameter); 
		}
		return parameters;
	}

	ExpressionNode  ActualParameter(int currentParameterIndex, Token subroutineToken) {
		ExpressionNode  parameter;
		parameter = null; 
		if (factory.shouldBeReference(subroutineToken, currentParameterIndex)) {
			Expect(1);
			parameter = factory.createReferenceNode(t); 
		} else if (StartOf(11)) {
			parameter = Expression();
		} else SynErr(91);
		return parameter;
	}

	void InterfaceSection() {
		if (la.kind == 9) {
			TypeDefinitions();
		}
		if (la.kind == 23) {
			VariableDeclarations();
		}
		while (la.kind == 25 || la.kind == 27) {
			if (la.kind == 25) {
				ProcedureHeading();
			} else {
				FunctionHeading();
			}
		}
	}

	void ImplementationSection() {
		while (StartOf(12)) {
			if (la.kind == 25 || la.kind == 27) {
				Subroutine();
			} else if (la.kind == 23) {
				VariableDeclarations();
			} else {
				TypeDefinition();
			}
		}
	}

	void ProcedureHeading() {
		Expect(25);
		Expect(1);
		Token name = t; 
		List<FormalParameter> formalParameters = new ArrayList<>(); 
		if (la.kind == 17) {
			formalParameters = FormalParameterList();
		}
		factory.addProcedureInterface(name, formalParameters); 
		Expect(6);
	}

	void FunctionHeading() {
		Expect(27);
		Expect(1);
		Token name = t; 
		List<FormalParameter> formalParameters = new ArrayList<>(); 
		if (la.kind == 17) {
			formalParameters = FormalParameterList();
		}
		Expect(24);
		Expect(1);
		String returnValue = t.val; 
		factory.addFunctionInterface(name, formalParameters, returnValue); 
		Expect(6);
	}



	public void Parse(Source source) {
		this.scanner = new Scanner(source.getInputStream());
		la = new Token();
		la.val = "";		
		Get();
		Pascal();
		Expect(0);

	}

	private static final boolean[][] set = {
		{_T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x},
		{_x,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _T,_T,_T,_x, _x,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x},
		{_x,_x,_x,_T, _T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _T,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x},
		{_x,_x,_x,_x, _x,_x,_x,_x, _x,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_T,_T, _x,_T,_x,_T, _x,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x},
		{_x,_T,_T,_T, _T,_x,_T,_x, _x,_x,_x,_x, _x,_x,_x,_T, _x,_T,_x,_x, _T,_T,_x,_x, _x,_x,_x,_x, _x,_T,_T,_T, _T,_T,_T,_x, _T,_x,_x,_x, _x,_T,_x,_T, _T,_x,_x,_x, _T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_T, _T,_T,_x,_x, _x,_x,_x},
		{_x,_x,_x,_x, _x,_x,_T,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_T,_x, _x,_x,_x,_T, _x,_x,_x,_x, _x,_x,_T,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x},
		{_x,_T,_T,_T, _T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_T, _x,_T,_x,_x, _T,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_T, _T,_T,_x,_x, _x,_x,_x},
		{_x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_T,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_T,_T,_T, _T,_T,_T,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x},
		{_x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_T, _T,_T,_T,_x, _x,_x,_x,_x, _x,_x,_x},
		{_x,_T,_T,_T, _T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_T, _x,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_T, _T,_T,_x,_x, _x,_x,_x},
		{_x,_x,_x,_x, _x,_x,_T,_x, _T,_x,_T,_T, _x,_x,_x,_x, _T,_x,_T,_x, _T,_T,_x,_x, _T,_x,_x,_x, _x,_x,_T,_x, _x,_x,_x,_T, _x,_x,_T,_T, _T,_x,_T,_x, _x,_T,_T,_T, _x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_x, _x,_x,_x,_x, _x,_x,_x},
		{_x,_T,_T,_T, _T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_T, _x,_T,_x,_x, _T,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_T, _T,_T,_x,_x, _x,_x,_x},
		{_x,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_T, _x,_T,_x,_T, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x}

	};
	
    public boolean hadErrors(){
    	return errors.count > 0;
    }

    public RootNode getRootNode() {
        return this.mainNode;
    }
    
    public boolean caseEnds(){
    	if(la.val.toLowerCase().equals("end") && !t.val.toLowerCase().equals(":"))
    		return true;
    		
    	if(la.val.toLowerCase().equals("else"))
    		return true;
    		
    	else if(la.val.toLowerCase().equals(";")){
    		Token next = scanner.Peek();
    		return next.val.toLowerCase().equals("end") || next.val.toLowerCase().equals("else");
    	}
    	
    	return false;
    }
    
    public boolean isSingleIdentifier(){
    	Token next = scanner.Peek();
    	if(next.val.equals("]") && factory.containsIdentifier(la.val.toLowerCase()))
    		return true;

    	return false;
    }
    
    public boolean continuesArray(){
    	Token next = scanner.Peek();
    	if(next.val.toLowerCase().equals("array") || (next.val.toLowerCase().equals("packed")))
    		return true;

    	return false;
    }
} // end Parser

class Errors {
	public int count = 0;                                    // number of errors detected
	public java.io.PrintStream errorStream = System.err;     // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
	
	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "identifier expected"; break;
			case 2: s = "stringLiteral expected"; break;
			case 3: s = "integerLiteral expected"; break;
			case 4: s = "doubleLiteral expected"; break;
			case 5: s = "\"program\" expected"; break;
			case 6: s = "\";\" expected"; break;
			case 7: s = "\"uses\" expected"; break;
			case 8: s = "\",\" expected"; break;
			case 9: s = "\"type\" expected"; break;
			case 10: s = "\"=\" expected"; break;
			case 11: s = "\"of\" expected"; break;
			case 12: s = "\"set\" expected"; break;
			case 13: s = "\"packed\" expected"; break;
			case 14: s = "\"array\" expected"; break;
			case 15: s = "\"[\" expected"; break;
			case 16: s = "\"]\" expected"; break;
			case 17: s = "\"(\" expected"; break;
			case 18: s = "\")\" expected"; break;
			case 19: s = "\"..\" expected"; break;
			case 20: s = "\"+\" expected"; break;
			case 21: s = "\"-\" expected"; break;
			case 22: s = "\"const\" expected"; break;
			case 23: s = "\"var\" expected"; break;
			case 24: s = "\":\" expected"; break;
			case 25: s = "\"procedure\" expected"; break;
			case 26: s = "\"forward\" expected"; break;
			case 27: s = "\"function\" expected"; break;
			case 28: s = "\".\" expected"; break;
			case 29: s = "\"begin\" expected"; break;
			case 30: s = "\"end\" expected"; break;
			case 31: s = "\"break\" expected"; break;
			case 32: s = "\"randomize\" expected"; break;
			case 33: s = "\"readln\" expected"; break;
			case 34: s = "\"case\" expected"; break;
			case 35: s = "\"else\" expected"; break;
			case 36: s = "\"for\" expected"; break;
			case 37: s = "\":=\" expected"; break;
			case 38: s = "\"to\" expected"; break;
			case 39: s = "\"downto\" expected"; break;
			case 40: s = "\"do\" expected"; break;
			case 41: s = "\"repeat\" expected"; break;
			case 42: s = "\"until\" expected"; break;
			case 43: s = "\"while\" expected"; break;
			case 44: s = "\"if\" expected"; break;
			case 45: s = "\"then\" expected"; break;
			case 46: s = "\"or\" expected"; break;
			case 47: s = "\"and\" expected"; break;
			case 48: s = "\"not\" expected"; break;
			case 49: s = "\">\" expected"; break;
			case 50: s = "\">=\" expected"; break;
			case 51: s = "\"<\" expected"; break;
			case 52: s = "\"<=\" expected"; break;
			case 53: s = "\"<>\" expected"; break;
			case 54: s = "\"in\" expected"; break;
			case 55: s = "\"*\" expected"; break;
			case 56: s = "\"/\" expected"; break;
			case 57: s = "\"div\" expected"; break;
			case 58: s = "\"mod\" expected"; break;
			case 59: s = "\"true\" expected"; break;
			case 60: s = "\"false\" expected"; break;
			case 61: s = "\"random\" expected"; break;
			case 62: s = "\"unit\" expected"; break;
			case 63: s = "\"interface\" expected"; break;
			case 64: s = "\"implementation\" expected"; break;
			case 65: s = "??? expected"; break;
			case 66: s = "invalid Pascal"; break;
			case 67: s = "invalid Subroutine"; break;
			case 68: s = "invalid Type"; break;
			case 69: s = "invalid Ordinal"; break;
			case 70: s = "invalid SignedIntegerLiteral"; break;
			case 71: s = "invalid ConstantDefinition"; break;
			case 72: s = "invalid NumericConstant"; break;
			case 73: s = "invalid NumericConstant"; break;
			case 74: s = "invalid LogicLiteral"; break;
			case 75: s = "invalid IdentifierConstant"; break;
			case 76: s = "invalid Procedure"; break;
			case 77: s = "invalid Function"; break;
			case 78: s = "invalid Statement"; break;
			case 79: s = "invalid ForLoop"; break;
			case 80: s = "invalid ReadStatement"; break;
			case 81: s = "invalid ReadStatement"; break;
			case 82: s = "invalid SignedLogicFactor"; break;
			case 83: s = "invalid SignedFactor"; break;
			case 84: s = "invalid Factor"; break;
			case 85: s = "invalid Factor"; break;
			case 86: s = "invalid Random"; break;
			case 87: s = "invalid Random"; break;
			case 88: s = "invalid MemberExpression"; break;
			case 89: s = "invalid SetConstructor"; break;
			case 90: s = "invalid ArrayAccessing"; break;
			case 91: s = "invalid ActualParameter"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		printMsg(line, col, s);
		count++;
	}
	
	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}
	
	public void Warning (int line, int col, String s) {	
		printMsg(line, col, s);
	}
	
	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}