//
//  TimeCounter.h
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 6/4/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TimeCounter : UIView
{
    int currentTime;
    NSTimer *timer;
    UILabel *timeLabel;
}

- (IBAction)startCounter:(id)sender;
- (IBAction)stopCounter:(id)sender;
- (void)timerFireMethod:(NSTimer*)theTimer;
@end
