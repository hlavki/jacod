package camp.xit.jacod.entry.parser.ast;

import camp.xit.jacod.entry.parser.ParserConstants;

public abstract class UnaryCondition extends Condition {

    public UnaryCondition(Class<?> clazz, Property property, int operator) {
        super(clazz, property, null, operator);
    }

    public UnaryCondition(Class<?> clazz, Property property) {
        super(clazz, property);
    }

    @Override
    public String toString() {
        return left.toString() + " " + (operator > -1 ? ParserConstants.tokenImage[operator].replace("\"", "") : "");
    }
}
