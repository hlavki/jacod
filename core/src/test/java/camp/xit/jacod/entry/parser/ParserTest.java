package camp.xit.jacod.entry.parser;

import camp.xit.jacod.entry.parser.ast.AllExpression;
import camp.xit.jacod.entry.parser.ast.CompileException;
import camp.xit.jacod.entry.parser.ast.Expression;
import camp.xit.jacod.model.BusinessPlace;
import java.io.StringReader;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void parse() throws Exception {
        StringReader reader = new StringReader("company.name = \"NAY\" & ( code = \"XCODE\" | name = \"HEY ahoj\" )");
        Parser parser = new Parser(reader);
        Expression expr = parser.parse(BusinessPlace.class);
        assertNotNull(expr);
    }

    @Test
    public void number() throws Exception {
        StringReader reader = new StringReader("order <= 10.23");
        Parser parser = new Parser(reader);
        Expression expr = parser.parse(BusinessPlace.class);
        assertNotNull(expr);
    }

    @Test
    public void digit() throws Exception {
        StringReader reader = new StringReader("order <= 2");
        Parser parser = new Parser(reader);
        Expression expr = parser.parse(BusinessPlace.class);
        assertNotNull(expr);
    }

    @Test
    public void nullExpression() throws Exception {
        StringReader reader = new StringReader("order is empty");
        Parser parser = new Parser(reader);
        Expression expr = parser.parse(BusinessPlace.class);
        assertNotNull(expr);
        assertThat(expr.toString(), is("order is empty"));
    }

    @Test
    public void expressionToString() throws ParseException {
        Parser parser = new Parser(new StringReader("(order is   not  empty) & order > 20"));
        Expression expr = parser.parse(BusinessPlace.class);
        assertNotNull(expr);
        assertThat(expr.toString(), is("(order is not empty) & order > 20"));
    }

    @Test
    public void compileError() throws Exception {
        try {
            StringReader reader = new StringReader("compana = \"NAY\" & (code = \"XCODE\" | code = \"HEY\")");
            Parser parser = new Parser(reader);
            parser.parse(BusinessPlace.class);
            fail("Should not be here!");
        } catch (CompileException e) {
        }
    }

    @Test
    public void syntaxError() throws Exception {
        try {
            String query = "company = \"NAY\" & (code = \"XCODE\" AND code = \"HEY\")";
            Parser parser = new Parser(new StringReader(query));
            parser.parse(BusinessPlace.class);
            fail("Should not be here!");
        } catch (ParseException e) {
        }
    }

    @Test
    public void syntaxError2() throws Exception {
        try {
            String query = "company = \"NAY\" AND (code = \"XCODE\" AND code = \"HEY\")";
            Parser parser = new Parser(new StringReader(query));
            parser.parse(BusinessPlace.class);
            fail("Should not be here!");
        } catch (ParseException e) {
        }
    }

    @Test
    public void empty() throws Exception {
        StringReader reader = new StringReader("");
        Parser parser = new Parser(reader);
        Expression expr = parser.parse(BusinessPlace.class);
        assertTrue(expr instanceof AllExpression);
    }
}
