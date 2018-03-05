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
        continueButton.frame = CGRectInset(CGRectOffset(title.frame, 0, 850), 150, -20);
        [continueButton setTitle:@"Continue" forState:UIControlStateNormal];
        continueButton.hidden = YES;
        [continueButton addTarget:self action:@selector(closeInstructions:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:continueButton];

        nextButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        nextButton.frame = CGRectOffset(continueButton.frame, 200, 0);
        [nextButton setTitle:@"Next page" forState:UIControlStateNormal];
        [nextButton addTarget:self action:@selector(nextPage:) forControlEvents:UIControlEventTouchUpInside];
        nextButton.hidden = YES;
        [self addSubview:nextButton];
        
        
    }
    return self;
}

- (void) displayDatasetInfoForTraining:(NSString *)_datasetInfo
{
    training = YES;
    [self displayDatasetInfoFor:_datasetInfo];
}

- (void) displayDatasetInfoFor:(NSString*)_datasetInfo
{
    instructions.text = NSLocalizedStringFromTable(_datasetInfo, @"datasetInfo", nil);
    NSString *titleString = [NSString stringWithFormat:@"title.%@", _datasetInfo];
    title.text = NSLocalizedStringFromTable(titleString, @"datasetInfo", nil);
    datasetInfo = true;
    continueButton.hidden = NO;
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
        }
        // display next and continue button as appropriate, increase page counter and check if more pages are left to display
        // do we need to get back to previous pages?
    }
}


- (void) loadImageInstructionsFor:(NSString*)description
{
    if ([description isEqualToString:@"questions"]) {
        imageInstructions = [[NSArray arrayWithArray:[
                                                      [NSDictionary dictionaryWithContentsOfFile:
                                                       [[NSBundle mainBundle] pathForResource:@"imageFilenames" ofType:@"plist"]
                                                       ] 
                                                      objectForKey:@"questions"]
                              ] retain];
//        NSLog(@"loading question instructions");
    }
    else if ([description isEqualToString:@"physical"]) {
        imageInstructions = [[NSArray arrayWithArray:[
                                                      [NSDictionary dictionaryWithContentsOfFile:
                                                       [[NSBundle mainBundle] pathForResource:@"imageFilenames" ofType:@"plist"]
                                                       ] 
                                                      objectForKey:@"physical"]
                              ] retain];

    }
    else if ([description isEqualToString:@"2D"]) {
        imageInstructions = [[NSArray arrayWithArray:[
                                                      [NSDictionary dictionaryWithContentsOfFile:
                                                       [[NSBundle mainBundle] pathForResource:@"imageFilenames" ofType:@"plist"]
                                                       ] 
                                                      objectForKey:@"2D"]
                              ] retain];
        
    }
    else if ([description isEqualToString:@"mono"]) {
        imageInstructions = [[NSArray arrayWithArray:[
                                                      [NSDictionary dictionaryWithContentsOfFile:
                                                       [[NSBundle mainBundle] pathForResource:@"imageFilenames" ofType:@"plist"]
                                                       ] 
                                                      objectForKey:@"mono"]
                              ] retain];
        
    }
    else if ([description isEqualToString:@"stereo"]) {
        imageInstructions = [[NSArray arrayWithArray:[
                                                      [NSDictionary dictionaryWithContentsOfFile:
                                                       [[NSBundle mainBundle] pathForResource:@"imageFilenames" ofType:@"plist"]
                                                       ] 
                                                      objectForKey:@"stereo"]
                              ] retain];
        
    }
    else if ([description isEqualToString:@"training"]) {
        imageInstructions = [[NSArray arrayWithArray:[
                                                      [NSDictionary dictionaryWithContentsOfFile:
                                                       [[NSBundle mainBundle] pathForResource:@"imageFilenames" ofType:@"plist"]
                                                       ] 
                                                      objectForKey:@"training"]
                              ] retain];
        showingTrainingInstructions = YES;
        
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
        [[NSNotificationCenter defaultCenter] postNotificationName:@"startStereoTest" object:nil];
        // TODO: this has to go somewhere else!
//        [[NSNotificationCenter defaultCenter] postNotificationName:@"showModalityInstructions" object:nil];
    }
    else if (showingTrainingInstructions)
    {
        showingTrainingInstructions = NO;
        [[NSNotificationCenter defaultCenter] postNotificationName:@"showDatasetInfo" object:nil];
//        [[NSNotificationCenter defaultCenter] postNotificationName:@"startTraining" object:nil];
    }
     else if (!datasetInfo && training)
     {
         training = NO;
         [[NSNotificationCenter defaultCenter] postNotificationName:@"prepareTraining" object:nil];
     }
    else if (datasetInfo){
        datasetInfo = false;
        if ([currentCondition isEqualToString:@"physical"] && !training) {
            UIAlertView *nextModel = [[UIAlertView alloc] initWithTitle:@"Next model required" message:@"Please ask the experimenter to hand you the next physical model." delegate:nil cancelButtonTitle:@"Received it." otherButtonTitles:nil, nil];
            [nextModel show];
            [nextModel release];
        }
        if (training)
        {
            [[NSNotificationCenter defaultCenter] postNotificationName:@"startTraining" object:nil];
            training = NO;
        }
        else
            [[NSNotificationCenter defaultCenter] postNotificationName:@"showNextModel" object:nil];
    }
}

@end
