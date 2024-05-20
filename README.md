# Objectives

The "pdiff" utility aims to:

- Provide an official collection of diagrams used for PlantUML non-regression tests.
- Compare changes between different PlantUML versions.

# Principles

The tool operates in several modes:

## INSERT

This mode allows you to add new diagrams to the collection. For each diagram, a SHA1 signature is calculated, which 
will serve as the diagram's name. Currently, each diagram must be self-contained: no external file inclusions, 
except for inclusion from the standard library.

## RUN

This mode allows you to run a specific version of PlantUML on the entire collection. Note that the PlantUML library 
is not included with the "pdiff" tool. You need to download or compile the library version you wish to use. 
The RUN results are stored in a specific directory. These results are not intended to be committed to this repository; 
they are kept locally. This is because the RUN results depend not only on the PlantUML version but also on the 
GraphViz version and the configuration of the machine that performed the RUN.

## DIFF

This mode compares two different RUN results and provides a summary of the differences.
