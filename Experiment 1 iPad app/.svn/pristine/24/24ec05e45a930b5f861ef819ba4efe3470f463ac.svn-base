//
//  Logger.h
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 2/2/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import <Foundation/Foundation.h>

@class ExperimentStateMachine;

@interface Logger : NSObject
{
    NSMutableDictionary *log;
    NSMutableDictionary *userInfo;
    NSString *userID;
    NSString *currentCondition;
    NSMutableDictionary *currentQuestion;
    NSMutableArray *currentAnswers;
    NSMutableDictionary *currentBlock;
    NSMutableArray *repetitions;
    NSMutableDictionary *currentTrial;
    NSMutableArray *questionsOfCurrentTrial;
    bool currentTypeIsRadio;
    int trialCounter, blockCounter;
    ExperimentStateMachine *state;
}

@property(nonatomic, readwrite, retain) ExperimentStateMachine *state;
@property(copy, nonatomic) NSString* currentCondition;
@property(readonly)NSMutableArray *questionsOfCurrentTrial;

- (id) initWithState:(ExperimentStateMachine*)_state andLog:(NSMutableDictionary*)_log;
- (void) setUserInfo:(NSDictionary *)user;
- (void) startNewBlock:(NSDictionary *)blockInfo;
- (void) startNewTrial:(NSDictionary*)datasetInfo;
- (void) startNewQuestion:(NSDictionary*)questionInfo;
- (void) timerStarted:(NSDictionary*)startTime;
- (void) buttonPressed:(NSDictionary*)buttonPress;
- (void) questionDone:(NSDictionary*)done;
- (void) experimentFinished;
- (void)writeLogWithCurrentTimeAndDate;

//- (id) initWithState:(ExperimentStateMachine*)_state andLog:(NSMutableArray*)_log;
//- (void) setUserInfo:(NSDictionary *)user;
//- (void)logButtonPress:(NSNotification*)notification;
//- (void)logQuestionDisplayed:(NSNotification *)notification;
//- (void) logButtonsDisplayed:(NSNotification *)notification;
//- (void)logQuestionFinished:(NSNotification *)notification;
//- (void) blockFinished:(NSNotification*)notification;
//- (void) newBlock:(NSNotification*)notification;


@end
