TEX_FILE=proposal

default: pdf

pdf:
		pdflatex $(TEX_FILE).tex
		bibtex $(TEX_FILE).aux
		pdflatex $(TEX_FILE).tex
		pdflatex $(TEX_FILE).tex

clean:
	rm -f $(TEX_FILE).log $(TEX_FILE).aux
	rm -f $(TEX_FILE).pdf
