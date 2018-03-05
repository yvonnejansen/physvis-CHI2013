//
//  QuestionView.h
//  Experiment 2
//
//  Created by Yvonne Jansen on 9/3/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QuestionView : UIView
{
    UILabel *title, *ratingLabel, *timeLabel, *sliderValueLabel, *result, *putModelDown;
    UISlider *timeEstimationSlider;
    UISegmentedControl *ratingSegments;
    UIButton *closeButton;
    bool timeset;
}

@property (strong, nonatomic) IBOutlet UILabel *result, *putModelDown;


- (void) sliderValueChanged:(UISlider*)sender;
- (NSDictionary*) getValues;

@end
