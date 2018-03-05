//
//  TuioObject.h
//  FitzStudyPad
//
//  Created by Yvonne on 8/1/11.
//  Copyright 2011 INRIA AVIZ. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface TuioObject : NSObject {
    int tuioID;
    int sessionID;
    float val1, val2, val3, activeTag;
    bool isAlive, wasAlive, changed;
    NSDate *tuioTime;
}

@property(assign)int tuioID, sessionID;
@property(assign)float val1, val2, val3, activeTag;
@property(assign)bool isAlive, wasAlive, changed;
@property(readwrite,nonatomic,copy)NSDate *tuioTime;

+ (TuioObject*) objectWithID:(int)objectID;
- (void) updateToTime:(NSDate*)time val1:(float)val1 val2:(float)val2 val3:(float)val3;
- (void) updateVal1:(float)value1 val2:(float)value2 val3:(float)value3 active:(float)flag;

@end

