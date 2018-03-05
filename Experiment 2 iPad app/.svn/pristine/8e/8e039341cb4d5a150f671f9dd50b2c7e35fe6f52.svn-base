//
//  NumberPadViewController.m
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 3/20/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import "NumberPadViewController.h"

@implementation NumberPadViewController



- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        [point1 setHidden:YES];
        [point2 setHidden:YES];
    }
    return self;
}

- (float) getNumber
{
    float currentFloat;
    currentFloat = (dig1 + 10 * dig2 + 100 * dig3) / powl(10, prec);
    return currentFloat;
}

- (IBAction)buttonPressed:(id)sender
{
    NSString *num = ((UIButton*)sender).titleLabel.text;
    if (digit3.selected) 
    {   
        digit3.text = num;
        dig3 = [num intValue];
        digit3.selected = NO;
        digit2.selected = YES;
    }
    else if (digit2.selected) 
    {   
        digit2.text = num;
        dig2 = [num intValue];
        digit2.selected = NO;
        digit1.selected = YES;
    }
    else if (digit1.selected) 
    {   
        digit1.text = num;
        dig1 = [num intValue];
        digit1.selected = NO;
    }

}

- (IBAction)pointPressed:(id)sender
{
    if (digit2.selected) {
        [point2 setHidden:NO];
        prec = 2;
        if (!point1.hidden) {
            [point1 setHidden:YES];
        }
    } 
    else if (digit1.selected) {
        [point1 setHidden:NO];
        prec = 1;
        if (!point2.hidden) {
            [point2 setHidden:YES];
        }
    }
}


- (IBAction)backspace:(id)sender
{
   if (digit1.selected)
   {
       if (point1.hidden) {
           digit2.text = @"–";
           digit2.selected = YES;
           digit1.selected = NO;
       }
        else {
            [point1 setHidden:YES];
            prec = 0;
        }
   }
   else if (digit2.selected)
   {
       if (point2.hidden)
       {
           digit3.text = @"–";
           digit3.selected = YES;
           digit2.selected = NO;
       }
       else 
       {
           [point2 setHidden:YES];
           prec = 0;
       }
   }
   else if (digit3.selected)
   {
       digit3.text = @"–";
   }
   else
   {
       digit1.text = @"–";
       digit1.selected = YES;
   }


}


- (void) setPrecision:(int) _prec
{
    prec = _prec;
    switch (prec) {
        case 0:
            [point1 setHidden:YES];
            [point2 setHidden:YES];
            break;
        case 1:
            [point1 setHidden:NO];
            [point2 setHidden:YES];
            break;
        case 2:
            [point1 setHidden:YES];
            [point2 setHidden:NO];
            break;
        default:
            break;
    }
    
}


- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

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
