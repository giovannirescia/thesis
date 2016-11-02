# Thesis Project: Finding Symmetries in Description Logics

###  General Dependencies

* scala
* sbt
* ghc
* ghc-prof
* zlib
* [cabal](https://www.haskell.org/cabal/) (when running ./bootstrap.sh, run with --no-doc)

### Haskell dependencies (cabal install \<package\>)

* alex
* happy
* hylolib (>= 1.5.3)
* cmdargs

## General Architecture

### First module: the translator, from Description Logic to Modal Logic

#### A (really) brief introduction to Description Logic

Description logics (DL) is a family of formal knowledge representation languages, 
that are used in artificial intelligence to describe and reason about the relevant concepts of 
an application domain (known as _terminological knowledge_). It is of particular importance in providing a logical formalism 
for [ontologies](https://en.wikipedia.org/wiki/Ontology_(information_science)) and the
[Semantic Web](https://en.wikipedia.org/wiki/Semantic_Web): the Web Ontology Language [OWL] and its profile is based on DLs.

#### From DL to ML

This module is entirely coded in Scala 2.11, using [Scowl](https://github.com/phenoscape/scowl): a Scala DSL allowing
a declarative approach to composing OWL expressions and axioms using the [OWL API](http://owlapi.sourceforge.net/).

All the dependencies are specified in the build.sbt file, since this project was created under sbt.

The basic usage provides a way to select an ontology and translate its TBox to Modal Logic formulas, some translations are
still missing and the translation of the ABox will be added shortly.

From sbt, run:

  `> run <option> <ontology>`

Where:

```
<option>: Whether to render or translate the axioms. `render` will do a pretty print of the ontology, and `translate` will translate the axioms into Modal Logic formulas using the `.intohylo` format.
<ontology>: A string to match an ontology name, the algorithm will check a "starts with" to get the ontology(ies); if the name given is "all", the algorithm will work with all the ontologies.
```

### Second module: finding symmetries in Modal Logic

This modulo was entirely coded by Ezequiel Orbe in Haskell. It will work like a 'black box' for now, until an Scala
implementation gets done.

The basic pipeline is:
```
kcnf -> sy4ncl -> bliss
```

#### KCNF

This modulo takes a Modal Logic formula in `.intohylo` format and builds a Conjunctive Normal Form (CNF) of it.

#### SY4NCL

This modulo finds symmetries in a Modal Logic formula (the formula should be in CNF).

#### BLISS

This modulo takes some stuff as input and then do something with it.

## Putting all the pieces together

Just run
```
$ bash scripts/init.sh
$ bash scripts/sbt.sh
$ bash scripts/doall.sh
```

This scripts will:

* Create all the necessary directories:
  * Outputs
  * Ontologies
* Unzip the ontologies
* Make a soft build of tools for Modal Logic (for a rebuild, run `$ bash scripts/fbuild.sh')
* Translate all the ontologies to Modal Logic Formulas
* Search, and hopefully find, symmetries in those formulas

And BAM! 7 hours and 15 minutes later you got yourself a bunch of data...

The final output for each symmetry is in `output/final-output/<ontology>`

For playing around with an ontology and render or translate it, from the root directory of this project, run:
```
$ sbt
> run [render|translate] <ontology>
```

## Contact

For any question or suggestion, email me at giovannirescia89@gmail.com, or [open an issue on the tracker](https://github.com/giovannirescia/tesis/issues).