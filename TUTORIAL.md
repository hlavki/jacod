# JACOD - Java Codelist API

## Installation

**Using maven**

```xml
<dependency>
    <groupId>camp.xit.jaoca</groupId>
    <artifactId>jacod-bom</artifactId>
    <version>${jacod.version}</version>
</dependency>
```

## Usage

### Base Codelist (no entry class defined)

```java
CodelistClient cl = new CodelistClient.Builder()
        .addScanPackages("com.example.model").
        .withDataProvider(new CSVDataProvider())
        .build();

Codelist title = cl.getCodelist("Title");
title.getEntry("DrSC.");
title.stream().filter(e -> e.getCode().contains("Dr")).forEach(System.out::println);
```

### Extended codelist

```java
@Getter
@Setter
@ToString(callSuper = true)
public class Title extends CodelistEntry {

    public enum Position {
        BEFORE, AFTER
    }

    private Position position;


    public Title() {
    }


    public Title(String code) {
        super(code);
    }


    public Title(CodelistEnum<Title> codeEnum) {
        super(codeEnum.toString());
    }
}
```

```java
CodelistClient cl = new CodelistClient.Builder()
        .addScanPackages("com.example.model").
        .withDataProvider(new CSVDataProvider())
        .build();

Codelist<Title> aps = cl.getCodelist(Title.class);
```

### Codelist references

Codelist entry can reference other codelists e.g.:

```java
public class PresentedPaperSection extends CodelistEntry {

    private PaperType paperType;
}
```

If codelist reference is base codelist without entry class, you have to use @EntryRef annotation to define codelist name.

Napr.

```java
public class InsuranceProduct extends CodelistEntry {


    @EntryRef("InsuranceCompany")
    private CodelistEntry company;
}
```

Same for collection references:

```java
public class InsuranceProduct extends CodelistEntry {


    @EntryRef("InsuranceCompany")
    private List<CodelistEntry> companies;
}
```
### Embedded types

Every extended codelist may define properties of simple types, but also more complex (embedded) types.
Class that define embedded type has contain `@Embeddable` annotation. Embedded type class does not need to
extend CodelistEntry class, but can contain reference to another codelist entry.

Embedded type is only wrapper for some subset of values.

```java
public class BusinessPlace extends CodelistEntry {

    private LegalSubject company;
}
```

```java
@Embeddable
public class LegalSubject {

    private String name;
    private String ico;
    private String dic;
    private String icDph;
    private String centralRegister;
    private Boolean taxPayer;
    private Address businessAddress;
}

@Embeddable
public class Address {

    private String street;
    private String referenceNumber;
    private String zipCode;
    private String registerNumber;
    private String city;
    private String displayValue;
}
```

Systém podporuje viacero zdrojov dát. Ak daný čiselník používa iný zdrojový systém, ktorý má ine názvy polí,
je možné prepísať východzie mapovanie z triedy [CodelistEntry](src/main/java/camp/xit/kiwi/codelist/client/model/CodelistEntry.java)
pomocou anotácie [EntryMapping](src/main/java/camp/xit/kiwi/codelist/client/EntryMapping.java).

Príklad:

```java
@EntryMapping(provider = CrafterDataProvider.class, value = {
    @EntryFieldMapping(field = "code", mappedField = "ID"),
    @EntryFieldMapping(field = "name", mappedField = "DESCRIPTION"),
    @EntryFieldMapping(field = "days", mappedField = "DAYS")
})
public class PaymentDeferment extends CodelistEntry {

    private Integer days;
}
```

Anotáciu `@EntryMapping` je možné vložiť aj mimo samotnej tredy definujúcej číselník napr:

```java
@EntryMapping(provider = CrafterDataProvider.class, entryClass=PaymentDeferment.class, value = {
    @EntryFieldMapping(field = "code", mappedField = "ID"),
    @EntryFieldMapping(field = "name", mappedField = "DESCRIPTION"),
    @EntryFieldMapping(field = "days", mappedField = "DAYS")
})
class PaymentDefermentMapping {}
```

### Zdrojový systém

Každý zdrojový systém musí implementovať interface [DataProvider](src/main/java/camp/xit/kiwi/codelist/provider/DataProvider.java).
Momentálne je možné definovať maximálne jednu implementáciu zdrojového systému.

### Enumerácie

Je možné definovať enumeráciu pre daný číselník a to tak, že projekt, ktorý konzumuje toto API, si vytvorí enumeračnú triedu zodpovedajúcu požiadavkam. Najlepší zdroj príkladov sú [junit testy](src/test/java/camp/xit/kiwi/codelist/client/CodelistEnumTest.java). Pre každú enumeračnú triedu musí existovať odvodený číselník. Pre implicitne odvodené číselníky nie je potrebné definovať custom triedy.

#### Príklad použitia

```java
public class ContractState extends CodelistEntry {

    public enum States implements CodelistEnum<ContractState> {
        ACTIVE, INACTIV, INPROGRESS, XNA
    }
}
```

```java
public enum InsuranceProducts implements CodelistEnum<InsuranceProduct> {
    XSELL_A, XSELL_B, XNA, NONE
}
```

#### Použitie API

Použitie API je v oboch prípadoch rovnaké:

Potom môžeš použiť priamo [CodelistClient](src/main/java/camp/xit/kiwi/codelist/client/CodelistClient.java):

```java
CodelistClient cl = new CodelistClient.Builder().getClient();
ContactState activeState = cl.getEntry(ContactState.States.ACTIVE);
```

alebo metôdu triedy [Codelist](src/main/java/camp/xit/kiwi/codelist/client/model/Codelist.java)

```java
CodelistClient cl = new CodelistClient.Builder().getClient();
Codelist<ContractState> csc = cl.getCodelist(ContractState.class);
scs.getEntry(ContractState.States.INACTIV);
```

resp.

Potom môžeš použiť priamo [CodelistClient](src/main/java/camp/xit/kiwi/codelist/client/CodelistClient.java):

```java
CodelistClient cl = new CodelistClient.Builder().getClient();
InsuranceProduct xsellA = cl.getEntry(InsuranceProducts.XSELL_A);
```

alebo metôdu triedy [Codelist](src/main/java/camp/xit/kiwi/codelist/client/model/Codelist.java)

```java
CodelistClient cl = new CodelistClient.Builder().getClient();
Codelist<InsuranceProduct> ipc = cl.getCodelist(InsuranceProduct.class);
InsuranceProduct xsellA = ipc.getEntry(InsuranceProducts.XSELL_A);
```

## Create Release

mvn clean release:prepare release:perform -DpushChanges=false -DlocalCheckout=true -Darguments='-Dmaven.javadoc.failOnError=false -Dmaven.deploy.skip=true -Dmaven.site.skip=true'