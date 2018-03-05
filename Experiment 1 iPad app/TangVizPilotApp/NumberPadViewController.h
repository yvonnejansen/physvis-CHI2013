//
//  NumberPadViewController.h
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 3/20/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface NumberPadViewController : UIViewController
{
    IBOutlet UIButton *b0, *b1, *b2, *b3, *b4, *b5, *b6, *b7, *b8, *b9, *del, *point;
    IBOutlet UITextField *digit1, *digit2, *digit3;
    IBOutlet UILabel *point1, *point2;
    int dig1, dig2, dig3, prec;
}

- (float) getNumber;
- (IBAction)buttonPressed:(id)sender;
- (IBAction)pointPressed:(id)sender;
- (void) setPrecision:(int) _prec;

@end
