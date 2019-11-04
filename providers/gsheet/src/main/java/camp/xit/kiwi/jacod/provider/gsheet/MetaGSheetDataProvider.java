package camp.xit.kiwi.jacod.provider.gsheet;

import camp.xit.jacod.provider.EntryData;
import camp.xit.kiwi.jacod.provider.gsheet.service.RangeValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MetaGSheetDataProvider extends GSheetDataProvider {

    private static final String DEFAULT_META_SHEET = "Codelists";
    private static final String DEFAULT_NAME_COLUMN = "NAME";

    private final String metaSheet;
    private List<EntryData> metadata;
    private String nameColumn;


    public MetaGSheetDataProvider(String serviceAccountFile, String spreadSheetId) {
        this(serviceAccountFile, spreadSheetId, DEFAULT_META_SHEET, DEFAULT_NAME_COLUMN);
    }


    public MetaGSheetDataProvider(File serviceAccountFile, String spreadSheetId) {
        this(serviceAccountFile, spreadSheetId, DEFAULT_META_SHEET, DEFAULT_NAME_COLUMN);
    }


    public MetaGSheetDataProvider(String serviceAccountFile, String spreadSheetId, String metaSheet, String nameColumn) {
        this(new File(serviceAccountFile), spreadSheetId, metaSheet, nameColumn);
    }


    public MetaGSheetDataProvider(File serviceAccountFile, String spreadSheetId, String metaSheet, String nameColumn) {
        super(serviceAccountFile, spreadSheetId);
        this.metaSheet = metaSheet;
        this.metadata = readMetadata();
        this.nameColumn = nameColumn;
    }


    public MetaGSheetDataProvider(String name, String serviceAccountFile, String spreadSheetId, String metaSheet, String nameColumn) {
        this(name, new File(serviceAccountFile), spreadSheetId, metaSheet, nameColumn);
    }


    public MetaGSheetDataProvider(String name, File serviceAccountFile, String spreadSheetId, String metaSheet, String nameColumn) {
        super(name, serviceAccountFile, spreadSheetId);
        this.metaSheet = metaSheet;
        this.metadata = readMetadata();
        this.nameColumn = nameColumn;
    }


    public synchronized void reloadMetadata() {
        this.metadata = readMetadata();
    }


    protected List<EntryData> readMetadata() {
        RangeValue sheetValues = gsheetService.readSheetValues(spreadSheetId, metaSheet);
        JsonNode valuesNode = sheetValues.getValues();
        ArrayNode fieldNamesNode = null;
        List<EntryData> result = new ArrayList<>();
        if (valuesNode.isArray()) {
            int rowNum = 0;
            for (JsonNode rowNode : valuesNode) {
                if (rowNode.isArray() && rowNum == 0) {
                    fieldNamesNode = (ArrayNode) rowNode;
                } else if (rowNode.isArray()) {
                    ArrayNode arrRowNode = (ArrayNode) rowNode;
                    EntryData data = new EntryData();
                    for (int idx = 0; idx < fieldNamesNode.size(); idx++) {
                        String key = fieldNamesNode.get(idx).asText();
                        String value = ofNullable(arrRowNode.get(idx)).map(v -> v.asText()).orElse(null);
                        if (value != null && value.isEmpty()) value = null;
                        data.addField(key, value);
                    }
                    result.add(data);
                }
                rowNum++;
            }
        }
        return result;
    }


    @Override
    public final Set<String> readAllNames() {
        return getAllNames();
    }


    private Set<String> getAllNames() {
        return metadata.stream().map(e -> e.getSingleValue(nameColumn))
                .filter(v -> v.isPresent()).map(v -> v.get()).collect(toSet());
    }


    @Override
    public Optional<List<EntryData>> readEntries(String codelist, long lastReadTime) {
        Optional<List<EntryData>> result = empty();
        if (getAllNames().contains(codelist)) {
            result = super.readEntries(codelist, lastReadTime);
        }
        return result;
    }
}
