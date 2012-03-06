#!/usr/bin/env python
# -*- coding: utf-8 -*-
import time, operator, cv, os, sys, json, colorsys, OSC, socket
from SimpleCV import *
##import numpy as np

if len(sys.argv) == 1:
    host = 'localhost'
else:
    host = sys.argv[1]

c = OSC.OSCClient()
c.connect( ( host, 5500 ) )

table_width = 130
table_height = 80

mask_switch = 0

color_default_value = 100
color_default_value_hue = 30

savevalues = ['th_blue','th_yellow','th_red','min_blue','min_yellow','min_red','cropl','cropt','cropr','cropb','color_blue_hue','color_blue_sat','color_blue_val','color_yellow_hue','color_yellow_sat','color_yellow_val','color_red_hue','color_red_sat','color_red_val','avg_blue','avg_yellow','avg_red']


vision_save_files = "/group/teaching/sdp/sdp3/secure/vision_save_files"
if os.path.isdir( vision_save_files ):
    imagecapture = "camera"
else:
    imagecapture = "staticfile"

savefile = "cfg_" + socket.gethostname().split('.')[0] + ".txt"



def save_state(savefile):
    f = open(savefile,'w')
    for val in savevalues:
        str = val + "=" + json.dumps( globals()[val] ) + "\n"
        f.write(str)
    f.close()

def load_state(savefile):
    f = open(savefile,'r')
    for val in savevalues:
        str = f.readline()
        strval = str.split('=')[1][:-1]
        globals()[val] = json.loads(strval)
    f.close()

if not os.path.isfile( savefile ):
    load_state("cfg.txt")
    save_state(savefile)
else:
    load_state(savefile)

## GUI stuff

def control_th_blue(th):
    global th_blue
    th_blue = int(th)

def control_th_yellow(th):
    global th_yellow
    th_yellow = int(th)

def control_th_red(th):
    global th_red
    th_red = int(th)

def control_min_blue(th):
    global min_blue
    min_blue = int(th)

def control_min_yellow(th):
    global min_yellow
    min_yellow = int(th)

def control_min_red(th):
    global min_red
    min_red = int(th)

def control_mask(ch):
    global mask_switch
    mask_switch = int(ch)

def control_cropl(val):
    global cropl
    cropl = int(val)

def control_cropr(val):
    global cropr
    cropr = int(val)

def control_cropt(val):
    global cropt
    cropt = int(val)

def control_cropb(val):
    global cropb
    cropb = int(val)

def control_color_blue_hue(val):
    global color_blue_hue
    color_blue_hue = int(val)

def control_color_blue_sat(val):
    global color_blue_sat
    color_blue_sat = int(val)

def control_color_blue_val(val):
    global color_blue_val
    color_blue_val = int(val)

def control_color_yellow_hue(val):
    global color_yellow_hue
    color_yellow_hue = int(val)

def control_color_yellow_sat(val):
    global color_yellow_sat
    color_yellow_sat = int(val)

def control_color_yellow_val(val):
    global color_yellow_val
    color_yellow_val = int(val)

def control_color_red_hue(val):
    global color_red_hue
    color_red_hue = int(val)

def control_color_red_sat(val):
    global color_red_sat
    color_red_sat = int(val)

def control_color_red_val(val):
    global color_red_val
    color_red_val = int(val)


def color_RGB_to_HSV( c1 ):
    c2 = ( c1[0]/255., c1[1]/255., c1[2]/255. )
    c3 = colorsys.rgb_to_hsv( *c2 )
    c4 = c3[0]*180., c3[1]*255. ,c3[2]*255.
    return c4

def color_HSV_to_RGB( c1 ):
    c2 = ( c1[0]/180., c1[1]/255., c1[2]/255. )
    c3 = colorsys.hsv_to_rgb( *c2 )
    c4 = c3[0]*255., c3[1]*255. ,c3[2]*255.
    return c4

def color_mod_HSV(c1, hue, sat, val):
    c2 = color_RGB_to_HSV( c1 )
    c3 = c2[0] + hue, c2[1] + sat, c2[2] + val
    c4 = color_HSV_to_RGB( c3 )
    return c4


def to_color(a):
    b = tuple(reversed(a))[-3:]
    c = [ int(round(i)) for i in b ]
    return c

def int_tuple( a ):
    c = [ int(round(i)) for i in a ]
    return c


def avg_colors(colors):
    new = (0.,0.,0.)
    count = 0
    for color in colors:
        if color != 0:
            temp = color[:3]
            new = map(operator.add,new,temp)
            count += 1
    if count == 0:
        return 0
    return [i / count for i in new]

def initialize_color_stacks():
    global blue, yellow, red

    blue = []
    yellow = []
    red = []

    for i in range(5):
        blue.append(0)
        yellow.append(0)
        red.append(0)

def get_direction_from_blob( blob ):
    center = blob.centroid()
    sorted_points = sorted( blob.mConvexHull, key=lambda point: spsd.euclidean(center, point), reverse = True )
    return np.subtract( sorted_points[0], center )


cv.NamedWindow("control")
cv.ResizeWindow("control", 400,230)
cv.CreateTrackbar( "mask", "control", mask_switch, 3, control_mask )
cv.CreateTrackbar( "crop left", "control", cropl, 100, control_cropl )
cv.CreateTrackbar( "crop top", "control", cropt, 150, control_cropt )
cv.CreateTrackbar( "crop right", "control", cropr, 100, control_cropr )
cv.CreateTrackbar( "crop bottom", "control", cropb, 150, control_cropb )

cv.NamedWindow("ctr_blue")
cv.ResizeWindow("ctr_blue", 400,230)
cv.CreateTrackbar( "th blue", "ctr_blue", th_blue, 255, control_th_blue )
cv.CreateTrackbar( "min blue", "ctr_blue", min_blue, 300, control_min_blue )
cv.CreateTrackbar( "blue hue", "ctr_blue", color_blue_hue, 2*color_default_value_hue, control_color_blue_hue )
cv.CreateTrackbar( "blue sat", "ctr_blue", color_blue_sat, 2*color_default_value, control_color_blue_sat )
cv.CreateTrackbar( "blue val", "ctr_blue", color_blue_val, 2*color_default_value, control_color_blue_val )

cv.NamedWindow("ctr_yellow")
cv.ResizeWindow("ctr_yellow", 400,230)
cv.CreateTrackbar( "th yellow", "ctr_yellow", th_yellow, 255, control_th_yellow )
cv.CreateTrackbar( "min yellow", "ctr_yellow", min_yellow, 300, control_min_yellow )
cv.CreateTrackbar( "yellow hue", "ctr_yellow", color_yellow_hue, 2*color_default_value_hue, control_color_yellow_hue )
cv.CreateTrackbar( "yellow sat", "ctr_yellow", color_yellow_sat, 2*color_default_value, control_color_yellow_sat )
cv.CreateTrackbar( "yellow val", "ctr_yellow", color_yellow_val, 2*color_default_value, control_color_yellow_val )

cv.NamedWindow("ctr_red")
cv.ResizeWindow("ctr_red", 400,230)
cv.CreateTrackbar( "th red", "ctr_red", th_red, 255, control_th_red )
cv.CreateTrackbar( "min red", "ctr_red", min_red, 300, control_min_red )
cv.CreateTrackbar( "red hue", "ctr_red", color_red_hue, 2*color_default_value_hue, control_color_red_hue )
cv.CreateTrackbar( "red sat", "ctr_red", color_red_sat, 2*color_default_value, control_color_red_sat )
cv.CreateTrackbar( "red val", "ctr_red", color_red_val, 2*color_default_value, control_color_red_val )


## Static file input
if imagecapture == "staticfile":
    file = "shot0002.png"
    img = Image(file)

## Camera input
elif imagecapture == "camera":
    cam = Camera(prop_set = {"width": 768, "height": 576})
    img = cam.getImage()

## Start processing

width = int( (img.width-cropl-cropr)*1.2 )
height = int( (img.height-cropt-cropb)*1.2 )

display = Display( (width,height) )


mode = "null"

mouse_down = False
key_down = False
frozen = False
count = 0

blue = []
yellow = []
red = []
initialize_color_stacks()

index = 1
finalTime = 1



## BIG LOOP

while not display.isDone():

    cv.WaitKey(2)
    startTime = time.clock()

    event = pg.event.poll()
    keyinput = pg.key.get_pressed()

    if keyinput[pg.K_s]:
        save_state(savefile)
        print "state saved"

    if keyinput[pg.K_l]:
        load_state(savefile)
        print "state loaded"

    if keyinput[pg.K_q]:
        sys.exit()

    if keyinput[pg.K_f] and not frozen:
        frozen = True
        if imagecapture == "camera":
            frozen_cam = cam.getImage()
        index = 1
        mode = "null"

## Live feed

    if imagecapture == "camera":
        if not frozen:
            img = cam.getImage()
        else:
            img = frozen_cam

    if imagecapture == "staticfile":
        img = Image(file)


    img = img.crop( x=int( cropl ), y=int( cropt ), w=int( img.width-cropl-cropr ), h=int( img.height-cropb-cropt ) )
    dl = DrawingLayer( ( img.width, img.height ) )


## Processing frozen

    if frozen:

## Keyboard input

        if keyinput[pg.K_n]:
            index = 1
            mode = "null"
            frozen = False
            initialize_color_stacks()

        elif keyinput[pg.K_b]:
            index = 1
            mode = "blue"

        elif keyinput[pg.K_y]:
            index = 1
            mode = "yellow"

        elif keyinput[pg.K_r]:
            index = 1
            mode = "red"

        elif keyinput[pg.K_1]:
            index = 1

        elif keyinput[pg.K_2]:
            index = 2

        elif keyinput[pg.K_3]:
            index = 3

        elif keyinput[pg.K_4]:
            index = 4

## Mouse input

        if display.mouseLeft and mode != "null":
            vars()[mode][index] = ( display.mouseX, display.mouseY )
            cv.SetTrackbarPos(mode + " hue", "ctr_" + mode, color_default_value_hue )
            cv.SetTrackbarPos(mode + " sat", "ctr_" + mode, color_default_value )
            cv.SetTrackbarPos(mode + " val", "ctr_" + mode, color_default_value )


## Clean red point

        red[3] = 0
        red[4] = 0

## Draw points on screen and calculating the average colors

        bluecolors = []
        yellowcolors = []
        redcolors = []

        for point in blue:
            if point != 0:
                dl.centeredRectangle(point,(3,3),color=Color.BLACK)
                bluecolors.append(img.getPixel(point[0],point[1]))
        for point in yellow:
            if point != 0:
                dl.centeredRectangle(point,(3,3),color=Color.BLACK)
                yellowcolors.append(img.getPixel(point[0],point[1]))
        for point in red:
            if point != 0:
                dl.centeredRectangle(point,(3,3),color=Color.BLACK)
                redcolors.append(img.getPixel(point[0],point[1]))

## Calculating average colors

        if len(bluecolors) != 0:
            avg_blue = int_tuple(avg_colors(bluecolors))

        if len(yellowcolors) != 0:
            avg_yellow = int_tuple(avg_colors(yellowcolors))

        if len(redcolors) != 0:
            avg_red = int_tuple(avg_colors(redcolors))

## END FROZEN
    mod_blue = int_tuple( color_mod_HSV( avg_blue, color_default_value_hue - color_blue_hue, color_default_value - color_blue_sat, color_default_value - color_blue_val ) )
    mod_yellow = int_tuple( color_mod_HSV( avg_yellow, color_default_value_hue - color_yellow_hue, color_default_value - color_yellow_sat, color_default_value - color_yellow_val ) )
    mod_red = int_tuple( color_mod_HSV( avg_red, color_default_value_hue - color_red_hue, color_default_value - color_red_sat, color_default_value - color_red_val ) )

## Calculating masks

    mask_blue = img.colorDistance(color=mod_blue).binarize(th_blue)
    mask_yellow = img.colorDistance(color=mod_yellow).binarize(th_yellow)
    mask_red = img.colorDistance(color=mod_red).binarize(th_red)

    bm = BlobMaker()

    blobs_blue = bm.extractFromBinary(mask_blue,mask_blue,minsize=min_blue)
    blobs_yellow = bm.extractFromBinary(mask_yellow,mask_yellow,minsize=min_yellow)
    blobs_red = bm.extractFromBinary(mask_red,mask_red,minsize=min_red)

    blobs_blue = sorted( blobs_blue, key=lambda k: k.area(), reverse = True )
    blobs_yellow = sorted( blobs_yellow, key=lambda k: k.area(), reverse = True )
    blobs_red = sorted( blobs_red, key=lambda k: k.area(), reverse = True )




## Start OSC
    bundle = OSC.OSCBundle()
    #bundle.append( {'addr':"/table/room", 'args':["big"]} )


    if len(blobs_blue) == 0:
        blue_visible = False
        bundle.append( {'addr':"/table/blue/visible", 'args':[0]} )
    else:
        blue_visible = True
        bundle.append( {'addr':"/table/blue/visible", 'args':[1]} )

        blob_blue = blobs_blue[0]
        bundle.append( {'addr':"/table/blue/posx", 'args':[blob_blue.centroid()[0]*table_width/img.width] } )
        bundle.append( {'addr':"/table/blue/posy", 'args':[blob_blue.centroid()[1]*table_height/img.height] } )

        blob_blue.drawOutline(color = Color.BLUE, layer=dl,width=2)
        #blob_blue.drawHull(color = Color.BLUE, layer=dl,width=2)
        dl.circle((int(blob_blue.centroid()[0]),int(blob_blue.centroid()[1])),30,Color.BLUE,width=3)
        blue_direction =  get_direction_from_blob( blob_blue )
        arrow_blue = dl.line( blob_blue.centroid(), np.add( blob_blue.centroid(), blue_direction ), color = Color.YELLOW, width = 3 )

        bundle.append( {'addr':"/table/blue/dirx", 'args':[ blue_direction[0]/np.linalg.norm(blue_direction) ] } )
        bundle.append( {'addr':"/table/blue/diry", 'args':[ blue_direction[1]/np.linalg.norm(blue_direction) ] } )


    if len(blobs_yellow) == 0:
        yellow_visible = False
        bundle.append( {'addr':"/table/yellow/visible", 'args':[0]} )
    else:
        yellow_visible = True
        bundle.append( {'addr':"/table/yellow/visible", 'args':[1]} )

        blob_yellow = blobs_yellow[0]
        bundle.append( {'addr':"/table/yellow/posx", 'args':[blob_yellow.centroid()[0]*table_width/img.width] } )
        bundle.append( {'addr':"/table/yellow/posy", 'args':[blob_yellow.centroid()[1]*table_height/img.height] } )

        blob_yellow.drawOutline(color = Color.YELLOW, layer=dl,width=2)
        #blob_yellow.drawHull(color = Color.YELLOW, layer=dl,width=2)
        dl.circle((int(blob_yellow.centroid()[0]),int(blob_yellow.centroid()[1])),30,Color.YELLOW,width=3)

        yellow_direction =  get_direction_from_blob( blob_yellow )
        arrow_yellow = dl.line( blob_yellow.centroid(), np.add( blob_yellow.centroid(), yellow_direction ), color = Color.BLUE, width = 3 )

        bundle.append( {'addr':"/table/yellow/dirx", 'args':[ yellow_direction[0]/np.linalg.norm(yellow_direction) ] } )
        bundle.append( {'addr':"/table/yellow/diry", 'args':[ yellow_direction[1]/np.linalg.norm(yellow_direction) ] } )


    if len(blobs_red) == 0:
        red_visible = False
        bundle.append( {'addr':"/table/red/visible", 'args':[0]} )
    else:
        red_visible = True
        bundle.append( {'addr':"/table/red/visible", 'args':[1]} )

        blob_red = blobs_red[0]
        bundle.append( {'addr':"/table/red/posx", 'args':[blob_red.centroid()[0]*table_width/img.width] } )
        bundle.append( {'addr':"/table/red/posy", 'args':[blob_red.centroid()[1]*table_height/img.height] } )

        blob_red.drawOutline(color = Color.RED, layer=dl,width=2)
        dl.circle((int(blob_red.centroid()[0]),int(blob_red.centroid()[1])),30,Color.RED,width=3)


    try:
        c.send(bundle,timeout=None)
    except OSC.OSCClientError,e:
        print str(count) + " error",e

## End OSC



## Displaying masks
    if mask_switch == 0:
        out = img
    elif mask_switch == 1:
        out = mask_blue
    elif mask_switch == 2:
        out = mask_yellow
    elif mask_switch == 3:
        out = mask_red




## Applying overlays to the main channel

    out.addDrawingLayer(dl)
    if finalTime == 0:
        finaltTime = 1
    out.dl().ezViewText("FPS %0.1f" % (1/finalTime), (0,0))
    if frozen:
        out.dl().ezViewText("frozen", (80,0))
    if mode != "null":
        out.dl().ezViewText("mode: " + mode + str(index), (200,0))


##    dl.rectangle( (500,0),(20,20), mod_blue, filled=True )
##    dl.rectangle( (520,0),(20,20), mod_yellow, filled=True )
##    dl.rectangle( (540,0),(20,20), mod_red, filled=True )

    out_overlay = out.applyLayers()
    #out_overlay.save("out.jpg")

    display.writeFrame(out_overlay)



## Measuring FPS
    endTime = time.clock()
    finalTime = endTime - startTime
    count += 1


c.close()


