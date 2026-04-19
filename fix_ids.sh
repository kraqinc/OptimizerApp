#!/data/data/com.termux/files/usr/bin/bash

echo "Escaneando IDs en Java..."

IDS=$(grep -rho "R.id.[a-zA-Z0-9_]*" app/src/main/java | sed 's/R.id.//g' | sort -u)

echo "IDs encontrados:"
echo "$IDS"

echo "Actualizando XML base..."

FILE="app/src/main/res/layout/activity_main.xml"

for id in $IDS
do
  if ! grep -q "@+id/$id" $FILE; then
    echo "Agregando ID: $id"
    sed -i "/</FrameLayout>/i <View android:id=\"@+id/$id\" android:layout_width=\"0dp\" android:layout_height=\"0dp\" />" $FILE
  fi
done

echo "Listo."
