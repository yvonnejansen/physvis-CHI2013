//
//  TuioObject.m
//  FitzStudyPad
//
//  Created by Yvonne on 8/1/11.
//  Copyright 2011 INRIA AVIZ. All rights reserved.
//

#import "TuioObject.h"

@implementation TuioObject

@synthesize tuioID, sessionID, val1, val2, val3, changed, isAlive, wasAlive, tuioTime, activeTag;

- (id) init
{
    self = [super init];
    if (self)
    {
        val1 = 0;
        val2 = 0;
        val3 = 0;
        activeTag = 0;
        changed = isAlive = wasAlive = NO;
        tuioTime = [NSDate date];
    }
    return self;
}

+ (TuioObject*) objectWithID:(int)objectID
{
    TuioObject *newObject = [[TuioObject alloc] init];
    if (newObject) 
    {
        newObject.tuioID = objectID;
        newObject.sessionID = objectID;
    }
    return [newObject autorelease];
}

- (void) updateToTime:(NSDate*)time val1:(float)value1 val2:(float)value2 val3:(float)value3
{
    [self setTuioTime:time];
    self.val1 = value1;
    self.val2 = value2;
    self.val3 = value3;
}

- (void) updateVal1:(float)value1 val2:(float)value2 val3:(float)value3 active:(float)flag
{
    self.val1 = value1;
    self.val2 = value2;
    self.val3 = value3;
    self.activeTag = flag;
}


@end
