//
//  FitzSlider.m
//  FitzStudy
//
//  Created by Yvonne on 7/29/11.
//  Copyright 2011 INRIA AVIZ. All rights reserved.
//

#import "BarSlider.h"
#import "TuioSender.h"
#import "QuartzCore/CALayer.h"
#import "MetaDataReader.h"


#define OUTLINE_OFFSET_X -30
#define OUTLINE_OFFSET_Y -15
#define THUMBWIDTH 68

@implementation BarSlider

@synthesize thumb, thumbSuperView, trackView;

- (id) initWithFrame:(CGRect)frame metadata:(MetaDataReader*)meta
{
    self = [super initWithFrame:frame];
    if (self)
    {
        [self setAlpha:0.4];
        self.layer.cornerRadius = 6;
        UIView *scaleLine = [[UIView alloc] initWithFrame:CGRectMake(-1, 0, frame.size.width + 2, 2)];
        scaleLine.backgroundColor = [UIColor blackColor];
        [self addSubview:scaleLine];
        self.clipsToBounds = NO;
        self.multipleTouchEnabled = YES;
        self.thumb = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 50, 100)];
        self.thumb.image = [UIImage imageNamed:@"black arrow"];
        [self.thumb setAlpha:0.95];
        self.thumb.clipsToBounds = NO;
        [self addSubview:thumb];
        
//        NSLog(@"creating metadata for %@", name);
//        if (error) NSLog(@"%@", error);
        metadata = meta;
        [self drawLines:metadata.ticks];
        [self setFloatValue:0];
     }
    
    return self;
}

- (void) didMoveToSuperview
{
    timer = [[NSDate date] retain];
}

- (void) touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    UITouch *touch = [touches anyObject];
    time = -[timer timeIntervalSinceNow];
    float newX = MIN(MAX(self.bounds.origin.x, [touch locationInView:self].x), self.bounds.size.width);
    [self setFloatValue: (newX  / self.bounds.size.width)];

    [super touchesBegan:touches withEvent:event];

}

- (void) touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
    for (UITouch *touch in touches)
    {
        float newX = MIN(MAX(self.bounds.origin.x, [touch locationInView:self].x), self.bounds.size.width);
        [self setFloatValue: (newX  / self.bounds.size.width)];
    }

    [super touchesMoved:touches withEvent:event];
    
}

- (void) touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    [super touchesEnded:touches withEvent:event];

}


- (void) setFloatValue:(float)aFloat
{
    currentValue = aFloat;
    self.thumb.center = CGPointMake(aFloat * self.bounds.size.width, self.thumb.center.y);
}


- (UIView*) hitTest:(CGPoint)point withEvent:(UIEvent *)event
{
    CGPoint p = [self convertPoint:point toView:self];
    CGPoint pT = [self convertPoint:point toView:self.thumb];
    CGRect th = self.thumb.bounds; //[self convertRect:self.thumb.bounds toView:self];
    if (CGRectContainsPoint(self.bounds, p) || CGRectContainsPoint(th, pT)) 
    {
        return self;
    }
    return nil;
        
}

- (void) drawLines:(NSArray*)ticks
{
    UIView *lineView = [[UIView alloc] initWithFrame:self.bounds];
    float height = self.bounds.size.width;
    lineView.clipsToBounds = NO;
    int lineCount = ticks.count;
    for (int i = 0; i < lineCount; i++) {
        UIView *line = [[UIView alloc] initWithFrame:CGRectMake(height - i*(height/(lineCount-1)) - 1, 0 - self.bounds.size.height+35, 2, self.bounds.size.height-35)];
        line.clipsToBounds = NO;
        line.backgroundColor = [UIColor blackColor];
        [lineView addSubview:line];
        CGRect labelFrame = CGRectOffset(line.frame, -40, 0);
        labelFrame = CGRectInset(labelFrame, -30, -5);
        UILabel *label = [[UILabel alloc] initWithFrame:labelFrame];
        label.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleHeight;
        label.textAlignment = UITextAlignmentRight;
        label.text = [ticks objectAtIndex:lineCount - i - 1];
        label.transform = CGAffineTransformMakeRotation(M_PI_2);
        label.clipsToBounds = NO;
        [label setNeedsLayout];
        label.center = CGPointMake(line.center.x, line.center.y-50);
        [lineView addSubview:label];
    }
    [self addSubview:lineView];
}

- (float) getNumber
{
    return currentValue * metadata.maxAxisValue;
}

- (float) getMaxAxis
{
    return metadata.maxAxisValue;
}

@end
