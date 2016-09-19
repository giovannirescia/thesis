FILE = to_bliss
PWD        = $(shell pwd)
BINDIR = ../bin

.PHONY: clean clean-all
make:
	@echo Compiling with GHC
	ghc $(FILE).hs -rtsopts
	@echo Removing .hi files
	-rm *.hi
	@echo Removing .o files
	-rm *.o
	@echo Moving executable to bin directory
	mv $(FILE) $(BINDIR)

clean:
	@echo Removing .hi files
	-rm *.hi
	@echo Removing .o files
	-rm *.o

clean-all: clean
	@echo Removing executable files
	-rm $(BINDIR)/*