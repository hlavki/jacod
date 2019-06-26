package camp.xit.jacoa.entry;

import camp.xit.jacoa.model.Codelist;
import camp.xit.jacoa.model.CodelistEntry;
import java.util.ArrayList;
import java.util.List;

public class JoinEntryGroup<T extends CodelistEntry> implements EntryGroup<T> {

    private final List<EntryGroup<T>> entryGroups;


    public JoinEntryGroup(List<EntryGroup<T>> entryGroups) {
        this.entryGroups = entryGroups;
    }


    public JoinEntryGroup(List<? extends EntryGroup<T>> groups, EnumeratedEntryGroup<T> enumerated) {
        this.entryGroups = new ArrayList<>(groups);
        this.entryGroups.add(enumerated);
    }


    @Override
    public Codelist<T> getEntries(Codelist<T> entries, boolean validOnly) {
        return entryGroups.parallelStream()
                .flatMap(e -> e.getEntries(entries).stream(validOnly))
                .collect(Codelist.collect(entries.getName()));
    }


    @Override
    public String toString() {
        return "JoinEntryGroup{" + String.valueOf(entryGroups) + '}';
    }
}
