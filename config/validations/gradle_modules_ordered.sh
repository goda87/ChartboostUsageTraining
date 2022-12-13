#!/bin/bash
grep '\":[A-Za-z:-]*\"' $1 > modules.txt
sort modules.txt > modules_ordered.txt
diff modules.txt modules_ordered.txt

if [[ $? -eq 0 ]]; then
    exit 0
else
    echo "settings.gradle.kts must be sorted. Please, sort it before commit your changes."
    exit 1
fi

rm modules.txt
rm modules_ordered.txt
