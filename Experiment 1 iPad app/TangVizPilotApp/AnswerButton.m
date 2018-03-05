//
//  AnswerButton.m
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 2/3/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import "AnswerButton.h"

@implementation AnswerButton

+ (AnswerButton*) buttonWithType:(UIButtonType)buttonType
{
    AnswerButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
    [[button layer] setBorderColor:[[UIColor blackColor] CGColor]];
    [[button layer] setBorderWidth:1];
    [[button layer] setBackgroundColor:[[UIColor lightGrayColor] CGColor]];
    
    return [button autorelease];
    
}

- (id) initWithFrame:(CGRect)frame
{
    if (self)
    {
        [super initWithFrame:frame];
        [[self layer] setBackgroundColor:[[UIColor lightGrayColor] CGColor]];

    }
    return self;
}

- (id) initWithCoder:(NSCoder *)aDecoder
{
    if (self)
    {
        [super initWithCoder:aDecoder];
//        NSLog(@"the button coder \n %@", [[aDecoder decodeRect] description]);
        [[self layer] setBorderColor:[[UIColor blackColor] CGColor]];
        [[self layer] setBorderWidth:1];

        
    }
    return self;
}

- (void) endTrackingWithTouch:(UITouch *)touch withEvent:(UIEvent *)event
{
    [super endTrackingWithTouch:touch withEvent:event];
    if (self.selected) self.highlighted = YES;
}


- (void) touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    [super touchesEnded:touches withEvent:event];
    if (self.selected) [self setHighlighted:YES];

}
@end
