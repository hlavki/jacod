package camp.xit.jacod.entry.parser.ast;

import java.util.Collection;

public class IsEmptyCondition extends UnaryCondition {

    private final boolean negate;

    public IsEmptyCondition(Class<?> clazz, Property property, boolean negate) {
        super(clazz, property);
        this.negate = negate;
    }

    @Override
    public boolean filter(Object entry) {
        Object objValue = getValue(entry);
        Class<?> returnType = getters.get(getters.size() - 1).getReturnType();
        boolean collection = Collection.class.isAssignableFrom(returnType);
        boolean result = (collection ? (objValue == null || ((Collection) objValue).isEmpty()) : objValue == null);
        return negate ? !result : result;
    }
}
