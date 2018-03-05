//
//  DataViewController.h
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 1/26/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NumberPadViewController.h"
#import "BarSlider.h"
#import "Logger.h"
#import "TimeCounter.h"

@class MetaDataReader, QuestionView;

@interface DataViewController : UIViewController <UIAlertViewDelegate>
{
    NSArray *answers, *buttons;
    NSDate *startTime; 
    Logger *logger;
    int rankCounter, questionNumber;
    bool rank;
    UILabel *min, *max;
    NSString *datasetName, *correctAnswer, *answer;
    NSMutableArray *rankedButtons;
    MetaDataReader *metadata;
    TimeCounter *timer;
    UIImageView* visImage;
    bool training;
    int indexOfRadioButton;
    QuestionView *additionalQuestions;
}
@property (strong, nonatomic) IBOutlet UILabel *dataLabel;
@property (strong, nonatomic) IBOutlet UILabel *reminderLabel, *reminderLabel2;
@property (strong, nonatomic) id dataObject;
@property (strong, nonatomic) IBOutlet UIButton *a1;
@property (strong, nonatomic) IBOutlet UIButton *a2;
@property (strong, nonatomic) IBOutlet UIButton *a3;
@property (strong, nonatomic) IBOutlet UIButton *a4;
@property (strong, nonatomic) IBOutlet UIButton *a5;
@property (strong, nonatomic) IBOutlet UIButton *a6;
@property (strong, nonatomic) IBOutlet UIButton *a7;
@property (strong, nonatomic) IBOutlet UIButton *a8;
@property (strong, nonatomic) IBOutlet UIButton *a9;
@property (strong, nonatomic) IBOutlet UIButton *a10;
@property (strong, nonatomic) IBOutlet UIButton *done, *calibrate;
@property (strong, nonatomic) NumberPadViewController *numViewC;
@property (strong, nonatomic) BarSlider *slider, *slider2;
@property (strong, nonatomic) NSString *datasetName;
@property (assign) Logger *logger;
@property (assign) int questionNumber;


- (IBAction)answer:(id)sender;
- (IBAction)showButtons:(id)sender;
- (IBAction)sliderUp:(id)sender;
- (IBAction)sliderDown:(id)sender;
- (void)registerLogger:(Logger*) _logger;
- (void) setTraining:(bool)t;
- (bool) isTraining;
- (void) showVisualizationImageForCondition:(NSString*)condition;
- (void) initEverything;


@end
