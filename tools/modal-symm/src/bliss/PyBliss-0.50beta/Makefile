all: intpyblissmodule.cc setup.py
	unzip -o bliss-0.72.zip
	python setup.py install --home=.

clean:
	rm -f PyBliss.pyc setup.pyc
	rm -rf bliss-0.72
	rm -rf build
	rm -rf lib
