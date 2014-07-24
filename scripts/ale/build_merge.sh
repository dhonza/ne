#!/bin/bash

FRAMES="frames001_001_2"

mkdir -p all
mkdir -p tmp
mkdir -p tmp/l0
mkdir -p tmp/l1
mkdir -p tmp/l2

unzip ${FRAMES}.zip -d tmp
unzip ${FRAMES}_l0.zip -d tmp/l0
unzip ${FRAMES}_l1.zip -d tmp/l1
unzip ${FRAMES}_l2.zip -d tmp/l2
 
for f in `ls tmp/*.png`
do
    echo $f
    g=`echo $f | sed 's/.*\\///'`
    echo $g
    montage tmp/$g tmp/l0/$g tmp/l1/$g tmp/l2/$g -tile 4x1 -geometry 160x210+0+0 -filter box all/$g
done

cd all

ffmpeg -y -i frame%06d.png ../${FRAMES}.m4v

cd ..

rm -rf all
rm -rf tmp