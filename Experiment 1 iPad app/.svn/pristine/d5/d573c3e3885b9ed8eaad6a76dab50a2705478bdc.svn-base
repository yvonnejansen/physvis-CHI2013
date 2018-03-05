//
//  Logger.m
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 2/2/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import "Logger.h"
#import "ExperimentStateMachine.h"

#define NUM_REPETITIONS 3

@implementation Logger

@synthesize state, currentCondition, questionsOfCurrentTrial;

- (id) init
{
    if (self) {
        NSLog(@"init logger");
        log = [[NSMutableDictionary alloc] init];
        userInfo = [[NSMutableDictionary alloc] init];
        currentQuestion = [[NSMutableDictionary alloc] init];
        currentAnswers = [[NSMutableArray alloc] init];
        currentBlock = [[NSMutableDictionary alloc] init];
        questionsOfCurrentTrial = [[NSMutableArray alloc] init];
        repetitions = [[NSMutableArray alloc] init];
        currentTrial = [[NSMutableDictionary alloc] init];
        currentTypeIsRadio = false;
    
    }
    return self;
}

- (id) initWithState:(ExperimentStateMachine*)_state andLog:(NSMutableDictionary*)_log
{
    if (self)
    {
        [self init];
        state = _state;
        log = _log;
//        NSLog(@"log \n %@ \n length: %i", log, [log count]);
//        trialCounter = s tate.trial;
//        blockCounter = state.block;
        if (log != nil)
        {
            userInfo = [log objectForKey:@"userInfo"];
            int blockID = [log count]-1;
            NSString *blockString = [NSString stringWithFormat:@"block %i", blockID];
            currentBlock = [log objectForKey:blockString];
            repetitions = [currentBlock objectForKey:@"repetitions"];
            currentTrial = [repetitions lastObject];
            questionsOfCurrentTrial = [currentTrial objectForKey:@"questions"];
        
        }
        
    }
    return  self;
}

- (void) setUserInfo:(NSDictionary *)user
{
//    NSLog(@"set user info");
    userInfo = [[NSMutableDictionary dictionaryWithDictionary:user] retain];
    [log setObject:userInfo forKey:@"userInfo"];
    userID = [userInfo valueForKey:@"userID"];
}


- (void) startNewBlock:(NSDictionary *)blockInfo
{
    int blockID = [log count];
    NSString *blockString = [NSString stringWithFormat:@"block %i", blockID];
    currentBlock = [[NSMutableDictionary alloc] init];
    repetitions = [[NSMutableArray alloc] init];
    currentCondition = [blockInfo valueForKey:@"name"];
    [currentBlock setObject:[blockInfo retain] forKey:@"condition"];
    [currentBlock setObject:[repetitions retain] forKey:@"repetitions"];
    [log setObject:[currentBlock retain] forKey:blockString];
}

- (void) startNewTrial:(NSDictionary*)datasetInfo
{
    currentTrial = [[NSMutableDictionary alloc] init];
    [currentTrial setObject:[datasetInfo retain] forKey:@"dataset"];
    questionsOfCurrentTrial = [[NSMutableArray alloc] init];
    [currentTrial setObject:[questionsOfCurrentTrial retain] forKey:@"questions"];
    [repetitions addObject:[currentTrial retain]];
//    NSLog(@"start new trial: current trial %@", [currentTrial description]);
}

- (void) startNewQuestion:(NSDictionary*)questionInfo
{
    
    currentQuestion = [[NSMutableDictionary alloc] init];
    currentAnswers = [[NSMutableArray alloc] init];
    [currentQuestion setObject:[questionInfo retain] forKey:@"question"];
    [currentQuestion setObject:[currentAnswers retain] forKey:@"answer"];
    [questionsOfCurrentTrial addObject:[currentQuestion retain]];
    if ([[questionInfo valueForKey:@"type"] isEqualToString:@"radio"])
        currentTypeIsRadio = true;
    else
        currentTypeIsRadio = false;
//    NSLog(@"start new question: current trial %@", [currentTrial description]);
}

- (void) timerStarted:(NSDictionary*)startTime
{
    [currentQuestion setObject:[startTime retain] forKey:@"startTime"];

}

- (void) buttonPressed:(NSDictionary*)buttonPress
{
    [currentAnswers addObject:[buttonPress retain]];
//    NSLog(@"current question %@", [currentQuestion description]);
}

- (void) questionDone:(NSDictionary*)done
{
    [currentAnswers addObject:[done retain]];
}

- (void) experimentFinished
{
    NSArray *dirPaths;
    NSString *docsDir;
    dirPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, 
                                                   NSUserDomainMask, YES);
    docsDir = [dirPaths objectAtIndex:0];
    NSString *filename;
    filename = [[docsDir stringByAppendingPathComponent:[NSString stringWithFormat:@"%@ %@", userID, [[NSDate date] description]]] stringByAppendingPathExtension:@"plist"];
    bool writeOK = [log writeToFile:filename atomically:YES];
    NSLog(@"final writing %@", writeOK ? @"successful" : @"failed");
    
}


- (void)writeLogWithCurrentTimeAndDate
{
    NSString *docsDir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    NSString *filename = [[docsDir stringByAppendingPathComponent:[NSString stringWithFormat:@"%@ %@", userID, [[NSDate date] description]]] stringByAppendingPathExtension:@"plist"];
    NSString *recover = [[docsDir stringByAppendingPathComponent:@"recoverLog"] stringByAppendingPathExtension:@"plist"];
    bool writeOK = [log writeToFile:filename atomically:YES];
//    NSLog(@"writing log for block %i %@", blockCounter, writeOK ? @"successful" : @"failed");
    writeOK = [log writeToFile:recover atomically:YES];
//    NSLog(@"writing recover log for block %i %@", blockCounter, writeOK ? @"successful" : @"failed");
}






@end
