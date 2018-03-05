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
#define NUM_REPETITIONS 2 // there's another NUM_REPETITIONS in rootviewcontroller class

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
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(sendStartMessage:) name:@"sendStartMessage" object:nil];        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(showDatasetInfo:) name:@"showDatasetInfo" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(showCalibrationInstruction:) name:@"calibrateSensor" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(logAdditionalQuestions:) name:@"additionalQuestionsFinished" object:nil];
        trainingMode = NO;
        namesForTrainingDatasets = [[NSDictionary dictionaryWithObjectsAndKeys:@"homicide", @"physical-touch", @"suicide", @"physical-notouch", @"births", @"virtual-prop", @"exports", @"virtual-mouse", nil] retain];
        
        trainingAlreadyFinished = NO;
        block = trial = repetition = question = 0;

        NSString *docsDir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
        filename = [[[docsDir stringByAppendingPathComponent:@"recover"] stringByAppendingPathExtension:@"plist"] retain];
        recoverLogFilename = [[[docsDir stringByAppendingPathComponent:@"recoverLog"] stringByAppendingPathExtension:@"plist"] retain];
        NSLog(@"filename for recover file %@, for recover log file: %@", filename, recoverLogFilename);
//        NSLog(@"path : %@", [[[NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"datasetNames" ofType:@"plist"]] objectForKey:@"Root"] description]);
        datasetNames = [[NSArray arrayWithArray:[[NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"datasetNames" ofType:@"plist"]] objectForKey:@"Root"]] retain];
        tuioSender = [[TuioSender senderWithDestinationHostName:/*@"192.168.0.11"*/@"192.168.2.178" portNumber:3334] retain];
        
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
//    NSLog(@"writing recover file: \n%@", recoverDict);
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
    [dataView initEverything];

    float indexSentForDataset = ((float)indexOfCurrentDataset / (float)([datasetNames count] + namesForTrainingDatasets.count - 1));
    NSLog(@"TUIO: sent index %f", indexSentForDataset);
    [tuioSender objectStarted:[[currentCondition valueForKey:@"id"] intValue] value:0 x:indexSentForDataset y:0];

    [self showCalibrationInstruction:nil];
}

- (void) nextQuestion
{
    if (question < numberOfQuestions - 1)
    {
        question++;
        if (!trainingMode) {
            [logger writeLogWithCurrentTimeAndDate];
            [self writeRecoverFile];
            float indexSentForDataset = ((float)indexOfCurrentDataset / (float)([datasetNames count] + namesForTrainingDatasets.count - 1));
            [tuioSender objectStarted:[[currentCondition objectForKey:@"id"] intValue] value:question x:indexSentForDataset y:0];
            NSLog(@"TUIO id %i dataset %i question %i", [[currentCondition objectForKey:@"id"] intValue] , indexOfCurrentDataset, question);
        }
        rootview.pageCounter++;
        //                NSLog(@"moving to question %i", rootview.pageCounter);
        DataViewController* dataView = [rootview nextView];
        [dataView registerLogger:logger];
        [dataView initEverything];
        [dataView showVisualizationImageForCondition:conditionName];
    }

}

- (void) showCalibrationInstruction:(NSNotification*)notification
{
    if (![conditionName isEqualToString:@"virtual-mouse"]) {
        UIView *cal = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 768, 1024)];
        cal.backgroundColor = [UIColor whiteColor];
        UIImageView *calI = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"calib-physical"]];
        calI.center = CGPointMake(768/2, 1024/2+50);
        UILabel *ins = [[UILabel alloc] initWithFrame:CGRectMake(50, 60, 668, 200)];
        ins.numberOfLines = 0;
        ins.textAlignment = UITextAlignmentCenter;
        ins.font = [UIFont systemFontOfSize:24];
        ins.text = @"1) Pick up the physical 3d bar chart and hold it the way you would hold it to answer the questions. \n2) Without translating the object, rotate it to a side view as depicted. Make sure the orange bars are oriented towards you, then press Calibrate to save the calibration settings.";
        [cal addSubview:ins];
        [cal addSubview:calI];
        UIButton *calibrate = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        UIButton *close = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        
        [calibrate setTitle:@"Calibrate" forState:UIControlStateNormal];
        [close setTitle:@"Back" forState:UIControlStateNormal];
        [close addTarget:cal action:@selector(removeFromSuperview) forControlEvents:UIControlEventTouchUpInside];
        [calibrate addTarget:self action:@selector(sendCalibrateMessage:) forControlEvents:UIControlEventTouchUpInside];
        calibrate.frame = CGRectMake(50, 880, 268, 100);
        close.frame = CGRectMake(450, 880, 268, 100);
        calibrate.font = [UIFont systemFontOfSize:36];
        close.font = [UIFont systemFontOfSize:36];
        [cal addSubview:calibrate];
        [cal addSubview:close];
        
        UILabel *title = [[UILabel alloc] initWithFrame:CGRectMake(0, 10, 768, 50)];
        title.backgroundColor = [UIColor clearColor];
        title.font = [UIFont systemFontOfSize:48];
        title.textAlignment = UITextAlignmentCenter;
        title.text = @"Calibration";
        [cal addSubview:title];
        [rootview.view addSubview:cal];
        NSLog(@"cal view added for condition %@", conditionName);
    }
}

- (void) sendCalibrateMessage:(id)sender
{
    NSLog(@"sending calibrate message");
    [tuioSender objectValueChanged:8 value:rand()%1000 x:0 y:0];

}

- (void) closeCurrentModel
{
    [self sendModelCloseMessageFor:[[currentCondition valueForKey:@"id"] intValue]];
}


- (void) sendModelCloseMessageFor:(int)conditionID
{
    [rootview.pageViewController.view removeFromSuperview];
    [tuioSender objectEnded:conditionID value:0 x:1 y:1];
}





- (void) alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    int alertID = alertView.tag;
    DataViewController *dataView;
    switch (alertID) {
        case 1: // experiment finished -> save, remove recover file and exit
            [logger experimentFinished];
            [self sendModelCloseMessageFor:[[currentCondition valueForKey:@"id"] intValue]];
            [tuioSender objectStarted:6 value:0 x:0 y:1];
            if ([[NSFileManager defaultManager] fileExistsAtPath:filename]) {
                bool deletedRecoverFile = [[NSFileManager defaultManager] removeItemAtPath:filename error:nil];
                NSLog(@"removal of recover file was %@", deletedRecoverFile ? @"successful" : @"failed");
                
            }
            exit(0);

            break;
        case 2: // block finished - > start new one
//            [self sendModelCloseMessageFor:[[currentCondition valueForKey:@"id"] intValue]];
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
//            if (![[currentCondition valueForKey:@"name"] isEqualToString:@"physical"])
//                [self sendModelCloseMessageFor:[[currentCondition valueForKey:@"id"] intValue]];
//            
            if (repetition < NUM_REPETITIONS)
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
                if ([[currentCondition valueForKey:@"name"] isEqualToString:@"physical-touch"] || [[currentCondition valueForKey:@"name"] isEqualToString:@"physical-notouch"] ) {
                    UIAlertView *nextModel = [[UIAlertView alloc] initWithTitle:@"Next model required" message:@"Please ask the experimenter to hand you the next physical model." delegate:nil cancelButtonTitle:@"Received it." otherButtonTitles:nil, nil];
                    [nextModel show];
                    [nextModel release];
                }
                [self showNextModel:nil];
//                [rootview showDatasetInfo:[datasetNames objectAtIndex:indexOfCurrentDataset]];
            }
            
            break;
        case 4: // question finished
            if (question < numberOfQuestions - 1)
            {
                question++;
                if (!trainingMode) {
                    [logger writeLogWithCurrentTimeAndDate];
                    [self writeRecoverFile];
                    [tuioSender objectStarted:[[currentCondition objectForKey:@"id"] intValue] value:question x:indexOfCurrentDataset y:0];
                    NSLog(@"TUIO id %i question %i", [[currentCondition objectForKey:@"id"] intValue] , question);
                }
                rootview.pageCounter++;
//                NSLog(@"moving to question %i", rootview.pageCounter);
                dataView = [rootview nextView];
                [dataView registerLogger:logger];
                [dataView initEverything];
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
//                    [rootview showDatasetInfo:modelController.datasetName];
                    [self showNextModel:nil];
                    
                }
            }
            else if (question == 0)
                [self showNextModel:nil];
//                [rootview showDatasetInfo:modelController.datasetName];
            else
            {
                [self showNextModel:nil];
//                NSLog(@"page counter %i", rootview.pageCounter);
                rootview.pageCounter = question;
                dataView = [rootview goToPage:question];
                [dataView registerLogger:logger];
                [dataView initEverything];
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
    [dataView initEverything];
    [dataView showVisualizationImageForCondition:conditionName];
//    [self showCalibrationInstruction];
}

- (void) sendStartMessage:(NSNotification*)notification
{
    [tuioSender objectValueChanged:7 value:rand()%1000 x:1 y:0];
}

- (void) logAdditionalQuestions:(NSNotification*)not
{
    [logger additionalQuestions:not.userInfo];
    [self nextQuestion];
}

@end
