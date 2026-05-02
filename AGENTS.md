# AGENTS.md — CookBook

A Java knowledge-base repo (docs + runnable code). Not a production service — it is a learning/tutorial project.

## Build & Test

- **Compile**: `./mvnw clean compile`
- **Run all tests**: `./mvnw test`
- **Run specific test class**: `./mvnw -Dtest=AlgorithmBasicsTest test`
- **Package (skip tests)**: `./mvnw clean package -DskipTests=true` — matches CI
- **Wrapper script**: `./mvnw` is present and preferred over system `mvn`

## Repo Structure

```
├── pom.xml                         # Maven parent POM, packaging=pom
├── src/main/java/org/byron4j/cookbook/
│   ├── algrithms/                  # Data structure & algorithm demos
│   ├── javacore/                   # Enum, annotation, reflection, proxy demos
│   ├── netty/                      # NIO/Netty examples
│   ├── rocketmq/                   # MQ producer/consumer examples
│   ├── zk/                         # Zookeeper examples
│   └── ...                         # Other topic directories
├── src/test/java/.../algrithms/    # JUnit tests for algorithms
└── 数据结构和算法/                    # Markdown docs (Chinese paths)
    ├── 数据结构/                    # 10 data-structure docs
    └── 算法/                       # 18 algorithm docs
```

- `packaging=pom` in root POM means this is an aggregator/module project with no deployable artifact.
- Chinese directory names (`数据结构和算法`) are intentional and used in README links.

## Testing Conventions

- **JUnit 4** is used (`junit:junit` in POM).
- **Test classes**: `*Test.java` in `src/test/java/...`.
- Key test classes:
  - `AlgorithmBasicsTest` — validates sorting, tree traversal, linear list ops
  - `DataStructureCompletenessTest` — validates linked list, hash table, BST, AVL, B-tree demos
  - `QuickSortTest` — legacy quicksort test
- Tests are lightweight (no external services required).

## CI

- `.travis.yml` runs: `mvn clean package -DskipTests=true`
- No GitHub Actions workflows present.
- `test.sh` exists but is a no-op placeholder (`echo "Hello World!"`).

## Key Constraints

- **Java 8** target (`source/target=8`), but uses **Spring Boot 2.7.18** parent.
- **No linter/formatter config** (no Checkstyle, Spotless, or similar).
- **No type checker** beyond Java compiler.
- **No pre-commit hooks** configured.

## Adding Docs vs Code

- **New algorithm doc**: Create under `数据结构和算法/算法/` with naming pattern `N-标题.md`.
- **New data structure doc**: Create under `数据结构和算法/数据结构/` with pattern `NN-标题.md`.
- **New demo class**: Create under `src/main/java/org/byron4j/cookbook/algrithms/...`.
- **New test**: Create under `src/test/java/org/byron4j/cookbook/algrithms/`.
- **Update README.md** root index when adding new docs.

## Style Notes

- Markdown docs use **ASCII diagrams** for data structures (not images).
- Docs follow pattern: 概念 → 来源 → 知识点 → 数据结构/算法 → 最佳实践.
- Java code uses Chinese comments in some places (legacy from original author).

## Gotchas

- `mvnw` may need `chmod +x mvnw` on fresh clone.
- Some dependencies have `provided` or `compile` scope explicitly set; check POM before assuming defaults.
- `json-lib` is marked deprecated in POM with security warning — do not use in new code.
