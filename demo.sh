#!/bin/sh

cd build/classes/
xterm -hold -title "server" -e "java server_ex 25000 " &
sleep 1
xterm -hold -title "Sheldon" -e "java reader_ex pull 180 Sheldon localhost 25000" &
xterm -hold -title "Leonard" -e "java reader_ex pull 180 Leonard localhost 25000" &
xterm -hold -title "Penny" -e "java reader_ex pull 180 Penny localhost 25000" &

