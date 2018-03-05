//
//  ExperimentStateMachine.m
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 3/22/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import "ExperimentStateMachine.h"
#import "TuioSender.h"
#import "RootViewController.h"
#import "DataViewController.h"
#import "ModelController.h"
#import "QuartzCore/QuartzCore.h"

#define NUM_BLOCKS 4
#define NUM_REPETITIONS 3 // there's another NUM_REPETITIONS in rootviewcontroller class

@implementation ExperimentStateMachine

@synthesize userID, block, trial, repetition, question, filename, numberOfQuestions, trainingMode, recover;

- (id) initWithRootViewController:(RootViewController*)viewcontroller
{
    if (self)
    {

        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(stereoTest:) name:@"stereoTest" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(calibration:) name:@"calibration" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(showNextModel:) name:@"showNextModel" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(startTraining:) name:@"startTraining" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(prepareTraining:) name:@"prepareTraining" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(showModalityInstructions:) name:@"showModalityInstructions" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(showDatasetInfo:) name:@"showDatasetInfo" object:nil];
        trainingMode = NO;
        namesForTrainingDatasets = [[NSDictionary dictionaryWithObjectsAndKeys:@"homicide", @"physical", @"food", @"2D", @"births", @"mono", @"tax", @"stereo", nil] retain];
        
        trainingAlreadyFinished = NO;
        block = trial = repetition = question = 0;

        NSString *docsDir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
        filename = [[[docsDir stringByAppendingPathComponent:@"recover"] stringByAppendingPathExtension:@"plist"] retain];
        recoverLogFilename = [[[docsDir stringByAppendingPathComponent:@"recoverLog"] stringByAppendingPathExtension:@"plist"] retain];
        NSLog(@"filename for recover file %@, for recover log file: %@", filename, recoverLogFilename);
//        NSLog(@"path : %@", [[[NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"datasetNames" ofType:@"plist"]] objectForKey:@"Root"] description]);
        datasetNames = [[NSArray arrayWithArray:[[NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"datasetNames" ofType:@"plist"]] objectForKey:@"Root"]] retain];
        tuioSender = [[TuioSender senderWithDestinationHostName:@"192.168.2.178" portNumber:3333] retain];
        
        // send add tuio commands for all datasets (necessary for recover function)
//        for (int i = 1; i <= 11; i++) {
//            [tuioSender objectStarted:i value:1 x:1 y:1];
//        }
//        
//        // same for stereo test
//        [tuioSender objectStarted:7 value:1 x:1 y:1];
        
        rootview = viewcontroller;
        modelController = [rootview modelController];

        // check if we need to recover a previous session
        self.recover = NO;
        if ([[NSFileManager defaultManager] fileExistsAtPath:filename]) {
            self.recover = YES;
            [self readRecoverFile];
//            [self setSubjectGroup:[[recoverDict valueForKey:@"userID"] intValue]];
//            block = [[recoverDict valueForKey:@"block"] intValue];
//            trial = [[recoverDict valueForKey:@"trial"] intValue];
//            repetition = [[recoverDict valueForKey:@"repetition"] intValue];
//            question = [[recoverDict valueForKey:@"question"] intValue] + 1;
            
            logger = [[Logger alloc] initWithState:self andLog:[[NSMutableDictionary dictionaryWithContentsOfFile:[[[NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0] stringByAppendingPathComponent:@"recoverLog"] stringByAppendingPathExtension:@"plist"]] retain]];
//            if ((!advancedToNextBlock && !advancedToNextTrial && question == 0 && logger.questionsOfCurrentTrial != nil && trainingAlreadyFinished))
//                question++;
            [rootview setLogger:logger];
            if (advancedToNextBlock) {
                [logger startNewBlock:currentCondition];
            }
            else if (advancedToNextTrial)
                [logger startNewTrial:[NSDictionary dictionaryWithObject:[datasetNames objectAtIndex:indexOfCurrentDataset] forKey:@"dataset"]];
            
//            conditionOrder = [[[[NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"latinsquare" ofType:@"plist"]] objectForKey:@"Root"] objectAtIndex:userID]retain];
//            UIAlertView *recoverAlert = [[UIAlertView alloc] initWithTitle:@"Recovering" message:@"Press OK to continue the experiment." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
//            recoverAlert.tag = 5;
//            [recoverAlert show];
//            [recoverAlert release];

            NSLog(@"recovering to userid %i, block %i, trial %i, repetition %i, question %i", userID, block, trial, repetition, question);
        }
        if (!self.recover) {
            logger = [[Logger alloc] init];
            [rootview setLogger:logger];
//            [rootview startStereoTest:nil];
            
        }
        
    }
    return self;
}

//TODO: add modulo
- (void) setSubjectGroup:(int)_userID
{
    userID = _userID - 1;
    conditionOrder = [[[[NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"latinsquare" ofType:@"plist"]] objectForKey:@"Root"] objectAtIndex:(userID % NUM_BLOCKS)]retain];
//    currentCondition = [conditionOrder objectAtIndex:block];
//    indexOfCurrentDataset = [[[currentCondition objectForKey:@"datasets"] objectAtIndex:repetition] intValue];
//    [rootview startTrial];
}


- (bool)writeRecoverFile
{
    NSDictionary *recoverDict = [NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithInt:userID+1], @"userID", [NSNumber numberWithInt:block], @"block", [NSNumber numberWithInt:trial], @"trial", [NSNumber numberWithInt:repetition], @"repetition", [NSNumber numberWithInt:question], @"question", [NSNumber numberWithBool:trainingAlreadyFinished], @"trainingDone", nil];
    NSLog(@"writing recover file: \n%@", recoverDict);
    return [recoverDict writeToFile:filename atomically:YES]; 
}

- (NSDictionary*)readRecoverFile
{
    advancedToNextBlock = NO;
    NSDictionary *recoverDict = [NSDictionary dictionaryWithContentsOfFile:filename];
    [self setSubjectGroup:[[recoverDict valueForKey:@"userID"] intValue]];
    block = [[recoverDict valueForKey:@"block"] intValue];
    trial = [[recoverDict valueForKey:@"trial"] intValue];
    repetition = [[recoverDict valueForKey:@"repetition"] intValue];
    trainingAlreadyFinished = [[recoverDict objectForKey:@"trainingDone"] boolValue];
    question = [[recoverDict valueForKey:@"question"] intValue];
    currentCondition = [conditionOrder objectAtIndex:block];
    conditionName = [[currentCondition valueForKey:@"name"] retain];
    indexOfCurrentDataset = [[[currentCondition objectForKey:@"datasets"] objectAtIndex:repetition] intValue];
    [self getCurrentSetOfQuestions];
    
    NSLog(@"restored values to userid %i, block %i, trial %i, repetition %i, question %i", userID, block, trial, repetition, question);

//    if (question + 1 < numberOfQuestions) question++;
    
    // we need to check here whether we recovered from the end of a trial or block
//    if (!(question == 0 && trainingAlreadyFinished))
//    {
//        question++;
//        NSLog(@"advancing question as first one has already been done");
//    }
//    if (!(question  < numberOfQuestions) && repetition + 1 < NUM_REPETITIONS) {
//        NSLog(@"advancing questions as trial was finished");
//        question = 0;
//        repetition++;
//        trial++;
//        advancedToNextTrial = YES;
//        indexOfCurrentDataset = [[[currentCondition objectForKey:@"datasets"] objectAtIndex:repetition] intValue];
//        [self getCurrentSetOfQuestions];
//    }
//    else if (!((question < numberOfQuestions) || repetition + 1 < NUM_REPETITIONS) && block + 1 < NUM_BLOCKS) {
//        NSLog(@"advancing question as block was finished");
//        question = 0;
//        repetition = 0;
//        trial++;
//        block++;
//        advancedToNextBlock = YES;
//        currentCondition = [conditionOrder objectAtIndex:block];
//        conditionName = [currentCondition valueForKey:@"name"];
//        indexOfCurrentDataset = [[[currentCondition objectForKey:@"datasets"] objectAtIndex:repetition] intValue];
//        [self getCurrentSetOfQuestions];
//    }
//    else [rootview finishExperiment];
    return recoverDict;
}


- (void) clearRecoverFiles
{
    if ([[NSFileManager defaultManager] fileExistsAtPath:filename]) {
        bool deletedRecoverFile = [[NSFileManager defaultManager] removeItemAtPath:filename error:nil];
        NSLog(@"removal of recover file was %@", deletedRecoverFile ? @"successful" : @"failed");
        
    }
    if ([[NSFileManager defaultManager] fileExistsAtPath:recoverLogFilename]) {
        bool deletedRecoverFile = [[NSFileManager defaultManager] removeItemAtPath:recoverLogFilename error:nil];
        NSLog(@"removal of recover file was %@", deletedRecoverFile ? @"successful" : @"failed");
        
    }

}

- (void) getCurrentSetOfQuestions
{
    NSString *name = [datasetNames objectAtIndex:indexOfCurrentDataset];
    currentSetOfQuestions = [[NSArray arrayWithArray:[
                                                        [NSDictionary dictionaryWithContentsOfFile:
                                                            [[NSBundle mainBundle] pathForResource:name ofType:@"plist"]
                                                         ] 
                                                      objectForKey:@"Root"]
                             ] retain];
    numberOfQuestions = [currentSetOfQuestions count];
//    NSLog(@"current set of questions: %@", [currentSetOfQuestions description]);
}


- (void) showNextModel:(NSNotification*)notification
{
//    NSLog(@"questions: %@", [modelController.pageData description]);
    DataViewController *dataView = [rootview startTrial];
    [dataView showVisualizationImageForCondition:conditionName];
    [dataView registerLogger:logger];

    float indexSentForDataset = ((float)indexOfCurrentDataset / (float)([datasetNames count] + namesForTrainingDatasets.count - 1));
    NSLog(@"TUIO: sent index %f", indexSentForDataset);
    [tuioSender objectStarted:[[currentCondition valueForKey:@"id"] intValue] value:0 x:indexSentForDataset y:0];

}


- (void) sendModelCloseMessageFor:(int)conditionID
{
    [rootview.pageViewController.view removeFromSuperview];
    [tuioSender objectEnded:conditionID value:0 x:1 y:1];
}


- (void)stereoTest:(NSNotification*)notification
{
    float screen = [[notification.userInfo valueForKey:@"screen"] floatValue];
    if (screen == 4)
    {
        bool passed = [[notification.userInfo valueForKey:@"result"] boolValue];
        [tuioSender objectValueChanged:7 value:5 x:0 y:0.9];
//        NSLog(@"root view received stereo test %@", passed ? @"passed." : @"failed.");
        if (!passed)
        {
//            finished = true;
            UIAlertView *end = [[UIAlertView alloc] initWithTitle:@"End" message:@"Unfortunately you failed the stereovision test. Please call the instructor." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
            end.tag = 6;
            [end show];
            [end release];
        }
        else {
            if (![conditionName isEqualToString:@"stereo"])
            {
                UIAlertView *glasses = [[UIAlertView alloc] initWithTitle:@"Thank you." message:@"You can take off the glasses for now. You will be reminded when you need them again." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
                [glasses show];
                [glasses release];
            }
            [self showModalityInstructions:nil];
        }
    }
    else
        [tuioSender objectStarted:7 value:0 x:0 y:screen/10.0];
    
}

- (void)calibration:(NSNotification*)notification
{
    float screen = [[notification.userInfo valueForKey:@"screen"] floatValue];
    NSString *action = [notification.userInfo valueForKey:@"action"];
    if ([action isEqualToString:@"start"])
        [tuioSender objectStarted:7 value:0 x:0 y:screen/10.0];
    else 
        [tuioSender objectValueChanged:7 value:5 x:0 y:0.9];

}


- (void) alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    int alertID = alertView.tag;
    DataViewController *dataView;
    switch (alertID) {
        case 1: // experiment finished -> save, remove recover file and exit
            [logger experimentFinished];
            [self sendModelCloseMessageFor:[[currentCondition valueForKey:@"id"] intValue]];
            [tuioSender objectStarted:9 value:0 x:0 y:0];
            if ([[NSFileManager defaultManager] fileExistsAtPath:filename]) {
                bool deletedRecoverFile = [[NSFileManager defaultManager] removeItemAtPath:filename error:nil];
                NSLog(@"removal of recover file was %@", deletedRecoverFile ? @"successful" : @"failed");
                
            }
            exit(0);

            break;
        case 2: // block finished - > start new one
            [self sendModelCloseMessageFor:[[currentCondition valueForKey:@"id"] intValue]];
           if (block < NUM_BLOCKS - 1)
            {
                if ([conditionName isEqualToString:@"stereo"])
                {
                    UIAlertView* glasses = [[UIAlertView alloc] initWithTitle:@"You can take off the glasses now." message:nil delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
                    [glasses show];
                    [glasses release];
                }
                block++;
                trial++;
                repetition = 0;
                question = 0;
                currentCondition = [conditionOrder objectAtIndex:block];
                conditionName = [currentCondition valueForKey:@"name"];
                [logger startNewBlock:currentCondition];
                [logger writeLogWithCurrentTimeAndDate];
                trainingAlreadyFinished = NO;
                [self writeRecoverFile];
//                [logger startNewTrial:[NSDictionary dictionaryWithObject:[datasetNames objectAtIndex:indexOfCurrentDataset] forKey:@"dataset"]];
                
                // TODO: do training here now
                trainingMode = YES;
                [self showModalityInstructions:nil];
                
                // commented tentatively
                /*
                indexOfCurrentDataset = [[[currentCondition objectForKey:@"datasets"] objectAtIndex:repetition] intValue];
                [logger startNewBlock:currentCondition];
                [logger startNewTrial:[NSDictionary dictionaryWithObject:[datasetNames objectAtIndex:indexOfCurrentDataset] forKey:@"dataset"]];
               [self getCurrentSetOfQuestions];
                [modelController setPageData:currentSetOfQuestions];
                modelController.datasetName = [datasetNames objectAtIndex:indexOfCurrentDataset];
                rootview.pageCounter = 0;
//                dataView = [rootview startTrial];
//                [dataView registerLogger:logger];
                [rootview showDatasetInfo:[datasetNames objectAtIndex:indexOfCurrentDataset]];
                [rootview showInstructions:[currentCondition valueForKey:@"name"]];
//                [self showNextModel:nil];
                 */
           }
            break;
        case 3: // trial finished -> start new one
            if (![[currentCondition valueForKey:@"name"] isEqualToString:@"physical"])
                [self sendModelCloseMessageFor:[[currentCondition valueForKey:@"id"] intValue]];
            
            if (repetition < NUM_REPETITIONS - 1)
            {
                if (!trainingMode)    
                {    
                    repetition++;
                    trial++;
                }
                else {
                    trainingAlreadyFinished = YES;
                    trainingMode = NO;
                    [rootview setTraining:NO];
                    rootview.trainingFrame.hidden = YES;
                }
                question = 0;
                indexOfCurrentDataset = [[[currentCondition objectForKey:@"datasets"] objectAtIndex:repetition] intValue];
                [logger startNewTrial:[NSDictionary dictionaryWithObject:[datasetNames objectAtIndex:indexOfCurrentDataset] forKey:@"dataset"]];
                [logger writeLogWithCurrentTimeAndDate];
                [self writeRecoverFile];
                [self getCurrentSetOfQuestions];
                [modelController setPageData:currentSetOfQuestions];
                modelController.datasetName = [datasetNames objectAtIndex:indexOfCurrentDataset];
                rootview.pageCounter = 0;
                [rootview showDatasetInfo:[datasetNames objectAtIndex:indexOfCurrentDataset]];
            }
            
            break;
        case 4: // question finished
            if (question < numberOfQuestions - 1)
            {
                question++;
                if (!trainingMode) {
                    [logger writeLogWithCurrentTimeAndDate];
                    [self writeRecoverFile];
                }
                rootview.pageCounter++;
//                NSLog(@"moving to question %i", rootview.pageCounter);
                dataView = [rootview nextView];
                [dataView registerLogger:logger];
                [dataView showVisualizationImageForCondition:conditionName];
            }
//            [logger writeLogWithCurrentTimeAndDate];
            break;
        case 5: // recover
            // load questions
//            [self getCurrentSetOfQuestions];
            if (buttonIndex == 1) {
                [self clearRecoverFiles];
                exit(0);
            }
            [modelController setPageData:currentSetOfQuestions];
            modelController.datasetName = [datasetNames objectAtIndex:indexOfCurrentDataset];
            [rootview.userInfoView removeFromSuperview];
//            dataView = [rootview startTrial];
//            [dataView registerLogger:logger];
            rootview.currentConditionName = conditionName;

            if (repetition == 0 && question == 0)
            {
//                [rootview showDatasetInfo:[datasetNames objectAtIndex:indexOfCurrentDataset]];
//                [rootview showInstructions:[currentCondition valueForKey:@"name"]];
//                [logger startNewBlock:currentCondition];
                if (!trainingAlreadyFinished)
                    [self showModalityInstructions:nil];
                else {
                    [rootview showDatasetInfo:modelController.datasetName];
                }
            }
            else if (question == 0)
                [rootview showDatasetInfo:modelController.datasetName];
            else
            {
                [self showNextModel:nil];
//                NSLog(@"page counter %i", rootview.pageCounter);
                rootview.pageCounter = question;
                dataView = [rootview goToPage:question];
                [dataView registerLogger:logger];
                [dataView showVisualizationImageForCondition:conditionName];
//                NSLog(@"moving to question %i", rootview.pageCounter);

            }
            break;
            
        case 6:
            exit(0);
            break;
            
        case 7: // start experiment
            currentCondition = [conditionOrder objectAtIndex:block];
            conditionName = [currentCondition valueForKey:@"name"];

            [logger startNewBlock:currentCondition];
            [rootview setTraining:YES];
            [rootview showInstructions:@"questions"];
            break;
                        
//        case 9: // training
            
            break;
            
        default:
            break;
    }
    
}

- (void) showDatasetInfo:(NSNotification*)notification
{
    if (trainingMode)
        [rootview showDatasetInfo:[namesForTrainingDatasets valueForKey:conditionName]];
    else {
        [rootview showDatasetInfo:[datasetNames objectAtIndex:[[currentCondition valueForKey:@"id"] intValue]]];
    }
}


- (void) showModalityInstructions:(NSNotification*)notification
{
    conditionName = [currentCondition valueForKey:@"name"];
    trainingMode = YES;
    [rootview setTraining:YES];
    NSString* currentDatasetName = [namesForTrainingDatasets valueForKey:conditionName]; 
    [rootview showModalityInstructions:conditionName];
    modelController.datasetName = currentDatasetName;
    [modelController setPageData:[[NSArray arrayWithArray:[
                                                           [NSDictionary dictionaryWithContentsOfFile:
                                                            [[NSBundle mainBundle] pathForResource:currentDatasetName ofType:@"plist"]
                                                            ] 
                                                           objectForKey:@"Root"]
                                   ] retain]];
    numberOfQuestions = [modelController.pageData count];
    float indexSentForDataset = ((float)(datasetNames.count + [[currentCondition valueForKey:@"id"] intValue] - 1) / (float)([datasetNames count] + namesForTrainingDatasets.count - 1));
    NSLog(@"TUIO: sent index %f", indexSentForDataset);
    [tuioSender objectStarted:[[currentCondition valueForKey:@"id"] intValue] value:0 x:indexSentForDataset y:0];        
}

- (void) prepareTraining:(NSNotification*)notification
{
    [rootview showTrainingInstructions];

}

- (void) startTraining:(NSNotification*)notification
{
    DataViewController *dataView = [rootview startTrial];
    [dataView showVisualizationImageForCondition:conditionName];
    
}

@end
