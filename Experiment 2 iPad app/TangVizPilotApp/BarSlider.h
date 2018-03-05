//
//  FitzSlider.h
//  FitzStudy
//
//  Created by Yvonne on 7/29/11.
//  Copyright 2011 INRIA AVIZ. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TTRangeSlider.h"

@class MetaDataReader;

@interface BarSlider : UIControl {
    UIImageView *thumb;
    UIView      *thumbSuperView;
    UIView      *trackView;
    CGRect trackRect;
    float currentValue;
    UIView *outlineView;
    NSDate *timer;
    double time;
    MetaDataReader *metadata;
}

@property(nonatomic,readwrite,retain) UIImageView *thumb;
@property(nonatomic,readwrite,retain) UIView  *thumbSuperView, *trackView;

//- (float) floatValue;
- (id) initWithFrame:(CGRect)frame metadata:(MetaDataReader*)meta;
- (void) setFloatValue:(float)aFloat;
- (float) getNumber;
- (float) getMaxAxis;

@end
