#!/bin/sh
cd src || exit 1
javac -encoding UTF-8 --release 17 *.java && echo "Compilado com sucesso. Para rodar: cd src && java Main"
