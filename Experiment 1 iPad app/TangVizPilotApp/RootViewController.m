//
//  RootViewController.m
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 1/26/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//
#define BARCHART_2D 1
#define NUM_REPETITIONS 3 // there's another NUM_REPETITIONS in state machine class
#define NUM_CONDITIONS 4

#import "RootViewController.h"

#import "ModelController.h"

#import "DataViewController.h"
#import "TuioSender.h"
#import "TuioObject.h"
#import "StereoTest.h"
#import "ExperimentStateMachine.h"
#import "QuartzCore/QuartzCore.h"


@implementation RootViewController

@synthesize pageViewController = _pageViewController;
@synthesize modelController = _modelController;
@synthesize tuioSender, state, pageCounter, trainingFrame, currentDataViewController, userInfoView, currentConditionName;

- (void)dealloc
{
    [_pageViewController release];
    [_modelController release];
    [super dealloc];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
//    [[UIApplication sharedApplication] setStatusBarHidden:YES animated:NO];
    [super viewDidLoad];
    pageCounter = 0;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(calibration:) name:@"calibration" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(startStereoTest:) name:@"startStereoTest" object:nil];
	// Do any additional setup after loading the view, typically from a nib.
    // Configure the page view controller and add it as a child view controller.
    stepper.stepValue = 1;
    stepper.minimumValue = 1;
    stepper.maximumValue = 12;
    finished = false;
    
    _modelController = [[ModelController alloc] init];

    // init the state machine
    state = [[ExperimentStateMachine alloc] initWithRootViewController:self];
    
    instructionView = [[InstructionView alloc] initWithFrame:self.view.frame];
    datasetInfoView = [[InstructionView alloc] initWithFrame:self.view.frame];
    trainingFrame = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 768, 1024)];
//    trainingFrame.layer.borderWidth = 10;
//    trainingFrame.layer.borderColor = [UIColor blueColor].CGColor;
    trainingFrame.backgroundColor = [UIColor blueColor];
    trainingFrame.hidden = YES;
    UILabel *trainingLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 768, 40)];
    trainingLabel.text = @"TRAINING";
    trainingLabel.font = [UIFont systemFontOfSize:35];
    trainingLabel.textColor = [UIColor whiteColor];
    trainingLabel.backgroundColor = [UIColor clearColor];
    trainingLabel.textAlignment = UITextAlignmentCenter;
    [trainingFrame addSubview:trainingLabel];
    [self.view addSubview:trainingFrame];

    
    // if we're not recovering initialize to 0 here
//    if (!recover)
//    {
//        logger = [[Logger alloc] init];
//        state = [[ExperimentStateMachine alloc] init];
//    
        // start the stereo test only when not in recover
//        [self.view addSubview:stereoTest];
        
//    }

    // prepare alerts
    experimentFinished = [[UIAlertView alloc] initWithTitle:@"Finished" message:@"Thanks for participating!" delegate:state cancelButtonTitle:@"Close" otherButtonTitles:nil, nil];
    experimentFinished.tag = 1;
    
    blockFinished = [[UIAlertView alloc] initWithTitle:@"Block finished" message:@"Press OK to continue." delegate:state cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
    blockFinished.tag = 2;
    
    trialFinished = [[UIAlertView alloc] initWithTitle:@"Trial finished" message:@"Press OK to continue." delegate:state cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
    trialFinished.tag = 3;
    
    questionFinished = [[UIAlertView alloc] initWithTitle:@"Continue to next question" message:nil delegate:state cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
    questionFinished.tag = 4;
    
    startExperiment = [[UIAlertView alloc] initWithTitle:@"Start Experiment" message:@"Press start when you are ready to begin." delegate:state cancelButtonTitle:@"Start" otherButtonTitles:nil, nil];
    startExperiment.tag = 7;

    recover = [[UIAlertView alloc] initWithTitle:@"Recover Experiment" message:@"Press start when you are ready to continue." delegate:state cancelButtonTitle:@"Start" otherButtonTitles:@"delete recover info", nil];
    recover.tag = 5;

    trainingAlert = [[UIAlertView alloc] initWithTitle:@"Training" message:@"Press start when you are ready to begin." delegate:state cancelButtonTitle:@"Start" otherButtonTitles:nil, nil];
    trainingAlert.tag = 9;

    
    block = state.block;
    trial = state.trial;

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(nextQuestion:) name:@"questionFinished" object:nil];

    if (state.recover)
        [self recover];
}

- (bool) isTraining
{
    return training;
}

- (void) setTraining:(bool)t
{
    training = t;
    trainingFrame.hidden = !t;
//    if (t)
//        [instructionView
}

- (void) finishExperiment
{
    [experimentFinished show];
}

- (void)setLogger:(Logger*)_logger
{
    logger = _logger;
}

- (void) startStereoTest:(NSNotification*)notification
{
    if (!stereoTest)
        stereoTest = [[StereoTest alloc] initWithFrame:CGRectMake(0, 0, 550, 700)];
    

    [self.view addSubview:stereoTest];
    stereoTest.center = self.view.center;
}

- (void) recover
{
    [recover show];
    [recover release];
}

- (DataViewController*) startTrial
{
    pageCounter = 0;
//    if (self.pageViewController != nil)
//    {
//        while ([self.pageViewController retainCount] > 1) {
//            [self.pageViewController release];
//        }
//    }
    self.pageViewController = [[[UIPageViewController alloc] initWithTransitionStyle:UIPageViewControllerTransitionStylePageCurl navigationOrientation:UIPageViewControllerNavigationOrientationHorizontal options:nil] autorelease];
    self.pageViewController.delegate = self;
    self.pageViewController.dataSource = self.modelController;
    DataViewController *startingViewController = [self.modelController viewControllerAtIndex:0 storyboard:self.storyboard];
    if (training)
        [startingViewController setTraining:YES];
    startingViewController.logger = logger;
    NSArray *viewControllers = [NSArray arrayWithObject:startingViewController];
    [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:NO completion:NULL];
    
    
    [self addChildViewController:self.pageViewController];
    [self.view addSubview:self.pageViewController.view];
    
    // Set the page view controller's bounds using an inset rect so that self's view is visible around the edges of the pages.
    CGRect pageViewRect = self.view.bounds;
    pageViewRect = CGRectInset(pageViewRect, 40.0, 40.0);
    self.pageViewController.view.frame = pageViewRect;
    
    [self.pageViewController didMoveToParentViewController:self]; 
    
    for (UIGestureRecognizer *r in self.pageViewController.gestureRecognizers) {
        [self.pageViewController.view removeGestureRecognizer:r];
    }
    return startingViewController;

    
}

- (DataViewController*) nextView
{
    DataViewController *currentViewController = [self.pageViewController.viewControllers objectAtIndex:0];
    NSArray *viewControllers = nil;
    UIViewController *nextViewController = [self.modelController pageViewController:self.pageViewController viewControllerAfterViewController:currentViewController];
    if (training)
        [((DataViewController*)nextViewController) setTraining:YES];
    viewControllers = [NSArray arrayWithObjects:nextViewController, nil];
    [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:YES completion:NULL];
    return (DataViewController*) nextViewController;
}

- (DataViewController*) goToPage:(int)_page
{
//    DataViewController *currentViewController = [self.pageViewController.viewControllers objectAtIndex:0];
    NSArray *viewControllers = nil;
    UIViewController *nextViewController = [self.modelController viewControllerAtIndex:_page storyboard:self.storyboard];
    viewControllers = [NSArray arrayWithObject:nextViewController];
    [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:YES completion:nil];
    return (DataViewController*) nextViewController;
}

- (IBAction)changeUserIDLabel:(id)sender
{
    UIStepper *step = (UIStepper *)sender;
    userID.text = [NSString stringWithFormat:@"%.0f", [step value]];
}


- (IBAction)userInfoFinished:(id)sender
{
    [logger setUserInfo:[[NSDictionary dictionaryWithObjectsAndKeys:[userID text], @"userID", [groupPicker titleForSegmentAtIndex:[groupPicker selectedSegmentIndex]], @"groupID", nil] retain]];
    // put groupID into the state controller
    [userInfoView removeFromSuperview];
    [userInfoView release];
    [state setSubjectGroup:[[userID text] intValue]];
    [startExperiment show];
    
}


- (ModelController *)modelController
{
    /*
     Return the model controller object, creating it if necessary.
     In more complex implementations, the model controller may be passed to the view controller.
     */
    if (!_modelController) {
        _modelController = [[ModelController alloc] init];
    }
    return _modelController;
}

#pragma mark - UIPageViewController delegate methods

/*
- (void)pageViewController:(UIPageViewController *)pageViewController didFinishAnimating:(BOOL)finished previousViewControllers:(NSArray *)previousViewControllers transitionCompleted:(BOOL)completed
{
    
}
 */

- (UIPageViewControllerSpineLocation)pageViewController:(UIPageViewController *)pageViewController spineLocationForInterfaceOrientation:(UIInterfaceOrientation)orientation
{
    if (UIInterfaceOrientationIsPortrait(orientation)) {
        // In portrait orientation: Set the spine position to "min" and the page view controller's view controllers array to contain just one view controller. Setting the spine position to 'UIPageViewControllerSpineLocationMid' in landscape orientation sets the doubleSided property to YES, so set it to NO here.
        UIViewController *currentViewController = [self.pageViewController.viewControllers objectAtIndex:0];
        NSArray *viewControllers = [NSArray arrayWithObject:currentViewController];
        [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:YES completion:NULL];
        
        self.pageViewController.doubleSided = NO;
        return UIPageViewControllerSpineLocationMin;
    }

    // In landscape orientation: Set set the spine location to "mid" and the page view controller's view controllers array to contain two view controllers. If the current page is even, set it to contain the current and next view controllers; if it is odd, set the array to contain the previous and current view controllers.
    DataViewController *currentViewController = [self.pageViewController.viewControllers objectAtIndex:0];
    NSArray *viewControllers = nil;

    NSUInteger indexOfCurrentViewController = [self.modelController indexOfViewController:currentViewController];
    if (indexOfCurrentViewController == 0 || indexOfCurrentViewController % 2 == 0) {
        UIViewController *nextViewController = [self.modelController pageViewController:self.pageViewController viewControllerAfterViewController:currentViewController];
        viewControllers = [NSArray arrayWithObjects:currentViewController, nextViewController, nil];
    } else {
        UIViewController *previousViewController = [self.modelController pageViewController:self.pageViewController viewControllerBeforeViewController:currentViewController];
        viewControllers = [NSArray arrayWithObjects:previousViewController, currentViewController, nil];
    }
    [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:YES completion:NULL];


    return UIPageViewControllerSpineLocationMid;
}

- (void) nextQuestion:(NSNotification *)notification
{
// TODO: remove this after debugging
    if (pageCounter + 1 < state.numberOfQuestions)
        [questionFinished show];
    else 
    {    
        if (state.repetition + 1 < NUM_REPETITIONS)
            [trialFinished show];
        else if (state.block + 1 < NUM_CONDITIONS) 
            [blockFinished show];
        else 
            [experimentFinished show];
    }
}

- (void)calibration:(NSNotification *)notification
{
    NSString *action = [notification.userInfo valueForKey:@"action"];
    if ([action isEqualToString:@"start"])
    {
        [self.view addSubview:stereoTest];
        [tuioSender objectValueChanged:7 value:0 x:0 y:0.1];
    }
    else
    {
        [tuioSender objectValueChanged:7 value:0 x:0 y:0.9];
    }
}

- (void) showInstructions:(NSString *)conditionName
{
    trial = state.trial;
    block = state.block;
    currentConditionName = conditionName;
    [instructionView displayInstructionsForTraining:conditionName];
    [self.view addSubview:instructionView];
    
}

- (void) showTrainingInstructions
{
    [instructionView displayInstructionsForTraining:@"training"];
    [self.view addSubview:instructionView];
}

- (void) showModalityInstructions:(NSString*)modality
{
    trial = state.trial;
    block = state.block;
    currentConditionName = modality;
    [instructionView displayInstructionsForTraining:modality];
    [self.view addSubview:instructionView];
}

- (void) showDatasetInfo:(NSString*)datasetInfo
{
    datasetInfoView.currentCondition = currentConditionName;
    if (training)
        [datasetInfoView displayDatasetInfoForTraining:datasetInfo];
    else
    {
//        if ([currentConditionName isEqualToString:@"physical"])
//        {
//            UILabel *callExperimenter = [[UILabel alloc] initWithFrame:CGRectMake(100, 300, 400, 50)];
//            callExperimenter.text = @"Call the experimenter!";
//            callExperimenter.tag = 89;
//            [datasetInfoView addSubview:callExperimenter];
//        }
        [datasetInfoView displayDatasetInfoFor:datasetInfo];
    }
    [self.view addSubview:datasetInfoView];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    [tuioSender release];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
}

- (void)viewWillDisappear:(BOOL)animated
{
	[super viewWillDisappear:animated];
//    for (UIView *view in self.view.subviews)
//    {
//        if (view.tag == 89) {
//            [view removeFromSuperview];
//        }
//    }
}

- (void)viewDidDisappear:(BOOL)animated
{
	[super viewDidDisappear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


@end



