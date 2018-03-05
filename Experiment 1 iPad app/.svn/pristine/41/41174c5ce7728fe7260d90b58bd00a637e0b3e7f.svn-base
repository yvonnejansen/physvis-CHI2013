//
//  RootViewController.h
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 1/26/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Logger.h"
#import "InstructionView.h"

@class StereoTest;
@class TuioSender;
@class ExperimentStateMachine;
@class ModelController;
@class DataViewController;

@interface RootViewController : UIViewController <UIPageViewControllerDelegate, UIAlertViewDelegate>
{
    Logger *logger;
    int pageCounter;
    IBOutlet UIView *userInfoView;
    IBOutlet UILabel *userID;
    IBOutlet UIStepper *stepper;
    IBOutlet UISegmentedControl* groupPicker;
    TuioSender *tuioSender;
    UIAlertView *startExperiment, *experimentFinished, *blockFinished, *trialFinished, *questionFinished, *recover, *trainingAlert;
    int block;
    int trial;
    int currentCondition;
    int indexOfCurrentDataset;
    NSArray *conditionOrder;
    InstructionView *instructionView, *datasetInfoView;
    bool finished;
    NSArray *datasetNames;
    bool showInstructions;
    StereoTest *stereoTest;
    ExperimentStateMachine *state;
    ModelController *_modelController;
    UIView *trainingFrame;
    bool training;
    NSString *currentConditionName;

}
@property (strong, nonatomic) UIView *trainingFrame;
@property (strong, nonatomic) UIPageViewController *pageViewController;
@property (strong, nonatomic) TuioSender *tuioSender;
@property (strong, nonatomic) ExperimentStateMachine *state;
@property (readonly, strong, nonatomic) ModelController *modelController;
@property (assign) DataViewController *currentDataViewController;
@property int pageCounter;
@property (strong, nonatomic) UIView* userInfoView;
@property (copy) NSString *currentConditionName;

//- (void) showNextModel:(NSNotification *)notification;
- (void) startStereoTest:(NSNotification*)notification;
- (void) showInstructions:(NSString *)conditionName;
- (DataViewController*) startTrial;
- (DataViewController*) nextView;
- (void)setLogger:(Logger*)_logger;
- (DataViewController*) goToPage:(int)_page;
- (void) finishExperiment;
- (void) showDatasetInfo:(NSString*)datasetInfo;
- (void) recover;
- (void) setTraining:(bool)t;
- (bool) isTraining;
- (void) showModalityInstructions:(NSString*)modality;
- (void) showTrainingInstructions;



@end
