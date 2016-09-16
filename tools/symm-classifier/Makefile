VANALYZER_SRC = Main.hs
VANALYZER     = var_analyzer
PWD           = $(shell pwd)
BINDIR        = ../bin

.PHONY: clean clean-all
make:
	@echo Compiling with GHC
	ghc $(VANALYZER_SRC) -o $(VANALYZER)
	@echo Removing .hi files
	-rm *.hi
	@echo Removing .o files
	-rm *.o

	@echo Moving executable to bin directory
	mv $(VANALYZER) $(BINDIR)

clean:
	@echo Removing .hi files
	-rm *.hi
	@echo Removing .o files
	-rm *.o

clean-all: clean
	@echo Removing executable files
	-rm $(BINDIR)/*