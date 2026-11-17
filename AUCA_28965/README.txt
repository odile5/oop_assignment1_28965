AUCA_ID - School Management System (console, Java 17+)

COMPILE (from inside the AUCA_ID folder)
  Linux/Mac : mkdir -p out && javac -d out $(find src -name "*.java")
  Windows   : dir /s /b src\*.java > files.txt && mkdir out && javac -d out @files.txt

RUN
  java -cp out com.auca.main.Main

Tips while using the forms: type BACK (previous field) or CANCEL (discard the form).
Status/choice fields accept the number or the word (e.g. 1 or ACTIVE).
Dates: YYYY-MM-DD. Study year: 1-6 (Section 8 of the assignment).
