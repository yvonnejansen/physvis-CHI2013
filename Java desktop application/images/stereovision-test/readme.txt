The images are from Hess and Copperstock's online stereo vision acuity test.
http://3d.mcgill.ca/

Image pairs were generated from the following anaglyph images:
http://3d.mcgill.ca/images/fd05s0.png
http://3d.mcgill.ca/images/bd05s0.png

The actual test uses more images and computes the acuity by decreasing stimulus size (stereo disparity). The image file naming convention is:
- fd or bd: in front or behind the screen
- ## (two digits): stereo disparity, from 01 (very small) to 10 (max)
- s#: repetition

The two images we took test stereo acuity half-way between the minimum and maximum values (05).

The procedure for separating image pairs is the following:
- Load the anaglyph in photoshop
- Duplicate the layer
- Hide the top (new) layer, select the background layer
- Open Levels, change min output level from 0 to 255 for Blue and Green layers
- Select the Threshold tool, move the slider between the two peaks, confirm
- Save the image as xxx-left.png
- Show the top layer and select it
- use the Levels tool as above, but for Red and Blue layers
- Use the Threshold tool as above
- Save as xxx-right.png
