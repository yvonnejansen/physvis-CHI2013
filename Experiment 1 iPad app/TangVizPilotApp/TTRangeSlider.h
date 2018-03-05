//
//  TTRangeSlider.h
//  TuioPad
//
//  Created by Yvonne Jansen on 4/4/11.
//  Copyright 2011 INRIA AVIZ. All rights reserved.
//

#import <Foundation/Foundation.h>

#define SLIDER_HEIGHT 30
#define SLIDER_WIDTH 410
#define X 30
#define Y 0

@interface TTRangeSlider : UIControl {
    CGFloat minimumRangeLength; //the minimum allowed range size
    int     minTouchID, maxTouchID;
    BOOL    minActive, maxActive;
    UITouch *leftThumbRegisteredTouch, *rightThumbRegisteredTouch;
    float minPos, maxPos;
    CGPoint leftTouchCoord, rightTouchCoord;

    
	
    
	UIImageView *minSlider, *maxSlider, *trackImageView, *inRangeTrackImageView, *distributionView; // the sliders representing the min and max, and a background view;
}

@property (nonatomic) float minPos, maxPos, minimumRangeLength;
@property(readwrite,nonatomic,retain) UIImageView *distributionView;
@property(readwrite,nonatomic,retain) UIImageView *backgroundImageView, *leftCap, *rightCap;
@property(assign) CGPoint leftTouchCoord, rightTouchCoord;

- (void) valueChanged:(id)sender;
- (void)setMinThumbImage:(UIImage *)image;
- (void)setMaxThumbImage:(UIImage *)image;
- (void)setTrackImage:(UIImage *)image;
- (void)setInRangeTrackImage:(UIImage *)image;

@end




