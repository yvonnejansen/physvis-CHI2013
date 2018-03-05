//
//  NumberPickerViewController.h
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 3/19/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface NumberPickerViewController : UIViewController <UIPickerViewDataSource, UIPickerViewDelegate>
{
    IBOutlet UIPickerView* firstDigit, *secondDigit, *thirdDigit;
    NSArray *numbers;
    int digit1, digit2, digit3, precision, width, height, xOff, yOff, distance;
    float number;
}

@property(nonatomic, retain, readwrite) UIPickerView *firstDigit, *secondDigit, *thirdDigit;

- (float) getCurrentNumber;

@end
