//
//  QuestionView.m
//  Experiment 2
//
//  Created by Yvonne Jansen on 9/3/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import "QuestionView.h"

@implementation QuestionView

@synthesize result, putModelDown;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor whiteColor];
        result = [[UILabel alloc] initWithFrame:CGRectMake(10, 20, 500, 40)];
        result.font = [UIFont systemFontOfSize:36];
        result.center = CGPointMake(frame.size.width/2, result.center.y);
        result.text = @" ";
        [self addSubview:result];
        
        putModelDown = [[UILabel alloc] initWithFrame:CGRectMake(10, 100, 500, 40)];
        putModelDown.font = [UIFont systemFontOfSize:24];
        putModelDown.textAlignment = UITextAlignmentCenter;
        putModelDown.center = CGPointMake(frame.size.width/2, putModelDown.center.y);
        putModelDown.text = @" ";
        [self addSubview:putModelDown];
        
        ratingLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 150, 500, 150)];
        ratingLabel.numberOfLines = 0;
        ratingLabel.font = [UIFont systemFontOfSize:24];
        ratingLabel.backgroundColor = [UIColor clearColor];
        ratingLabel.text = @"Answering this question was";
        [self addSubview:ratingLabel];
        
        ratingSegments = [[UISegmentedControl alloc] initWithItems:[NSArray arrayWithObjects:@"1", @"2", @"3", @"4", @"5", nil]];
        ratingSegments.frame = CGRectMake(100, 250, 400, 50);
        [ratingSegments setSelectedSegmentIndex:-1];
        [self addSubview:ratingSegments];
        UILabel *easyLabel = [[UILabel alloc] initWithFrame:CGRectMake(110, 300, 80, 30)];
        UILabel *diffLabel = [[UILabel alloc] initWithFrame:CGRectMake(420, 300, 120, 30)];
        easyLabel.text = @"very easy";
        easyLabel.backgroundColor = [UIColor clearColor];
        diffLabel.text = @"very difficult";
        diffLabel.backgroundColor = [UIColor clearColor];
        [self addSubview:easyLabel];
        [self addSubview:diffLabel];
        
        
        timeLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 450, 500, 50)];
        timeLabel.numberOfLines = 0;
        timeLabel. font = [UIFont systemFontOfSize:24];
        timeLabel.backgroundColor = [UIColor clearColor];
        timeLabel.text = @"Answering this question took me about";
        [self addSubview:timeLabel];
        
        timeEstimationSlider = [[UISlider alloc] initWithFrame:CGRectMake(10, 500, 600, 50)];
        timeEstimationSlider.value = 0;
        timeEstimationSlider.minimumValue = 0;
        timeEstimationSlider.maximumValue = 120;
        [self addSubview:timeEstimationSlider];
        
        sliderValueLabel = [[UILabel alloc] initWithFrame:CGRectMake(460, 450, 80, 50)];
        sliderValueLabel.font = [UIFont systemFontOfSize:24];
        sliderValueLabel.backgroundColor = [UIColor clearColor];
        sliderValueLabel.text = @"0 s";
        sliderValueLabel.textAlignment = UITextAlignmentRight;
        [self addSubview:sliderValueLabel];
        
        [timeEstimationSlider addTarget:self action:@selector(sliderValueChanged:) forControlEvents:UIControlEventValueChanged];
        
        closeButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        closeButton.frame = CGRectMake(0, 0, 150, 50);
        closeButton.center = CGPointMake(320, 600);
        [closeButton setTitle:@"Continue" forState:UIControlStateNormal];
        [closeButton setFont:[UIFont systemFontOfSize:24]];
        [closeButton addTarget:self action:@selector(finishedAdditionalQuestions:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:closeButton];
        
        timeset = NO;
    }
    return self;
}

- (void) sliderValueChanged:(UISlider *)sender
{
    timeset = YES;
    sliderValueLabel.text = [NSString stringWithFormat:@"%i s", [[NSNumber numberWithFloat:[sender value]] intValue]];
}

- (NSDictionary*) getValues
{
    if ([ratingSegments selectedSegmentIndex] > -1)
        return [NSDictionary dictionaryWithObjectsAndKeys:sliderValueLabel.text, @"perceivedTime", [ratingSegments titleForSegmentAtIndex:[ratingSegments selectedSegmentIndex]], @"difficulty", nil];
    else {
        NSLog(@"Error: no rating selected, index %i", [ratingSegments selectedSegmentIndex]);
        return nil;
    }
}

- (void) finishedAdditionalQuestions:(id)sender
{
    if ([ratingSegments selectedSegmentIndex] > -1)
    {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"additionalQuestionsFinished" object:nil userInfo:[[self getValues] retain]];
        UIView *superview = self.superview;
        [self removeFromSuperview];
    }
    else if (!timeset)
    {
        NSLog(@"Error: no time set, index %i", [ratingSegments selectedSegmentIndex]);
        UIAlertView *warning = [[UIAlertView alloc] initWithTitle:@"Incomplete" message:@"Please set the time you estimate for answering the question." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
        [warning show];
        [warning release];

    }
    else {
        NSLog(@"Error: no rating selected, index %i", [ratingSegments selectedSegmentIndex]);
        UIAlertView *warning = [[UIAlertView alloc] initWithTitle:@"Incomplete" message:@"Please rate the difficulty before proceeding" delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
        [warning show];
        [warning release];
    }
//    [superview removeFromSuperview];
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
