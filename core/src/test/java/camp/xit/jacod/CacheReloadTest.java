package camp.xit.jacod;

import camp.xit.jacod.CodelistClient;
import camp.xit.jacod.model.Codelist;
import camp.xit.jacod.model.Title;
import camp.xit.jacod.provider.csv.SimpleCsvDataProvider;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheReloadTest {

    private static final Logger LOG = LoggerFactory.getLogger(CacheReloadTest.class);
    private static final int CACHE_EXPIRY_TIME = 1;
    private static final int WAIT_TIME = 3;


    @Test
    void reloadCache() throws Exception {
        CodelistClient client = new CodelistClient.Builder()
                .withDataProvider(new SimpleCsvDataProvider())
                .withExpiryTime(CACHE_EXPIRY_TIME, TimeUnit.SECONDS)
                .withPrefetched("Title").build();

        Codelist<Title> titles = client.getCodelist(Title.class);
        LOG.info("Waiting for {} seconds", WAIT_TIME);
        Thread.sleep(WAIT_TIME * 1000);
        titles = client.getCodelist(Title.class);
        assertThat(titles.size(), greaterThan(0));
    }


    @Test
    void equalEntries() throws Exception {
        CodelistClient client = new CodelistClient.Builder()
                .withDataProvider(new SimpleCsvDataProvider())
                .withPrefetched("Title").build();
        Title dr1 = client.getCodelist(Title.class).getEntry("ThDr.");
        client.clearCache();
        LOG.info("Waiting for {} seconds", WAIT_TIME);
        Thread.sleep(WAIT_TIME * 1000);
        Title dr2 = client.getCodelist(Title.class).getEntry("ThDr.");
        assertEquals(dr1, dr2);
    }
}