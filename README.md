# cs4928_cafe_lab

## Compile

```bash
mvn compile
```

## Run Week Demo

```bash
mvn exec:java -Dexec.mainClass="com.cafepos.demo.Week2Demo"
# Replace Week2Demo with Week3Demo, Week4Demo, etc.
```

## Generate UML Diagram (PNG)

```bash
mvn compile
plantuml -DPLANTUML_LIMIT_SIZE=16384 diagrams/puml/*.puml -o ../png
# Output: diagrams/png/*.png
```

**Note:** Each compile generates a new timestamped diagram file (not overwritten).

**Install Dependencies** (if needed):

```bash
paru -S plantuml graphviz
```

