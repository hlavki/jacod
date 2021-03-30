package camp.xit.jacod.entry;

import camp.xit.jacod.CodelistClient;
import camp.xit.jacod.model.Codelist;
import camp.xit.jacod.model.InsuranceProduct;
import camp.xit.jacod.test.CodelistClientExtension;
import camp.xit.jacod.test.CodelistClientExtension.CsvClient;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(CodelistClientExtension.class)
public class QueryEntryGroupTest {

    @Test
    public void parseNumber(@CsvClient CodelistClient client) throws Exception {
        EntryGroup group = new QueryEntryGroup(InsuranceProduct.class, "rate < 0.5 & company = \"02\"");
        Codelist<InsuranceProduct> filtered = group.getEntries(client.getCodelist(InsuranceProduct.class));
        assertThat(filtered.size(), is(2));
        assertThat(filtered.keySet(), containsInAnyOrder("A_02", "B_02"));
    }

    @Test
    public void parseDigit(@CsvClient CodelistClient client) throws Exception {
        EntryGroup group = new QueryEntryGroup(InsuranceProduct.class, "(rate < 1 & selected = true)");
        Codelist<InsuranceProduct> filtered = group.getEntries(client.getCodelist(InsuranceProduct.class));
        assertThat(filtered.size(), is(2));
        assertThat(filtered.keySet(), containsInAnyOrder("A_01", "B_02"));
    }

    @Test
    public void nullValues(@CsvClient CodelistClient client) throws Exception {
        EntryGroup group = new QueryEntryGroup(InsuranceProduct.class, "order > 10");
        Codelist<InsuranceProduct> filtered = group.getEntries(client.getCodelist(InsuranceProduct.class));
        assertThat(filtered.size(), is(4));
    }

    @Test
    public void nullExpression(@CsvClient CodelistClient client) throws Exception {
        EntryGroup group = new QueryEntryGroup(InsuranceProduct.class, "order is empty");
        Codelist<InsuranceProduct> filtered = group.getEntries(client.getCodelist(InsuranceProduct.class));
        assertThat(filtered.size(), is(1));
    }

    @Test
    public void notNullExpression(@CsvClient CodelistClient client) throws Exception {
        EntryGroup group = new QueryEntryGroup(InsuranceProduct.class, "order is not empty");
        Codelist<InsuranceProduct> filtered = group.getEntries(client.getCodelist(InsuranceProduct.class));
        assertThat(filtered.size(), is(5));
    }

    @Test
    public void notNullExpressionAndExpression(@CsvClient CodelistClient client) throws Exception {
        QueryEntryGroup group = new QueryEntryGroup(InsuranceProduct.class, "(order is   not  empty) & order > 20");
        Codelist<InsuranceProduct> filtered = group.getEntries(client.getCodelist(InsuranceProduct.class));
        assertThat(filtered.size(), is(3));
    }
}
