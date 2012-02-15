mplayer -tv device=/dev/video0:driver=v4l2:input=2:width=768:height=576:norm=pal:fps=25:saturation=0:brightness=0:contrast=0:hue=0 tv://1 -aspect 4:3 -vf yadif,screenshot
