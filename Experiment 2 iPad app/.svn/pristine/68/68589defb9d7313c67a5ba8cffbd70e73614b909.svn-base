//
//  StereoTest.m
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 3/16/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import "StereoTest.h"
#import "QuartzCore/QuartzCore.h"

@implementation StereoTest

@synthesize screen;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        calibrationOnly = false;
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(calibration:) name:@"calibration" object:nil];
        
        self.layer.cornerRadius = 12;
        self.backgroundColor = [UIColor whiteColor];
        self.clipsToBounds = NO;
        text = [[UILabel alloc] initWithFrame:CGRectInset(frame /*CGRectInset(frame, 100, 150)*/, 20, 20)];
        text.backgroundColor = [UIColor clearColor];
        text.lineBreakMode = UILineBreakModeWordWrap;
        text.textAlignment = UITextAlignmentCenter;
        text.numberOfLines = 0;
        text.font = [UIFont systemFontOfSize:24];
        text.text = NSLocalizedStringFromTable(@"StereoTestA", @"Instructions", nil);
        [self addSubview:text];
        
        b1 = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        b2 = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        b3 = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        [b1 setHidden:YES];
        [b2 setHidden:YES];
        [b3 setHidden:YES];
        proceed = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        
        b2.frame = CGRectMake(CGRectGetMidX(frame) - 84, CGRectGetMaxY(frame) + 20, 168, 80);
        b1.frame = CGRectOffset(b2.frame, -200, 0);
        b3.frame = CGRectOffset(b2.frame, 200, 0);
        proceed.frame = b2.frame;
        
        [b1 setTitle:@"In front of the screen" forState:UIControlStateNormal];
        [b2 setTitle:@"Behind the screen" forState:UIControlStateNormal];
        [b3 setTitle:@"Not sure" forState:UIControlStateNormal];
        
        [proceed setTitle:@"Continue" forState:UIControlStateNormal];

        [proceed addTarget:self action:@selector(buttonPressed:) forControlEvents:UIControlEventTouchUpInside];
        [b1 addTarget:self action:@selector(buttonPressed:) forControlEvents:UIControlEventTouchUpInside];
        [b2 addTarget:self action:@selector(buttonPressed:) forControlEvents:UIControlEventTouchUpInside];
        [b3 addTarget:self action:@selector(buttonPressed:) forControlEvents:UIControlEventTouchUpInside];
        
        [self addSubview:b1];
        [self addSubview:b2];
        [self addSubview:b3];
        
        screen = 1;
        [[NSNotificationCenter defaultCenter] postNotificationName:@"stereoTest" object:nil userInfo:[NSDictionary dictionaryWithObject:[NSNumber numberWithInt:screen] forKey:@"screen"]];
        [self addSubview:proceed];
    }
    return self;
}


- (IBAction)buttonPressed:(id)sender
{
    UIButton *button = (UIButton*)sender;
    
    if (sender == proceed && screen == 1) 
    {
        if (calibrationOnly)
            [self removeFromSuperview];
        else
        {
            screen++;
            text.text = NSLocalizedStringFromTable(@"StereoTestB", @"Instructions", nil);
            [[NSNotificationCenter defaultCenter] postNotificationName:@"stereoTest" object:nil userInfo:[NSDictionary dictionaryWithObject:[NSNumber numberWithInt:screen] forKey:@"screen"]];
            [b1 setHidden:NO];
            [b2 setHidden:NO];
            [b3 setHidden:NO];
            [proceed setHidden:YES];
        }
        
    }
    else if (screen == 2)
    {
        screen++;
        if (button == b1)
            answer1 = -1;
        else if (button == b2)
            answer1 = 1;
        else if (button == b3)
            answer1 = 0;
        [[NSNotificationCenter defaultCenter] postNotificationName:@"stereoTest" object:nil userInfo:[NSDictionary dictionaryWithObject:[NSNumber numberWithInt:screen] forKey:@"screen"]];
    }
    else if (screen == 3)
    {
        screen++;
        if (button == b1)
            answer2 = 1;
        else if (button == b2)
            answer2 = -1;
        else if (button == b3)
            answer2 = 0;
       bool answerCorrect = ((answer1 + answer2) == 2);
//        NSLog(@"stereo test %@", answerCorrect ? @"passed" : @"failed");
        text.text = answerCorrect ? NSLocalizedStringFromTable(@"StereoTestP", @"Instructions", nil)
                                            : NSLocalizedStringFromTable(@"StereoTestF", @"Instructions", nil);
        NSDictionary *userInfo = [NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithInt:screen], @"screen", [NSNumber numberWithBool:answerCorrect], @"result", nil];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"stereoTest" object:nil userInfo:userInfo];
        if (answerCorrect)
        {
            [proceed setHidden:NO];
            [b1 setHidden:YES];
            [b2 setHidden:YES];
            [b3 setHidden:YES];
        }
        [self removeFromSuperview];
    }
    else if (screen == 4 && button == proceed) {
//        NSLog(@"posting stop calibration now");
//        [self calibration:nil];
        if (!calibrationOnly)
            [self removeFromSuperview];
        else 
        {
            [self removeFromSuperview];
            [[NSNotificationCenter defaultCenter] postNotificationName:@"calibration" object:nil userInfo:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithInt:screen], @"screen", @"stop", @"action", nil]];
        }
    }
}

- (void) calibration:(NSNotification *)notification
{
//    NSLog(@"calibration dictionary : %@", [notification.userInfo description]);
    NSString *action = [notification.userInfo valueForKey:@"action"];
    if ([action isEqualToString:@"start"])
    {
        text.text = NSLocalizedStringFromTable(@"StereoTestA", @"Instructions", nil);
        [proceed setHidden:NO];
        [b1 setHidden:YES];
        [b2 setHidden:YES];
        [b3 setHidden:YES];
        calibrationOnly = YES;
    }
    else
    {
        [self removeFromSuperview];
//        [[NSNotificationCenter defaultCenter] postNotificationName:@"showNextModel" object:nil];

    }
    
}

- (UIView*)hitTest:(CGPoint)point withEvent:(UIEvent *)event
{
    if (!proceed.hidden && [proceed pointInside:[proceed convertPoint:point fromView:self] withEvent:event])        return proceed;
    else if ([b1 pointInside:[b1 convertPoint:point fromView:self] withEvent:event])
        return b1;
    else if ([b2 pointInside:[b2 convertPoint:point fromView:self] withEvent:event])
        return b2;
    else if ([b3 pointInside:[b3 convertPoint:point fromView:self] withEvent:event])
        return b3;
    else if ([self pointInside:point withEvent:event])
        return self;
    return nil;
}


@end
