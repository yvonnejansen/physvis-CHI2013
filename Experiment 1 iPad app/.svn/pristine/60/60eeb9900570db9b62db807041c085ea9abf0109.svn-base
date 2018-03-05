//
//  InstructionView.h
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 3/15/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface InstructionView : UIView
{
    UILabel *instructions;
    IBOutlet UIButton *continueButton, *nextButton;
    UILabel *title;
    NSString *currentCondition;
    bool datasetInfo;
    bool training;
    NSArray* imageInstructions;
    UIImageView *instructionImageView;
    int pageNumber;
    bool showingInitialInstructions, showingTrainingInstructions;
}
@property(copy)NSString* currentCondition;

//- (void) displayInstructionsFor:(NSString *)condition;
- (void) displayInstructionsForTraining:(NSString *)condition;
- (IBAction)closeInstructions:(id)sender;
- (void) displayDatasetInfoForTraining:(NSString*)_datasetInfo;
- (void) displayDatasetInfoFor:(NSString*)_datasetInfo;
- (IBAction)nextPage:(id)sender;

@end
