package camp.xit.jacod.entry.parser.ast;

import camp.xit.jacod.entry.parser.ParserConstants;

public class OrExpression extends LogicalExpression {

    public OrExpression(Class<?> clazz, Expression left, Expression right) {
        super(clazz, left, right, ParserConstants.OR);
    }


    @Override
    public boolean filter(Object obj) {
        return getLeft().filter(obj) | getRight().filter(obj);
    }


    @Override
    public String toString() {
        return super.getLeft() + " | " + super.getRight();
    }
}
