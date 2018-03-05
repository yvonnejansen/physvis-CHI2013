//
//  InstructionView.m
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 3/15/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import "InstructionView.h"
#import "QuartzCore/QuartzCore.h"

@implementation InstructionView

@synthesize currentCondition;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        instructions = [[UILabel alloc] initWithFrame:CGRectInset(frame, 100, 100)];
        instructions.lineBreakMode = UILineBreakModeWordWrap;
        instructions.numberOfLines = 0;
        instructions.text = @"test";
        instructions.layer.cornerRadius = 8;
        instructions.font = [UIFont systemFontOfSize:20];
        [self addSubview:instructions];
        
        pageNumber = -1;
        showingInitialInstructions = NO;
        showingTrainingInstructions = NO;
        showingModalityInstructions = NO;
        
        title = [[UILabel alloc] initWithFrame:CGRectMake(55, 35, frame.size.width - 110, 50)];
        title.lineBreakMode = UILineBreakModeWordWrap;
        title.numberOfLines = 0;
        title.textAlignment = UITextAlignmentCenter;
        title.font = [UIFont systemFontOfSize:25];
        [self addSubview:title];

        instructionImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 768, 1024)];
        instructionImageView.hidden = YES;
        [self addSubview:instructionImageView];

        continueButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        continueButton.frame = CGRectInset(CGRectOffset(title.frame, 0, 920), 150, 0);
        [continueButton setTitle:@"Continue" forState:UIControlStateNormal];
        continueButton.hidden = YES;
        [continueButton addTarget:self action:@selector(closeInstructions:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:continueButton];

        nextButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        nextButton.frame = CGRectOffset(continueButton.frame, 250, 0);
        [nextButton setTitle:@"Next page" forState:UIControlStateNormal];
        [nextButton addTarget:self action:@selector(nextPage:) forControlEvents:UIControlEventTouchUpInside];
        nextButton.hidden = YES;
        [self addSubview:nextButton];
        
        calibrateButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        calibrateButton.frame = CGRectMake(150, 500, 150, 80);
        calibrateButton.font = [UIFont systemFontOfSize:24];
        [calibrateButton setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        calibrateButton.center = CGPointMake(768/2, 448);
        calibrateButton.hidden = YES;
        [calibrateButton setTitle:@"calibrate" forState:UIControlStateNormal];
        [calibrateButton addTarget:self action:@selector(calibrate) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:calibrateButton];
        
        
    }
    return self;
}

//- (void) displayDatasetInfoForTraining:(NSString *)_datasetInfo
//{
//    training = YES;
//    [self displayDatasetInfoFor:_datasetInfo];
//}
//
//- (void) displayDatasetInfoFor:(NSString*)_datasetInfo
//{
//    instructions.text = NSLocalizedStringFromTable(_datasetInfo, @"datasetInfo", nil);
//    NSString *titleString = [NSString stringWithFormat:@"title.%@", _datasetInfo];
//    title.text = NSLocalizedStringFromTable(titleString, @"datasetInfo", nil);
//    datasetInfo = true;
//    continueButton.hidden = NO;
//}


- (void) calibrate
{
    [[NSNotificationCenter defaultCenter] postNotificationName:@"calibrateSensor" object:nil];
}


- (IBAction)nextPage:(id)sender
{
    if (imageInstructions != nil && pageNumber < imageInstructions.count)
    {
        UIImage *currentPage = [UIImage imageNamed:[imageInstructions objectAtIndex:pageNumber]];
        [instructionImageView setImage:currentPage];
//        NSLog(@"displaying page %i", pageNumber);
        pageNumber++;
        if (pageNumber < imageInstructions.count) {
            nextButton.hidden = NO;
            
//            nextButton.
        }
        else {
            nextButton.hidden = YES;
            continueButton.hidden = NO;
            
//            calibrateButton.hidden = [currentCondition isEqualToString:@"virtual-mouse"] || (showingTrainingInstructions && pageNumber > 0) ? YES : NO;
        }
        // display next and continue button as appropriate, increase page counter and check if more pages are left to display
        // do we need to get back to previous pages?
    }
}


- (void) loadImageInstructionsFor:(NSString*)description
{
        imageInstructions = [[NSArray arrayWithArray:[
                                                      [NSDictionary dictionaryWithContentsOfFile:
                                                       [[NSBundle mainBundle] pathForResource:@"imageFilenames" ofType:@"plist"]
                                                       ] 
                                                      objectForKey:description]
                              ] retain];
    if ([description isEqualToString:@"training"])
    {    
        showingTrainingInstructions = YES;
        calibrateButton.hidden = YES;
    }
    else if ([description isEqualToString:@"questions"])
        showingInitialInstructions = YES;
    else {
        showingModalityInstructions = YES;
        calibrateButton.hidden = NO;
        if ([description isEqualToString:@"physical-touch"])
            calibrateButton.center = CGPointMake(768/2, 448);
        else if ([description isEqualToString:@"physical-notouch"])
            calibrateButton.center = CGPointMake(768/2, 418);
        else if ([description isEqualToString:@"virtual-prop"])
            calibrateButton.center = CGPointMake(768/2, 555);
        else if ([description isEqualToString:@"virtual-mouse"])
            calibrateButton.hidden = YES;



    }
        
    
    pageNumber = 0;
}

- (void) displayInstructionsFor:(NSString *)condition
{
    if (![condition isEqualToString:@"questions"])
        currentCondition = condition;
    else {
        showingInitialInstructions = YES;
    }
    [self nextPage:nil];
    instructionImageView.hidden = NO;
    datasetInfo = false;
}

- (void) displayInstructionsForTraining:(NSString *)condition
{
    training = YES;
    [self loadImageInstructionsFor:condition];
    [self displayInstructionsFor:condition];
}


- (IBAction)closeInstructions:(id)sender
{
    [self removeFromSuperview];
    pageNumber = -1;
//    continueButton.hidden = YES;
    nextButton.hidden = YES;
//    if ([currentCondition isEqualToString:@"stereo"])
//        [[NSNotificationCenter defaultCenter] postNotificationName:@"calibration" object:nil userInfo:[[NSDictionary dictionaryWithObjectsAndKeys:@"start", @"action", [NSNumber numberWithInt:1], @"screen", nil] retain]];
    // TODO: this might be wrong (no check whether we're in training mode)
    if (showingInitialInstructions)
    {
        showingInitialInstructions = NO;
//        [[NSNotificationCenter defaultCenter] postNotificationName:@"startStereoTest" object:nil];
        // TODO: this has to go somewhere else!
        [[NSNotificationCenter defaultCenter] postNotificationName:@"showModalityInstructions" object:nil];
    }
    else if (showingModalityInstructions)
    {
        training = YES;
        showingModalityInstructions = NO;
        [[NSNotificationCenter defaultCenter] postNotificationName:@"prepareTraining" object:nil];
    }
    else if (showingTrainingInstructions)
    {
        showingTrainingInstructions = NO;
        [[NSNotificationCenter defaultCenter] postNotificationName:@"startTraining" object:nil];
    }
    if ([currentCondition isEqualToString:@"physical"] && !training) {
        UIAlertView *nextModel = [[UIAlertView alloc] initWithTitle:@"Next model required" message:@"Please ask the experimenter to hand you the next physical model." delegate:nil cancelButtonTitle:@"Received it." otherButtonTitles:nil, nil];
        [nextModel show];
        [nextModel release];
    }
//        if (training)
//        {
//            training = NO;
//        }
//        else
//            [[NSNotificationCenter defaultCenter] postNotificationName:@"showNextModel" object:nil];
//    }
}

@end
