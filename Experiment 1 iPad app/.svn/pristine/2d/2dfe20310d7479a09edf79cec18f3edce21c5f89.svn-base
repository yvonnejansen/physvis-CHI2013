//
//  DataViewController.m
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 1/26/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import "DataViewController.h"
#import "QuartzCore/QuartzCore.h"
#import "MetaDataReader.h"

@implementation DataViewController

@synthesize dataLabel = _dataLabel;
@synthesize dataObject = _dataObject;
@synthesize a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, done, numViewC, slider, slider2, datasetName, reminderLabel, logger;

- (void)dealloc
{
    [_dataLabel release];
    [_dataObject release];
    [buttons release];
    [visImage release];
    [timer release];
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
    [super viewDidLoad];
    self.view.gestureRecognizers = nil;
    answers = [self.dataObject objectForKey:@"answers"];
    buttons = [[NSArray alloc] initWithObjects:a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, nil];
    NSString *meta = [NSString stringWithContentsOfFile:[[NSBundle mainBundle] pathForResource:datasetName ofType:@"metadata"] usedEncoding:nil error:nil];
    metadata = [[MetaDataReader alloc] initWithString:meta];
    for (int i = 0; i < buttons.count; i++) {
//        ((UIButton*)[buttons objectAtIndex:i]).backgroundColor = [metadata.colors objectAtIndex:i];
        ((UIButton*)[buttons objectAtIndex:i]).adjustsImageWhenDisabled = YES;
        ((UIButton*)[buttons objectAtIndex:i]).adjustsImageWhenHighlighted =YES;
    }
    UILabel *datasetLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 900, self.view.frame.size.width, 35)];
    datasetLabel.text = metadata.title;
    datasetLabel.font = [UIFont systemFontOfSize:20];
    datasetLabel.textAlignment = UITextAlignmentCenter;
    datasetLabel.backgroundColor = [UIColor clearColor];
//    [self.view addSubview:datasetLabel];

    NSString *conditionName = logger.currentCondition;
    
    startTime = [[NSDate date] retain];
//    [[NSNotificationCenter defaultCenter] postNotificationName:@"questionDisplayed" object:nil userInfo:[[NSDictionary dictionaryWithObjectsAndKeys:[[NSDate date] retain], @"time", [self.dataObject valueForKey:@"type"], @"type", [self.dataObject valueForKey:@"question"], @"question", nil] retain]];
    
    UIButton *showButtons = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    showButtons.frame = CGRectMake(228, 810, 268, 84);
    showButtons.layer.borderWidth = 3;
    showButtons.layer.cornerRadius = 10;
    showButtons.layer.backgroundColor = [UIColor blueColor].CGColor;
    
//    CGRect frame = self.view.frame;
//    CALayer *pulseLayer = [[CALayer alloc] init];
    
    [UIView animateWithDuration:1.0
                          delay:0
                        options://UIViewAnimationOptionAllowAnimatedContent | 
     UIViewAnimationOptionAllowUserInteraction | 
     UIViewAnimationOptionAutoreverse | 
     UIViewAnimationOptionCurveEaseInOut | UIViewAnimationOptionRepeat
                     animations:^{
                         showButtons.alpha = 0.1;
                     }
                     completion:^(BOOL finished) {
                         
                     }];
             
             
    indexOfRadioButton = -1;
             
             
//    // create the animation that will handle the pulsing.
//    CABasicAnimation* pulseAnimation = [CABasicAnimation animation];
//    
//    // the attribute we want to animate is the inputIntensity
//    // of the pulseFilter
//    pulseAnimation.keyPath = @"inputIntensity";
//    
//    // we want it to animate from the value 0 to 1
//    pulseAnimation.fromValue = [NSNumber numberWithFloat: 0.0];
//    pulseAnimation.toValue = [NSNumber numberWithFloat: 1.5];
//    
//    // over a one second duration, and run an infinite
//    // number of times
//    pulseAnimation.duration = 1.0;
//    pulseAnimation.repeatCount = HUGE_VALF;
//    
//    // we want it to fade on, and fade off, so it needs to
//    // automatically autoreverse.. this causes the intensity
//    // input to go from 0 to 1 to 0
//    pulseAnimation.autoreverses = YES;
//    
//    // use a timing curve of easy in, easy out..
//    pulseAnimation.timingFunction = [CAMediaTimingFunction functionWithName: kCAMediaTimingFunctionEaseInEaseOut];
//    
//    // add the animation to the selection layer. This causes
//    // it to begin animating. We'll use pulseAnimation as the
//    // animation key name
////    [pulseLayer addAnimation:pulseAnimation forKey:@"pulseAnimation"];
//    
//    
//    [showButtons.layer addAnimation:pulseAnimation forKey:@"pulseAnimation"];
    
    
    
    
    
//    pulseLayer.frame = CGRectInset(frame, -20, -20);
//    pulseLayer.backgroundColor = [UIColor blueColor].CGColor;
//    CABasicAnimation *pulseA = [CABasicAnimation animationWithKeyPath:@"opacity"];
//    pulseA.fromValue = [NSNumber numberWithFloat:1];
//    pulseA.toValue = [NSNumber numberWithFloat:0.1];
//    pulseA.duration = 1;
//    pulseA.repeatCount = 0;
//    pulseA.autoreverses = YES;
//    [pulseLayer addAnimation:pulseA forKey:@"pulsing"];
    [showButtons addTarget:self action:@selector(showButtons:) forControlEvents:UIControlEventTouchUpInside];
    [showButtons setTitle:@"Start" forState:UIControlStateNormal];
    [showButtons setFont:[UIFont systemFontOfSize:35]];
    if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"number"])
    {
        numViewC = [[NumberPadViewController alloc] init];
        numViewC.view.frame = CGRectOffset(numViewC.view.frame, (768 - numViewC.view.frame.size.width) / 2, 250);
        [self addChildViewController:numViewC];
    }
    rank = [[self.dataObject valueForKey:@"type"] isEqualToString:@"rank"];
    if (rank) rankedButtons = [[NSMutableArray alloc] init];
    rankCounter = 1;

    [self.view addSubview:showButtons];
    self.view.clipsToBounds = NO;
    
    if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"bar"]){
        slider = [[BarSlider alloc] initWithFrame:CGRectMake(100, 480, 480, 60) metadata:metadata];
        slider.transform = CGAffineTransformMakeRotation(-M_PI_2);
        [slider addTarget:self action:@selector(sliderDown:) forControlEvents:UIControlEventTouchDown];
        [slider addTarget:self action:@selector(sliderUp:) forControlEvents:UIControlEventTouchUpInside];
        [slider retain];
    }
    if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"two-bar"]){
        slider = [[BarSlider alloc] initWithFrame:CGRectMake(10, 480, 480, 60) metadata:metadata];
        slider.transform = CGAffineTransformMakeRotation(-M_PI_2);
//        [slider retain];
        slider2 = [[BarSlider alloc] initWithFrame:CGRectMake(250, 480, 480, 60) metadata:metadata];
        slider2.transform = CGAffineTransformMakeRotation(-M_PI_2);
//        [slider2 retain];
        min = [[UILabel alloc] initWithFrame:CGRectMake(225, 780, 100, 25)];
        min.text = @"minimum";
//        [min retain];
        max = [[UILabel alloc] initWithFrame:CGRectMake(415, 780, 100, 25)];
        max.text = @"maximum";
//        [max retain];
        [self.view addSubview:slider];        
        [self.view addSubview:slider2]; 
//        [slider addTarget:self action:@selector(answer:) forControlEvents:UIControlEventTouchDown];
        [slider addTarget:self action:@selector(sliderUp:) forControlEvents:(UIControlEventTouchUpInside | UIControlEventTouchUpOutside)];
        [slider addTarget:self action:@selector(sliderDown:) forControlEvents:UIControlEventTouchDown];
//        [slider2 addTarget:self action:@selector(answer:) forControlEvents:UIControlEventTouchDown];
        [slider2 addTarget:self action:@selector(sliderUp:) forControlEvents:(UIControlEventTouchUpInside | UIControlEventTouchUpOutside)];
        [slider2 addTarget:self action:@selector(sliderDown:) forControlEvents:UIControlEventTouchDown];
        [self.view addSubview:min];
        [self.view addSubview:max];
        slider.hidden = slider2.hidden = min.hidden = max.hidden = YES;

    }
//    [self.view addSubview:slider];
    
//    [self.view addSubview:answerView.view];

    done.clipsToBounds = YES;
    done.layer.borderWidth = 1;
    done.layer.borderColor = [[UIColor blackColor] CGColor];
    done.layer.cornerRadius = 9;

    
    CGRect timerFrame = CGRectMake(260, 0, 120, 40);
    timer = [[TimeCounter alloc] initWithFrame:timerFrame]; //CGRectMake(250,0,90,35)];//(self.view.frame.size.width-250, 250, 120, 50)];
    [showButtons addTarget:timer action:@selector(startCounter:) forControlEvents:UIControlEventTouchUpInside];
    [[done superview] addSubview:timer];
    [timer setHidden:YES];

    self.reminderLabel.text = @"Remember: don't look at the model before pressing \"Start\"!";
    self.reminderLabel.numberOfLines = 0;
    self.reminderLabel.font = [UIFont systemFontOfSize:32];
    self.reminderLabel.textColor = [UIColor redColor];
//    self.reminderLabel.backgroundColor = [UIColor blueColor];
    
//    self.answerTable.delegate = self;
	// Do any additional setup after loading the view, typically from a nib.
}

- (void) showVisualizationImageForCondition:(NSString*)condition
{
    if ([condition isEqualToString:@"physical"]) {
        visImage = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"physicalVisualization"]];
//        visImage.transform = CGAffineTransformMakeScale(0.2, 0.2);
    }
    else if ([condition isEqualToString:@"2D"]) {
        visImage = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"2DVisualization"]];
//        visImage.transform = CGAffineTransformMakeScale(0.4, 0.4);
    }
    else if ([condition isEqualToString:@"mono"]) {
        visImage = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"monoVisualization"]];
//        visImage.transform = CGAffineTransformMakeScale(0.2, 0.2);
    }
    else if ([condition isEqualToString:@"stereo"]) {
        visImage = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"stereoVisualization"]];
//        visImage.transform = CGAffineTransformMakeScale(0.2, 0.2);
    }
    visImage.center = CGPointMake(self.view.center.x, self.view.center.y+90);
    
    [self.view addSubview:visImage];

}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
    [startTime release];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
//    self.answerTable.dataSource = [self.dataObject objectForKey:@"countries"];
//    NSLog(@"number of answers: %i", [self.answerTable.dataSource count]);
    self.dataLabel.text = [self.dataObject valueForKey:@"question"];
    [self.view addSubview:visImage];
//    [self.answerTable reloadData];
//    for (NSString *country in self.answerTable){
        
    
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
//    NSLog(@"showing data view for %@", [self.dataObject valueForKey:@"question"]);
//    startTime = [[NSDate date] retain];
}

- (void)viewWillDisappear:(BOOL)animated
{
	[super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
	[super viewDidDisappear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return YES;
}

- (void) registerLogger:(Logger*) _logger
{
    logger = _logger;
    if (!training)
        [logger startNewQuestion:[[NSDictionary dictionaryWithObjectsAndKeys:[[NSDate date] retain], @"time", [self.dataObject valueForKey:@"type"], @"type", [self.dataObject valueForKey:@"name"], @"task",[self.dataObject valueForKey:@"question"], @"question", [self.dataObject objectForKey:@"correct answer"], @"correct answer", nil] retain]];

}

- (bool) isTraining
{
    return training;
}

- (void) setTraining:(bool)t
{
    training = t;
}


- (IBAction)answer:(id)sender
{
    
    ((UIButton *)sender).selected = ((UIButton *)sender).selected ? NO : YES;
//    NSLog(@"highlighted button %i", ((UIButton *)sender).selected);
    NSTimeInterval time = [startTime timeIntervalSinceNow];
    NSString *button = ((UIButton *)sender).currentTitle;
    if (!(((UIButton*)sender) == done))
    {
        NSString *answerType = ((UIButton *)sender).selected ? @"answer" : @"correction";
    
        if (rank) {
            if (!training)
                [logger buttonPressed:[[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", button, answerType, [NSNumber numberWithInt:rankCounter], @"rank", nil]];
            if ([answerType isEqualToString:@"answer"]) {
                [((UIButton *)sender) setTitle:[NSString stringWithFormat:@"%i - %@", rankCounter, button] forState:UIControlStateNormal];
                if ([rankedButtons count] > 0) 
                { 
                    [[rankedButtons lastObject] setEnabled:NO];
                    ((UIButton*)[rankedButtons lastObject]).layer.borderWidth = 4;
                }
                [rankedButtons addObject:sender];
//                ((UIButton*)[rankedButtons lastObject]).transform = CGAffineTransformMakeScale(([rankedButtons count] + 4) / (float)[buttons count], 1);
                ((UIButton*)[rankedButtons lastObject]).layer.borderWidth = 8;
                rankCounter++;
                if (rankedButtons.count == buttons.count)
                {
                    done.enabled = YES;
                }
            }
            else {
                rankCounter--;
                ((UIButton*)[rankedButtons lastObject]).transform = CGAffineTransformIdentity;
                ((UIButton*)[rankedButtons lastObject]).layer.borderWidth = 1;

                [rankedButtons removeObject:sender];
                [[rankedButtons lastObject] setEnabled:YES];
                ((UIButton*)[rankedButtons lastObject]).layer.borderWidth = 8;
                int index = [buttons indexOfObject:((UIButton *)sender)];
                [((UIButton *)[buttons objectAtIndex:index]) setTitle:[answers objectAtIndex:index] forState:UIControlStateNormal];

            }
            
        }
        else 
        {
            if (!training)
                [logger buttonPressed:[[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", button, answerType, nil]];
            ((UIButton *)sender).layer.borderWidth = ((UIButton *)sender).selected ? 8 : 1;
            ((UIButton *)sender).transform = ((UIButton *)sender).selected ? CGAffineTransformTranslate(CGAffineTransformMakeScale(1, 0.95), 7, 1) : CGAffineTransformIdentity;
//            if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"multi"])
//            {
//                int senderIndex = [buttons indexOfObject:((UIButton*)sender)];
//                ((UIButton *)sender).layer.backgroundColor = ((UIButton *)sender).selected ? [UIColor blackColor].CGColor : ((UIColor*)[metadata.colors objectAtIndex:senderIndex]).CGColor;
//                [((UIButton *)sender) setTitleColor:[UIColor whiteColor] forState:UIControlStateSelected];
//            }
        }  
        
        //        [[NSNotificationCenter defaultCenter] postNotification:[NSNotification notificationWithName:@"buttonPress" object:sender userInfo:[[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", button, answerType, nil]]];
    }
    if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"number"])
    {
        if (!training)
            [logger buttonPressed:[[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", [NSNumber numberWithFloat:[numViewC getNumber]], @"value", nil]];
//        [[NSNotificationCenter defaultCenter] postNotification:[NSNotification notificationWithName:@"buttonPress" object:sender userInfo:[[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", [NSNumber numberWithFloat:[numViewC getNumber]], @"value", nil]]];

    }
    else if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"bar"])
    {
        if (!training)
            [logger buttonPressed:[[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", [NSNumber numberWithFloat:[slider getNumber]], @"value", nil]];
        //        [[NSNotificationCenter defaultCenter] postNotification:[NSNotification notificationWithName:@"buttonPress" object:sender userInfo:[[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", [NSNumber numberWithFloat:[numViewC getNumber]], @"value", nil]]];
        
    }
//    else if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"two-bar"])
//    {
//        if (!training)
//            [logger buttonPressed:[[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", [NSNumber numberWithFloat:[slider getNumber]], @"min value",[NSNumber numberWithFloat:[slider2 getNumber]], @"max value", [slider getMaxAxis], @"maxAxisValue", nil]];
//        //        [[NSNotificationCenter defaultCenter] postNotification:[NSNotification notificationWithName:@"buttonPress" object:sender userInfo:[[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", [NSNumber numberWithFloat:[numViewC getNumber]], @"value", nil]]];
//        
//    }
    if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"radio"])
    {
        int newIndex = [buttons indexOfObject:((UIButton*)sender)];
        if (indexOfRadioButton == -1) {
//            [logger buttonPressed:[[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", button, @"buttonPressed", nil]];
            indexOfRadioButton = newIndex;
            done.enabled = YES;
        }
        else if (newIndex != indexOfRadioButton) {
//            [logger buttonPressed:[[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", button, @"buttonPressed", nil]];
            ((UIButton*)[buttons objectAtIndex:indexOfRadioButton]).transform = CGAffineTransformIdentity;
            ((UIButton*)[buttons objectAtIndex:indexOfRadioButton]).layer.borderWidth = 1;
            indexOfRadioButton = newIndex;
            done.enabled = YES;
        }
        else
        {    
            ((UIButton*)sender).transform = CGAffineTransformIdentity;
            ((UIButton*)sender).layer.borderWidth = 1;
            indexOfRadioButton = -1;
            done.enabled = NO;
        }
    }
    
    if ((UIButton*)sender == done)
    {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"questionFinished" object:nil];
        if (!training)
        {
            if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"two-bar"])
            {
                [logger buttonPressed:[[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", [NSNumber numberWithFloat:[slider getNumber]], @"min value",[NSNumber numberWithFloat:[slider2 getNumber]], @"max value", [slider getMaxAxis], @"maxAxisValue", nil]];

            }
            [logger questionDone:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", @"done", @"buttonPressed", nil]];
        }
        
        [timer stopCounter:nil];
        if ([[self.dataObject valueForKey:@"name"] isEqualToString:@"choice"])
            [self.view removeFromSuperview];
    }
    
}

- (IBAction)sliderUp:(id)sender
{
    NSTimeInterval time = [startTime timeIntervalSinceNow];
    if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"two-bar"])
    {
        
        if (sender == slider)
        {
            if (!training)
                [logger buttonPressed:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", @"minValueEnded", @"event", [NSNumber numberWithFloat:[slider getNumber]], @"min value", nil]];
        }
        else if (sender == slider2)
        {
            if (!training)
                [logger buttonPressed:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", @"maxValueEnded", @"event", [NSNumber numberWithFloat:[slider2 getNumber]], @"max value",nil]];
        }
        
    }
    else if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"bar"])
    {
        if (!training)
            [logger buttonPressed:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", @"valueEnded", @"event", nil]];
    }
    done.enabled = YES;
    
}

- (IBAction)sliderDown:(id)sender
{
    NSTimeInterval time = [startTime timeIntervalSinceNow];
    if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"two-bar"])
    {
        
        if (sender == slider)
        {
            if (!training)
            [logger buttonPressed:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", @"minValueStarted", @"event", nil]];
        }
        else if (sender == slider2)
        {
            if (!training)
                [logger buttonPressed:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", @"maxValueStarted", @"event", nil]];
        }
            
    }
    if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"bar"])
    {
        if (!training)
            [logger buttonPressed:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithDouble:-time], @"time", @"valueStarted", @"event", nil]];
    }
    done.enabled = YES;

}


- (IBAction)showButtons:(id)sender
{
    [((UIButton*)sender) setHidden:true];
    [self.reminderLabel setHidden:YES];
    [visImage removeFromSuperview];
    [timer setHidden:NO];
    startTime = [[NSDate date] retain];
    if (!training)
        [logger timerStarted:[[NSDictionary dictionaryWithObjectsAndKeys:[[NSDate date] retain], @"time", nil] retain]];

    if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"number"])
    {
        [self.view addSubview:numViewC.view];
//        [self.view addSubview:slider];        
    }
    else if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"bar"])
    {
        [self.view addSubview:slider]; 
        [slider addTarget:self action:@selector(sliderDown:) forControlEvents:UIControlEventTouchDown];
        [slider addTarget:self action:@selector(sliderUp:) forControlEvents:(UIControlEventTouchUpInside | UIControlEventTouchUpOutside)];
    }
    else if ([[self.dataObject valueForKey:@"type"] isEqualToString:@"two-bar"])
    {
        slider.hidden = slider2.hidden = min.hidden = max.hidden = NO;
    }

    else
    {
        CGRect startRect = ((UIButton *)[buttons objectAtIndex:0]).frame;
        CGFloat buttonHeight = startRect.size.height;
//        NSLog(@"using colors %@", [[self.dataObject objectForKey:@"useColors"] boolValue] ? @"YES" : @"NO");
        if ([[self.dataObject objectForKey:@"useColors"] boolValue]) {
//            NSArray *colors = [self.dataObject objectForKey:@"colors"];
//            NSNumberFormatter *numTest = [[NSNumberFormatter alloc] init];
//            NSLog(@"number from first object %f", [[numTest numberFromString:[answers objectAtIndex:0]] floatValue]);
//            if ([numTest numberFromString:[answers objectAtIndex:0]] ) {
//                for (int i = 0; i < answers.count; i++) {
////                    NSLog(@"setting background color to %@", [metadata.colors objectAtIndex:i]);
//                }
////            }
        }
        bool color = [[self.dataObject objectForKey:@"useColors"] boolValue];
        for (int i = 0; i < [answers count]; i++) {
            UIButton *b = (UIButton *)[buttons objectAtIndex:i];
            if (color)
                ((UIButton*)[buttons objectAtIndex:i]).layer.backgroundColor = ((UIColor*)[metadata.colors objectAtIndex:i]).CGColor;
            
            b.frame = CGRectOffset(startRect, 0, i * (buttonHeight + 2));
            [b setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
            [b setTitle:[answers objectAtIndex:i] forState:UIControlStateNormal];
            [b addTarget:self action:@selector(answer:) forControlEvents:UIControlEventTouchUpInside];
//            [b setBackgroundImage:[UIImage imageNamed:@"buttonBackgroundSelected"] forState:UIControlStateDisabled];
//            [b setBackgroundColor:[UIColor clearColor]];
//            [b setBackgroundImage:[UIImage imageNamed:@"buttonBackgroundSelected"] forState:UIControlStateSelected];
            [b setHidden:NO];
            b.clipsToBounds = YES;
            b.layer.borderWidth = 1;
            b.layer.borderColor = [[UIColor blackColor] CGColor];
            b.layer.cornerRadius = 9;
        }
        done.center = CGPointMake(done.center.x, ((UIButton *)[buttons objectAtIndex:answers.count-1]).center.y + done.frame.size.height + 3);
    }
    [done setHidden:NO];
//    if (!([[self.dataObject valueForKey:@"type"] isEqualToString:@"multi"] || [[self.dataObject valueForKey:@"type"] isEqualToString:@"two-bar"]))
        [done setEnabled:NO];
//    NSLog(@"actions for slider: %@", [slider actionsForTarget:self forControlEvent:(UIControlEventTouchDown | UIControlEventTouchUpInside | UIControlEventTouchUpOutside)]);
//    NSLog(@"actions for slider2: %@", [slider2 actionsForTarget:self forControlEvent:(UIControlEventTouchDown | UIControlEventTouchUpInside | UIControlEventTouchUpOutside)]);

}

@end
