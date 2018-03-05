//
//  NumberPickerViewController.m
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 3/19/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import "NumberPickerViewController.h"

@implementation NumberPickerViewController

@synthesize firstDigit, secondDigit, thirdDigit;

//- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
//{
//    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
//    if (self) {
//        firstDigit = [UIPickerView alloc] 
//        // Custom initialization
//    }
//    return self;
//}

- (id) init
{
    if (self)
    {
        yOff = 400;
        width = 80;
        height = 200;
        distance = 5;
        xOff = (768 - 3 * width - 2 * distance + 20) / 2;
        
        self.view.frame = CGRectMake(xOff, yOff, 3 * width + 2 * distance,height);
        numbers = [[NSArray arrayWithObjects:@"0", @"1", @"2", @"3", @"4", @"5", @"6", @"7", @"8", @"9", nil] retain];
        digit1 = digit2 = digit3 = precision = 0;
        firstDigit = [[UIPickerView alloc] initWithFrame:CGRectMake(0, 0, width, height)];
        secondDigit = [[UIPickerView alloc] initWithFrame:CGRectMake(width + distance, 0, width, height)];
        thirdDigit = [[UIPickerView alloc] initWithFrame:CGRectMake(2 * width + 2* distance, 0, width, height)];
        
        firstDigit.delegate = self;
        secondDigit.delegate = self;
        thirdDigit.delegate = self;
        
        firstDigit.showsSelectionIndicator = YES;
        secondDigit.showsSelectionIndicator = YES;
        thirdDigit.showsSelectionIndicator = YES;
        
        [self.view addSubview:firstDigit];
        [self.view addSubview:secondDigit];
        [self.view addSubview:thirdDigit];
    }
    
    return self;
}

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)thePickerView {
    
    return 1;
}

- (NSInteger)pickerView:(UIPickerView *)thePickerView numberOfRowsInComponent:(NSInteger)component {
    
    return [numbers count];
}

- (NSString *)pickerView:(UIPickerView *)thePickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component {
    return [numbers objectAtIndex:row];
}

- (void)pickerView:(UIPickerView *)thePickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component {
    
    if (thePickerView == firstDigit)
        digit1 = row;
    else if (thePickerView == secondDigit)
        digit2 = row;
    else if (thePickerView == thirdDigit)
        digit3 = row;
}

- (float) getCurrentNumber
{
    number = digit3 + 10 * digit2 + 100 * digit3;
    if (precision != 0)
        number = number / powl(10, precision);
    return number;
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView
{
}
*/

/*
// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
}
*/

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
	return YES;
}

@end
