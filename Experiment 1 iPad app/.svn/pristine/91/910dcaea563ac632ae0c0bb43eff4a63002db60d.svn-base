//
//  ExperimentStateMachine.h
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 3/22/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Logger.h"

@class TuioSender;
@class RootViewController;
@class ModelController;

@interface ExperimentStateMachine : NSObject <UIAlertViewDelegate>
{
    int userID;
    int block;
    int trial;
    int repetition;
    int question;
    NSString *filename, *recoverLogFilename, *conditionName;
    NSArray *questions;
    NSArray *datasetNames;
    NSArray *conditionOrder;
    NSDictionary *currentCondition;
    int indexOfCurrentDataset;
    int numberOfQuestions;
    NSArray *currentSetOfQuestions;
    TuioSender *tuioSender;
    RootViewController *rootview;
    ModelController *modelController;
    Logger *logger;
    NSDictionary *namesForTrainingDatasets;
    bool trainingMode, trainingAlreadyFinished, advancedToNextBlock, advancedToNextTrial;
}

@property int userID, block, trial, repetition, question, numberOfQuestions;
@property(readwrite,nonatomic,retain) NSString *filename;
@property(assign) bool trainingMode, recover;

- (id) initWithRootViewController:(RootViewController*)viewcontroller;
- (void) getCurrentSetOfQuestions;
- (void) setSubjectGroup:(int)groupID;

@end
