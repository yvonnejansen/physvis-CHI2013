//
//  TTRangeSlider.m
//  TuioPad
//
//  Created by Yvonne Jansen on 4/4/11.
//  Copyright 2011 INRIA AVIZ. All rights reserved.
//

#import "TTRangeSlider.h"
#import <QuartzCore/QuartzCore.h>


@interface TTRangeSlider ()

- (void)calculateMinMax;
- (void)setupSliders;
- (void) updateTrackImageViews;
- (void) updateThumbViews;
- (void) setMin:(CGFloat)minPos;
- (void) setMax:(CGFloat)maxPos;

@end

@implementation TTRangeSlider

@synthesize minimumRangeLength, distributionView, minPos, maxPos, leftTouchCoord, rightTouchCoord, leftCap, rightCap, backgroundImageView;
#pragma mark TODO: make view arrangement independent from outer views bounds -> add superview to prevent additional sliders being created in direct vicinity
#pragma mark -

#pragma mark init
- (id)initWithFrame:(CGRect)frame {
    if ((self = [super initWithFrame:frame]))//CGRectMake(frame.origin.x, frame.origin.y, frame.size.width, SLIDER_HEIGHT)])) {
    {
        self.clipsToBounds = NO;
        self.multipleTouchEnabled = YES;
        self.layer.borderColor = [UIColor clearColor].CGColor;
        self.layer.borderWidth = 40;
        
		
        // default values
		self.minPos = 0.0;
		self.maxPos = 1.0;
		minimumRangeLength = 0.0;
        minActive = NO;
        maxActive = NO;
//        self.backgroundImageView = [[UIView alloc] initWithFrame:frame];
		
  //      self.layer.anchorPoint = [self convertPoint:backgroundImageView.center toView:self];
        
		trackImageView = [[UIImageView alloc] initWithFrame:CGRectMake(X, Y, SLIDER_WIDTH, SLIDER_HEIGHT)];
		trackImageView.contentMode = UIViewContentModeScaleToFill;
        trackImageView.userInteractionEnabled = YES;
        trackImageView.multipleTouchEnabled = YES;
        trackImageView.layer.borderColor = [UIColor clearColor].CGColor;
        trackImageView.layer.borderWidth = 10;
		
		inRangeTrackImageView = [[UIImageView alloc] initWithFrame:CGRectMake(self.minPos*SLIDER_WIDTH, 0, (self.maxPos-self.minPos)*SLIDER_WIDTH, SLIDER_HEIGHT)];
		inRangeTrackImageView.contentMode = UIViewContentModeScaleToFill;
        
        distributionView = [[UIImageView alloc] initWithFrame:CGRectMake(X, 65, SLIDER_WIDTH, SLIDER_HEIGHT)];
        [distributionView.image stretchableImageWithLeftCapWidth:0 topCapHeight:0];
        //distributionView.contentMode = UIViewContentModeScaleAspectFit;
        distributionView.backgroundColor = [UIColor clearColor];
        distributionView.userInteractionEnabled = YES;
//        [distributionView.layer setBorderColor:[UIColor blackColor].CGColor];
//        [distributionView.layer setBorderWidth:1];
//        
       
		[self addSubview:trackImageView];
		[self addSubview:inRangeTrackImageView];
		[self addSubview:distributionView];
        UISwipeGestureRecognizer *removeRecognizer = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handleRightSwipe:)];
        removeRecognizer.direction = UISwipeGestureRecognizerDirectionRight;
        [self.distributionView addGestureRecognizer:removeRecognizer];
        
        UISwipeGestureRecognizer *remapRecognizer = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handleDownSwipe:)];
        remapRecognizer.direction = UISwipeGestureRecognizerDirectionDown;
        [self.distributionView addGestureRecognizer:remapRecognizer];
 		[self setupSliders];
        
		[self updateTrackImageViews];
		
	}
    return self;
}


- (void)setupSliders {
	
	minSlider = [[UIImageView alloc] initWithFrame:CGRectMake(X, Y, SLIDER_HEIGHT, SLIDER_HEIGHT)];
	minSlider.backgroundColor = [UIColor whiteColor];
	minSlider.contentMode = UIViewContentModeScaleToFill;
	
	maxSlider = [[UIImageView alloc] initWithFrame:CGRectMake(self.maxPos*(SLIDER_WIDTH), Y, SLIDER_HEIGHT, SLIDER_HEIGHT)];
	maxSlider.backgroundColor = [UIColor whiteColor];
	maxSlider.contentMode = UIViewContentModeScaleToFill;
	
	[self addSubview:minSlider];
	[self addSubview:maxSlider];
    
    [self setMinThumbImage:[UIImage imageNamed:@"sliderControl.png"]]; // the two thumb controls are given custom images
    [self setMaxThumbImage:[UIImage imageNamed:@"sliderControl.png"]];
    
    
    
    UIImage *image; // there are three track images, one for the region to the left of the minimum, one for the region within the range, and one for the region to the right of the maximum
    
    image = [UIImage imageNamed:@"subRangeTrack.png"];
    image = [image stretchableImageWithLeftCapWidth:image.size.width-2 topCapHeight:3];
    [self setTrackImage:image];
    
    image = [UIImage imageNamed:@"inRangeTrack.png"];
    [self setInRangeTrackImage:image];

	
}

- (void)setMinThumbImage:(UIImage *)image {
	minSlider.backgroundColor = [UIColor clearColor];
	minSlider.image = image;	
}

- (void)setMaxThumbImage:(UIImage *)image {
	maxSlider.backgroundColor = [UIColor clearColor];
	maxSlider.image = image;	
}

- (void)setInRangeTrackImage:(UIImage *)image {
//	trackImageView.frame = CGRectMake(inRangeTrackImageView.frame.origin.x,minSlider.frame.origin.y, inRangeTrackImageView.frame.size.width, trackImageView.frame.size.height);
	inRangeTrackImageView.image = [image stretchableImageWithLeftCapWidth:image.size.width/2.0-2 topCapHeight:0];
	
}

- (void)setTrackImage:(UIImage *)image {
	trackImageView.bounds = CGRectMake(X+10, MAX((SLIDER_HEIGHT-image.size.height)/2.0,0), SLIDER_WIDTH, MIN(SLIDER_HEIGHT,image.size.height));
	trackImageView.image = image;
//	inRangeTrackImageView.frame = CGRectMake(inRangeTrackImageView.frame.origin.x,trackImageView.frame.origin.y,inRangeTrackImageView.frame.size.width,trackImageView.frame.size.height);
}
float getPointDistance(CGPoint pt0, CGPoint pt1)
{
	CGPoint d = CGPointMake(pt0.x - pt1.x, pt0.y - pt1.y);
	return sqrt(d.x*d.x + d.y*d.y);
}

#pragma  mark -
#pragma mark touch callbacks
// TODO: make callbacks independent from frame bounds 
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
	[super touchesBegan:touches withEvent:event];
	printf("num touches began in slider %i\n", [[event allTouches] count]);
    CGPoint leftThumbTouch = self.leftTouchCoord;
    CGPoint rightThumbTouch = self.rightTouchCoord;
    bool foundLeft = NO, foundRight = NO;
    CGPoint curTouch;
    for (UITouch *touch in touches) 
    {
        curTouch = [touch locationInView:self];
        if (CGRectContainsPoint(trackImageView.bounds, curTouch))
        {
            if (!foundLeft && getPointDistance(self.leftTouchCoord, curTouch) < getPointDistance(self.leftTouchCoord, minSlider.center))
            {
                leftThumbTouch = curTouch;
                leftThumbRegisteredTouch = touch;
                foundLeft = YES;
            }
            else if (!foundRight && getPointDistance(self.leftTouchCoord, maxSlider.center) < getPointDistance(self.leftTouchCoord, curTouch))
            {
                rightThumbTouch = curTouch;
                rightThumbRegisteredTouch = touch;
                foundRight = YES;
            }
            else if (!foundLeft && getPointDistance(minSlider.center, curTouch) < getPointDistance(maxSlider.center, curTouch))
            {
                leftThumbTouch = curTouch;
                leftThumbRegisteredTouch = touch;
               foundLeft = YES;
            }
            else if (!foundRight && getPointDistance(maxSlider.center, curTouch) < getPointDistance(minSlider.center, curTouch))
            {
                rightThumbTouch = curTouch;
                rightThumbRegisteredTouch = touch;
                foundRight = YES;
            }
        }
    }
    
    //    }
	for (UITouch *touch in touches)
    {
        if (foundLeft) {
            minSlider.center = (leftThumbTouch.x < maxSlider.center.x) ? CGPointMake(leftThumbTouch.x, minSlider.center.y) : CGPointMake(maxSlider.center.x-30, minSlider.center.y);
        }
        if (foundRight) {
            maxSlider.center = (rightThumbTouch.x > minSlider.center.y) ? CGPointMake(rightThumbTouch.x, maxSlider.center.y) : CGPointMake(minSlider.center.x + 30, maxSlider.center.y);
        }
    }
    [self calculateMinMax];
	[self updateTrackImageViews];
}



    
    
    
    
    //	UITouch *touch = [touches anyObject];
//	for (UITouch *touch in touches)
//    {
////     printf("offset: %f, trackimage frame width: %f, converted point: %f\n", MAX(30,[minSlider convertPoint:minSlider.frame.origin toView:self].x), self.frame.size.width, [minSlider convertPoint:minSlider.frame.origin toView:self].x);
//        //TODO: figure out correct coordinates to get the first part of the trackimage where a finger could be to reset the thumb
//        if (CGRectContainsPoint(/*minSlider.frame*/CGRectMake(self.bounds.origin.x, self.bounds.origin.y, MAX(30,[minSlider convertPoint:minSlider.frame.origin toView:self].x + 20), 30), [touch locationInView:self])) { //if touch is beginning on minPos slider
//            trackingSlider = minSlider;
//            minActive = YES;
//            printf("left thumb tap \n");
////            minTouchID = [touch 
//        } else if (CGRectContainsPoint(/*maxSlider.frame*/CGRectMake(maxSlider.frame.origin.x - 20, maxSlider.frame.origin.y, self.frame.size.width-maxSlider.frame.origin.x + 20, 30), [touch locationInView:self])) { //if touch is beginning on maxPos slider
//            trackingSlider = maxSlider;
//            maxActive = YES;
//            printf("right thumb tab \n");
//        }
//    }
//CGRectMake(trackImageView.frame.origin.x, trackImageView.frame.origin.y, trackImageView.frame.size.width-[minSlider.frame, locationInView:trackImageView].x, 30)


//CGPoint findClosestPointInSet(CGPoint origin, NSSet* theSet)
//{
//    CGPoint closest = CGPointMake(CGFLOAT_MAX, CGFLOAT_MAX);
//    for (UITouch* touch in theSet)
//    {
//        closest = (getPointDistance(origin, closest) > getPointDistance(origin, [touch locationInView:self])) ? [touch locationInView:this] : closest;
//    }
//    return closest;
//}



- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
	[super touchesMoved:touches withEvent:event];
    {	
//	UITouch *touch = [touches anyObject];
    
    // first check if we have more than one touch and assign them to the correct thumbs
//    if ([touches count] == 2)
//    {
//        NSArray *touchArray = [touches sortedArrayUsingDescriptors:nil];
//        float deltaX = [touch locationInView:self].x - [touch previousLocationInView:self].x;
//        
//        CGPoint origin = [self convertPoint:self.bounds.origin toView:self.superview];
//        if (/*minActive &&*/ CGRectContainsPoint(/*minSlider.frame*/CGRectInset(minSlider.bounds, -20, -20), [touch previousLocationInView:self])) {
//            float newX = MAX(
//                             0,
//                             MIN(
//                                 minSlider.center.x+deltaX,
//                                 self.bounds.size.width-self.bounds.size.height*2.0-minimumRangeLength*(self.bounds.size.width-self.bounds.size.height*2.0))
//                             );
//            
//            minSlider.center = CGPointMake(
//                                         newX, minSlider.center.y);
//            
//            maxSlider.center = CGPointMake(
//                                         MAX(
//                                             maxSlider.center.x,
//                                             minSlider.center.x+self.bounds.size.height+minimumRangeLength*(self.bounds.size.width-self.bounds.size.height*2.0)
//                                             ), 
//                                         maxSlider.center.y);
//            
//        } else if (/*maxActive  &&*/ CGRectContainsPoint(/*maxSlider.frame*/CGRectMake(maxSlider.center.x-30, maxSlider.center.y-30, 50, 50), [touch previousLocationInView:self])) {
//            float newX = MAX(
//                             30+minimumRangeLength*(self.bounds.size.width-self.bounds.size.height*2.0),
//                             MIN(
//                                 [touch locationInView:self].x,           /*maxSlider.frame.origin.x+deltaX,*/
//                                 self.bounds.size.width-self.bounds.size.height)
//                             );
//            
//            maxSlider.center = CGPointMake(newX, maxSlider.center.y);
//            
//            minSlider.center = CGPointMake(
//                                         MIN(
//                                             minSlider.center.x,
//                                             maxSlider.center.x-self.bounds.size.height-minimumRangeLength*(self.bounds.size.width-2.0*self.bounds.size.height)
//                                             ), 
//                                         minSlider.center.y);
//        }
//	}
	}
    for (UITouch *touch in touches)
    {
        if (touch == leftThumbRegisteredTouch) {
            minSlider.center = CGPointMake(MIN(MAX([touch locationInView:self].x, trackImageView.bounds.origin.x), maxSlider.center.x -30), minSlider.center.y);
        }
        else if (touch == rightThumbRegisteredTouch) {
            maxSlider.center = CGPointMake(MAX(MIN([touch locationInView:self].x, SLIDER_WIDTH), minSlider.center.x + 30), maxSlider.center.y);

        }
    }
        
    [self calculateMinMax];
	[self updateTrackImageViews];
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
	[super touchesEnded:touches withEvent:event];
    for (UITouch *touch in touches)
    {
        if (touch == leftThumbRegisteredTouch) leftThumbRegisteredTouch = nil;
        if (touch == rightThumbRegisteredTouch) rightThumbRegisteredTouch = nil;
        
        //        if (CGRectContainsPoint(minSlider.frame, [touch locationInView:self])) { //if touch is beginning on minPos slider
//            //            trackingSlider = minSlider;
//            minActive = NO;
//            minTouchID = -1;
//        }
//        else if (CGRectContainsPoint(maxSlider.frame, [touch locationInView:self])) { //if touch is beginning on minPos slider
//            //            trackingSlider = minSlider;
//            maxActive = NO;
//            maxTouchID = -1;
//        }
    }
    
	[self sendActionsForControlEvents:UIControlEventTouchUpInside];
}

- (UIView*)hitTest:(CGPoint)point withEvent:(UIEvent *)event
{
//    CGRect mappingBounds = [self conv
    if (CGRectContainsPoint(trackImageView.bounds, point)) return self;

    return nil;
}



- (void)updateTrackImageViews {
    
//	inRangeTrackImageView.frame = CGRectMake(minSlider.frame.origin.x+0.5*self.frame.size.height,
//											 inRangeTrackImageView.frame.origin.y,
//											 maxSlider.frame.origin.x-minSlider.frame.origin.x,
//											 inRangeTrackImageView.frame.size.height);
    
	inRangeTrackImageView.bounds = CGRectMake(minSlider.center.x,
											 minSlider.center.y,
                                              getPointDistance(maxSlider.center, minSlider.center), SLIDER_HEIGHT);
    
    inRangeTrackImageView.center = CGPointMake(minSlider.center.x + (maxSlider.center.x - minSlider.center.x) / 2, minSlider.center.y + (maxSlider.center.y - minSlider.center.y) / 2);
}


#pragma mark -
#pragma mark update methods after control was changed
- (void)setMin:(CGFloat)newMin {
	self.minPos = MIN(1.0,MAX(0,newMin)); //value must be between 0 and 1
	[self updateThumbViews];
	[self updateTrackImageViews];
}

- (void)setMax:(CGFloat)newMax {
	self.maxPos = MIN(1.0,MAX(0,newMax)); //value must be between 0 and 1
	[self updateThumbViews];
	[self updateTrackImageViews];
}

- (void)calculateMinMax {
	float newMax = MIN(1,(maxSlider.center.x - SLIDER_HEIGHT)/(SLIDER_WIDTH-(2*SLIDER_HEIGHT)));
	float newMin = MAX(0,minSlider.center.x/(SLIDER_WIDTH-2.0*SLIDER_HEIGHT));
	
	if (newMin != self.minPos || newMax != self.maxPos) {
        
		self.minPos = newMin;
		self.maxPos = newMax;
//		[self sendActionsForControlEvents:UIControlEventValueChanged];
        
	}
    
}

- (void)setMinimumRangeLength:(CGFloat)length {
	minimumRangeLength = MIN(1.0,MAX(length,0.0)); //length must be between 0 and 1
	[self updateThumbViews];
	[self updateTrackImageViews];
}

- (void)updateThumbViews {
	
//	maxSlider.bounds = CGRectMake(maxPos*(SLIDER_WIDTH-2*SLIDER_HEIGHT)+SLIDER_HEIGHT, 
//								 (SLIDER_HEIGHT-self.bounds.size.height)/2.0, 
//								 SLIDER_HEIGHT, 
//								 SLIDER_HEIGHT);
	
//	minSlider.frame = CGRectMake(MIN(
//									 minPos*(self.bounds.size.width-2*self.bounds.size.height),
//									 maxSlider.center.x-self.bounds.size.height-(minimumRangeLength*(self.bounds.size.width-self.bounds.size.height*2.0))
//									 ), 
//								 (SLIDER_HEIGHT-self.bounds.size.height)/2.0, 
//								 self.bounds.size.height, 
//								 self.bounds.size.height);
	
}


/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect {
 // Drawing code
 }
 */

- (void)dealloc {
	[minSlider release];
	[maxSlider release];
    [super dealloc];
}




@end
