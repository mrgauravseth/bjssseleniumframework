export rootFile=$1
export url=$2
java -cp lib\tdi.jar;lib\log4j-1.2.15.jar;lib\jdom-1.1.jar;lib\postgresql-9.2-1003.jdbc4.jar tdi.MainClass %rootFile% -sqlOutputFile sqloutput.txt -username ers -password ers -url %url%

