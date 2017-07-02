package cz.cuni.mff.d3s.trupple.language.interop;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.Message;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.RootNode;

public class MainFunctionObject implements TruffleObject {

    private final RootNode rootNode;

    public MainFunctionObject(RootNode rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public ForeignAccess getForeignAccess() {
        ForeignAccess.Factory26 f = new ForeignAccess.Factory26() {
            @Override
            public CallTarget accessIsNull() {
                return null;
            }

            @Override
            public CallTarget accessIsExecutable() {
                return null;
            }

            @Override
            public CallTarget accessIsBoxed() {
                return null;
            }

            @Override
            public CallTarget accessHasSize() {
                return null;
            }

            @Override
            public CallTarget accessGetSize() {
                return null;
            }

            @Override
            public CallTarget accessUnbox() {
                return null;
            }

            @Override
            public CallTarget accessRead() {
                return null;
            }

            @Override
            public CallTarget accessWrite() {
                return null;
            }

            @Override
            public CallTarget accessExecute(int i) {
                return Truffle.getRuntime().createCallTarget(rootNode);
            }

            @Override
            public CallTarget accessInvoke(int i) {
                return null;
            }

            @Override
            public CallTarget accessNew(int i) {
                return null;
            }

            @Override
            public CallTarget accessKeys() {
                return null;
            }

            @Override
            public CallTarget accessKeyInfo() {
                return null;
            }

            @Override
            public CallTarget accessMessage(Message message) {
                return null;
            }
        };

        ForeignAccess.Factory factory = new ForeignAccess.Factory() {

            @Override
            public boolean canHandle(TruffleObject truffleObject) {
                return false;
            }

            @Override
            public CallTarget accessMessage(Message message) {
                if (message.getClass().getName().equals("com.oracle.truffle.api.interop.Execute")) { // TODO: remove this reflection
                    return Truffle.getRuntime().createCallTarget(rootNode);
                }
                return null;
            }

        };
        return ForeignAccess.create(factory);
    }

}
