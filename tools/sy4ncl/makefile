FILE = dist/build/sy4ncl/sy4ncl
BIN = sy4ncl
BINDIR = bin

.PHONY: clean clean-all
make:
	@echo Configuring Cabal
	cabal configure #--enable-executable-profiling
	@echo Building with Cabal
	cabal build
	@echo Moving executable to bin directory
	cp $(FILE) $(BINDIR)

clean:
	@echo Removing dist folder
	-rm -rf dist
	@echo Removing executable files
	-rm $(BINDIR)/$(BIN)