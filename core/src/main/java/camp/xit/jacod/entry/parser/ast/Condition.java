package camp.xit.jacod.entry.parser.ast;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class Condition extends Expression {

    protected final List<Method> getters;


    public Condition(Class<?> clazz, Property property, Constant rightExpression, int operator) {
        super(clazz, property, rightExpression, operator);
        this.getters = compileGetters();
    }


    public Condition(Class<?> entryClass, Expression expr) {
        super(entryClass, expr);
        this.getters = compileGetters();
    }


    public Property getProperty() {
        return (Property) getLeft();
    }


    protected final List<Method> compileGetters() {
        String[] properties = getProperty().getProperty().split("\\.");
        Class<?> clazz = getEntryClass();
        List<Method> getters = new ArrayList<>();
        for (String property : properties) {
            try {
                Method getter = new PropertyDescriptor(property, clazz).getReadMethod();
                getters.add(getter);
                clazz = getter.getReturnType();
            } catch (IntrospectionException e) {
                throw new CompileException("Can't compile condition. No getter for " + clazz.getSimpleName() + "." + property);
            }
        }
        return getters;
    }


    protected Object getValue(Object entry) {
        Object obj = entry;
        try {
            for (Method m : getters) {
                obj = m.invoke(obj);
                if (obj == null) break;
            }
            return obj;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("Cannot evaluate query", e);
        }
    }
}
