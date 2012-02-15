# -*- coding: utf-8 -*-
import cv
from SimpleCV import *

file = "debug.jpg"
img = Image(file)

img = img.crop(x=38,y=95,w=648,h=392)
mask_red = img.colorDistance( color=(208, 28, 22) ).binarize(88)   