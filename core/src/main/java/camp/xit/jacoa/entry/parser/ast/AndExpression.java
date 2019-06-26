package camp.xit.jacoa.entry.parser.ast;

import camp.xit.jacoa.entry.parser.ParserConstants;

public class AndExpression extends LogicalExpression {

    public AndExpression(Class<?> clazz, Expression left, Expression right) {
        super(clazz, left, right, ParserConstants.AND);
    }


    @Override
    public String toString() {
        return super.getLeft() + " AND " + super.getRight();
    }


    @Override
    public boolean filter(Object obj) {
        return getLeft().filter(obj) & getRight().filter(obj);
    }
}
