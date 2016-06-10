package pascal.language;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;
import com.oracle.truffle.api.dsl.internal.DSLOptions;

import pascal.language.runtime.Null;
import pascal.language.runtime.PascalFunction;

@TypeSystem({int.class, long.class, boolean.class, char.class, PascalFunction.class, Null.class})
@DSLOptions
public class PascalTypes {
	
	@TypeCheck(Null.class)
    public static boolean isNull(Object value) {
        return value == Null.SINGLETON;
    }
	
	@TypeCast(Null.class)
    public static Null asSLNull(Object value) {
        assert isNull(value);
        return Null.SINGLETON;
    }
	
	@ImplicitCast
    @TruffleBoundary
    public static long castInt(int value) {
        return value;
    }
}