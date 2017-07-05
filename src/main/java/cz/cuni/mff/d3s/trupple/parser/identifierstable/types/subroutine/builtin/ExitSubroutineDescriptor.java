package cz.cuni.mff.d3s.trupple.parser.identifierstable.types.subroutine.builtin;

import cz.cuni.mff.d3s.trupple.language.nodes.builtin.tp.ExitBuiltinNodeGen;
import cz.cuni.mff.d3s.trupple.language.nodes.call.ReadArgumentNode;
import cz.cuni.mff.d3s.trupple.parser.FormalParameter;
import cz.cuni.mff.d3s.trupple.parser.identifierstable.types.primitive.IntDescriptor;

import java.util.ArrayList;

public class ExitSubroutineDescriptor extends BuiltinProcedureDescriptor {

    public ExitSubroutineDescriptor() {
        super(ExitBuiltinNodeGen.create(new ReadArgumentNode(0, IntDescriptor.getInstance())),
                new ArrayList<FormalParameter>(){{
                    add(new FormalParameter("code", IntDescriptor.getInstance(), false));
                }});
    }

}
