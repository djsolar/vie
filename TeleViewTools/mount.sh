mkdir -p teledata
chmod 777 teledata* -R
mount -t cifs //192.168.6.20/tele/Release/Tele_20141110_D[14.01.01_20141016]/data ./teledata/ -o user=mx,pass=mx123
