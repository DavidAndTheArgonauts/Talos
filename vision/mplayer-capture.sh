#!/bin/bash
store='/tmp/sdp_group3_capture3/'
cd="$PWD"
rm -rf "$store"
mkdir -p "$store"
cd "$store"
echo "Please press Ctrl + C, when you want to close mplayer and WAIT for it to delete our files!"
echo "If not the next person cannot delete them and we keep filling up the /tmp with rubbish."
read -p "Press any key to continue"
nice mplayer -tv device=/dev/video0:driver=v4l2:input=2:width=768:height=576:norm=pal:fps=25 tv://1 -aspect 4:3 -vf yadif,scale=768:576 -vo png:z=0
rm -rf "$store"
