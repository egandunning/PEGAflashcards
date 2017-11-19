# PEGAflashcards
A command line tool for learning PEGA key terms.

A PEGA PDN account is required to download the HTML glossary document.

## Running the program

Download the runnable jar file.

Run the jar file with `java -jar glossary.jar` to see the list of possible command line arguments.

Run `java -jar glossary.jar init /path/to/glossary.html` to generate the glossary file. The glossary
file is a serialized Java object that is stored in `glossaryData.obj`

To view random flashcards, run `java -jar glossary.jar random`, then press enter to view a term,
press enter to view its definition.


