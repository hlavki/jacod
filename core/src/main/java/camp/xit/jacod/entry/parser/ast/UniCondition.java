package camp.xit.jacod.entry.parser.ast;

import camp.xit.jacod.entry.parser.ParserConstants;

public class UniCondition extends Condition {

    public UniCondition(Class<?> clazz, Property property, int operator) {
        super(clazz, property, null, operator);
    }


    @Override
    public boolean filter(Object entry) {
        boolean result = false;
        Object objValue = getValue(entry);
        switch (getOperator()) {
        case ParserConstants.IS_EMPTY:
            return objValue == null;
        }
        return result;
    }


    @Override
    public String toString() {
        return left.toString() + " " + (operator > -1 ? ParserConstants.tokenImage[operator].replace("\"", "") : "");
    }
}
