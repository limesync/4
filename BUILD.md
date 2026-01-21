# Building MobRealmsCore

## Quick Build

If Maven is installed:
```bash
mvn clean package
```

The compiled JAR will be in `target/MobRealmsCore-1.0.0.jar`

## Building Without Maven

If Maven is not installed on your system, you can use a Docker container to build:

```bash
docker run -it --rm -v "$(pwd)":/app -w /app maven:3.9-eclipse-temurin-17 mvn clean package
```

## What Gets Built

The build process:
1. Compiles all Java source files (31 files)
2. Packages resources (config.yml, messages.yml, plugin.yml)
3. Creates shaded JAR with dependencies
4. Outputs to `target/MobRealmsCore-1.0.0.jar`

## Dependencies

The plugin uses:
- Paper API 1.20.4 (provided by server)
- PlaceholderAPI 2.11.5 (provided by server)

No external dependencies are bundled.

## Build Requirements

- Java 17 or higher
- Maven 3.6 or higher
- Internet connection (for downloading dependencies on first build)

## Clean Build

To ensure a fresh build:
```bash
mvn clean package
```

## Skip Tests (if added in future)

```bash
mvn clean package -DskipTests
```

## Troubleshooting

### Maven not found
Install Maven or use Docker method above.

### Java version mismatch
Ensure Java 17+ is installed:
```bash
java -version
```

### Build fails
Check error messages in console. Common issues:
- Missing dependencies (Maven will download automatically)
- Syntax errors in code
- Network issues preventing dependency download

## IDE Integration

### IntelliJ IDEA
1. Open project folder
2. IntelliJ will auto-detect Maven project
3. Use "Maven" panel on right
4. Run "package" goal

### Eclipse
1. Import as Maven project
2. Right-click project → Run As → Maven build
3. Enter "clean package" as goals

### VS Code
1. Install "Extension Pack for Java"
2. Open project folder
3. Use Maven panel in sidebar
