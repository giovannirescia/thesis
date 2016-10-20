name := "prueba"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Phenoscape Maven repository" at "http://phenoscape.svn.sourceforge.net/svnroot/phenoscape/trunk/maven/repository"

libraryDependencies += "org.phenoscape" %% "scowl" % "1.1"

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.6"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"

onLoadMessage := ""

showSuccess := false