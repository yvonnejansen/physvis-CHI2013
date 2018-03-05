//
//  StereoTest.h
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 3/16/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface StereoTest : UIView
{
    IBOutlet UILabel* text;
    IBOutlet UIButton* b1;
    IBOutlet UIButton* b2;
    IBOutlet UIButton* b3;
    IBOutlet UIButton* proceed;
    bool calibrationOnly;
    int screen;
    int answer1, answer2;
}

@property int screen;
@end
