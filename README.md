# pdiff Utility

The `pdiff` utility is designed to manage and compare PlantUML diagrams across different versions.
It facilitates the collection, execution, and comparison of diagrams to ensure non-regression in PlantUML outputs.

## Objectives

The main goals of the `pdiff` utility are to:

- Maintain an official collection of diagrams for PlantUML non-regression testing.
- Compare output changes between different PlantUML versions.

## Compilation

With Gradle, use the following command:
```sh
./gradlew shadowJar
```

After the compilation, the following help could be displayed:

```sh
java -jar build/libs/pdiff-all.jar

Usage: <main class> [command] [command options]
  Commands:
    insert      This command allows you to insert new diagrams into the 
            collection. 
      Usage: insert [options]
        Options:
          -u, --user
            Specifies the user who inserts data into the collection
            Default: Arnaud.Roques

    run      This command runs a PlantUML version on the entire collection
      Usage: run [options]
        Options:
          -r, --run
            Specifies the name of the run
          -s, --slot
            Specifies the number of parallel slots
            Default: 6

    diff      This command compares two different runs
      Usage: diff run1 run2
```



## Principles

The `pdiff` tool operates in three primary modes:

- **insert**:

  During the insertion phase, the tool scans all files in the `/raw` directory. If it finds diagrams, it extracts the corresponding diagram text, computes the SHA-1 signature in base 36 of the diagram, and creates a file in `/db` with this signature containing the diagram text. This ensures that the `/db` collection contains a set of diagrams for non-regression testing.

- **run**:

  During the run phase, the tool generates PNG images for all diagrams in the `/db` collection and produces an HTML result file in the `/run` directory containing all images. For each image, a CRC is calculated.

- **diff**:

  In the diff phase, the tool compares the results of two different runs. Using the CRC, it identifies diagrams that produce different results and documents these differences in an HTML file.

## How to Use

### Insertion

This mode allows you to add new diagrams to the collection `db`. Each diagram is assigned a SHA-1 signature which acts as its unique identifier.
Diagrams must be self-contained and should not include external files, except those from the standard library.


```sh
java -jar build/libs/pdiff-all.jar insert -u foo@dummy.com
```

### Run

This mode executes a specified version of PlantUML on the entire collection of diagrams. Note that the PlantUML library is not bundled with the `pdiff` tool; you must download or compile the desired version separately. The results of the run are stored in a specific directory and remain local, as they depend on various factors, including the PlantUML version, GraphViz version, and the machine's configuration.

The command line usage differs slightly because you need to specify the JAR file (or directory) containing the PlantUML version you intend to use.

```sh
java -cp plantuml-1.2023.13.jar:build/libs/pdiff-all.jar com.pdiff.Main run
```

### Compare

This mode compares the results of two different runs and provides a summary of the differences between them. The summary is saved in an HTML file.

```sh
java -jar build/libs/pdiff-all.jar diff 1.2023.13 1.2024.5
```

