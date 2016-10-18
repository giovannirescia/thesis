# Thesis Project: Finding Symmetries in Description Logics

This project's architecture consists in two modulos.

## First modulo: the translator, from Description Logic to Modal Logic

### A (really) brief introduction to Description Logic

Description logics (DL) is a family of formal knowledge representation languages, 
that are used in artificial intelligence to describe and reason about the relevant concepts of 
an application domain (known as _terminological knowledge_). It is of particular importance in providing a logical formalism 
for [ontologies](https://en.wikipedia.org/wiki/Ontology_(information_science)) and the
[Semantic Web](https://en.wikipedia.org/wiki/Semantic_Web): the Web Ontology Language [OWL] and its profile is based on DLs.

### From DL to ML

This modulo is entirely coded in Scala 2.11, using [Scowl](https://github.com/phenoscape/scowl): a Scala DSL allowing
a declarative approach to composing OWL expressions and axioms using the [OWL API](http://owlapi.sourceforge.net/).

All the dependencies are specified in the build.sbt file, since this project was created under sbt.

The basic usage provides a way to select an ontology and translate its TBox to Modal Logic formulas, some translations are
still missing and the translation of the ABox will be added shortly.

From sbt, run:

  `> run <option> <ontology>`

Where:

```
<option>: Whether to render or translate the axioms. `render` will do a pretty print of the ontology, and `translate` will translate the axioms into Modal Logic formulas.
<ontology>: Selects an ontology. It could be: fam, gal, dol, wine, semintec, vicodi, lubm, modlubm or all.
```

## Second modulo: finding symmetries in Modal Logic

This modulo was entirely coded by Ezequiel Orbe in Haskell. It will work like a 'black box' for now, until an Scala
implementation gets done.

The basic pipeline is:
```
kcnf -> sy4ncl -> bliss
```

#### KCNF

This modulo takes a Modal Logic formula in `intohylo` format and builds a Conjunctive Normal Form (CNF) of it.

#### SY4NCL

This modulo finds symmetries in a Modal Logic formula (the formula should be in CNF).

#### BLISS