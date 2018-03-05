//
//  TimeCounter.m
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 6/4/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import "TimeCounter.h"
#import "QuartzCore/QuartzCore.h"

@implementation TimeCounter

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        currentTime = 0;
        timer = [[NSTimer timerWithTimeInterval:1 target:self selector:@selector(timerFireMethod:) userInfo:nil repeats:YES] retain];
        timeLabel = [[UILabel alloc] initWithFrame:frame];
        timeLabel.font = [UIFont systemFontOfSize:40];
        timeLabel.textColor = [UIColor blueColor];
//        timeLabel.layer.borderColor = [UIColor blueColor].CGColor;
//        timeLabel.layer.borderWidth = 2;
//        self.backgroundColor = [UIColor yellowColor];
        timeLabel.textAlignment = UITextAlignmentRight;
        timeLabel.text = @"0:00";
        [self addSubview:timeLabel];
//        NSLog(@"time counter created");
    }
    return self;
}

- (IBAction)startCounter:(id)sender
{
    [[NSRunLoop mainRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
//    NSLog(@"timer added to runloop");
}

- (IBAction)stopCounter:(id)sender
{
    [timer invalidate];
//    NSLog(@"timer stopped");
}

- (NSString *)timeFormatted:(int)totalSeconds
{
    
    int seconds = totalSeconds % 60; 
    int minutes = (totalSeconds / 60) % 60; 
    
    return [NSString stringWithFormat:@"%02d:%02d", minutes, seconds]; 
}

- (void)timerFireMethod:(NSTimer*)theTimer
{
    currentTime++;
    timeLabel.text = [self timeFormatted:currentTime]; 
}


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
