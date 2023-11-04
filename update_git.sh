#/bin/sh
date=`date +%m-%d-%Y`
#msg=" -m '"'Panel V3 migracion a $date'"'"
msg=" -m "'"'"Panel V2 migracion a $date"'"'
echo $msg
git add . --all
echo copy y paste lo siguiente
echo git commit $msg
echo git push -u origin rama_desarrollo main
echo "fgibarra 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"

