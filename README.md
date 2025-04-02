# pdiff Utility

The `pdiff` utility is designed to manage and compare PlantUML diagrams across different versions.
It facilitates the collection, execution, and comparison of diagrams to ensure non-regression in PlantUML outputs.

## 🏷️ Badges
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=plantuml_pdiff&metric=alert_status)](https://sonarcloud.io/project/overview?id=plantuml_pdiff)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=plantuml_pdiff&metric=bugs)](https://sonarcloud.io/summary/overall?id=plantuml_pdiff)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=plantuml_pdiff&metric=code_smells)](https://sonarcloud.io/summary/overall?id=plantuml_pdiff)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=plantuml_pdiff&metric=duplicated_lines_density)](https://sonarcloud.io/summary/overall?id=plantuml_pdiff)

## 🎯 Objectives

The primary objectives of the `pdiff` utility are:

- **Maintain an official collection of diagrams**: Serve as a repository for diagrams used in PlantUML non-regression testing.
- **Compare output changes**: Analyze and highlight differences in diagram outputs across various PlantUML versions.

## 🚀 CI Workflow

The project includes an automated GitHub Actions workflow designed to simplify the integration of new diagrams:

- **Create a branch** whose name starts with `input`.
- **Push diagram files** into the `/input` folder of your branch (this can be done directly via the GitHub website).
- **Automated processing**: the CI will automatically process these files, generate updated diagram data in the `/db` directory, and push the results into a new branch.
- **Submit a pull request**: Merge the generated `/db` files back into the `main` branch through a pull request.

### Advantages:
- Simplifies the contribution process for new diagrams.
- Automatically maintains and updates the official collection, ensuring consistency and accuracy.
- Streamlines review and integration workflows through clearly defined CI-driven procedures.

  
## 🧰 Requirements

To use this utility, you need:

- **Java 17 or later**: Ensure that Java 17 or a newer version is installed and properly configured on your system.
- **PlantUML Library**: At least one version of the compiled `plantuml.jar` library, which can be downloaded from [PlantUML releases](https://github.com/plantuml/plantuml/releases).
- **GraphViz**: A working installation of GraphViz.


## 🛠 Compilation

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



## 📖 Principles

The `pdiff` tool operates in three primary modes:

- **insert** (optional):

  Note that you don't have to run this mode since the project already contains some diagrams. Use it only if you want to add your diagrams to the collection. If you do, you are welcome to submit a pull request so that your examples can be included in the official collection.

  During the insertion phase, the tool scans all files in the `/raw` directory. If it finds diagrams, it extracts the corresponding diagram text, computes the SHA-1 signature in base 36 of the diagram, and creates a file in `/db` with this signature containing the diagram text. This ensures that the `/db` collection contains a set of diagrams for non-regression testing.


- **run**:

  During the run phase, the tool generates PNG images for all diagrams in the `/db` collection and produces an HTML result file containing all images. For each image, a CRC is calculated.

- **diff**:

  In the diff phase, the tool compares the results of two different runs. Using the CRC, it identifies diagrams that produce different results and documents these differences in an HTML file.

## ℹ How to Use

### ➕ Insertion

This mode allows you to add new diagrams to the collection `db`. Each diagram is assigned a SHA-1 signature which acts as its unique identifier.
Diagrams must be self-contained and should not include external files, except those from the standard library.


```sh
java -jar build/libs/pdiff-all.jar insert -u foo@dummy.com
```

### ⚙ Run

This mode executes a specified version of PlantUML on the entire collection of diagrams. Note that the PlantUML library is not bundled with the `pdiff` tool; you must
[download](https://github.com/plantuml/plantuml/releases) or compile the desired version separately.

The results of the run are stored in a specific directory and remain local, as they depend on various factors, including the PlantUML version, GraphViz version, and the machine's configuration.

The command line usage differs slightly because you need to specify the JAR file (or directory) containing the PlantUML version you intend to use.

On Linux:

```sh
java -cp plantuml-1.2023.13.jar:build/libs/pdiff-all.jar com.pdiff.Main run
```

On Windows:
```sh
java -cp "plantuml-1.2023.13.jar;build/libs/pdiff-all.jar" com.pdiff.Main run
```


### 🔀 Compare

This mode compares the results of two different runs and provides a summary of the differences between them. The summary is saved in an HTML file.

```sh
java -jar build/libs/pdiff-all.jar diff 1.2023.13 1.2024.5
```

