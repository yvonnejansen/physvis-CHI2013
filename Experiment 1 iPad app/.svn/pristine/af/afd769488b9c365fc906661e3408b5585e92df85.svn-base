//
//  TuioSender.h
//  FitzStudyPad
//
//  Created by Yvonne on 8/1/11.
//  Copyright 2011 INRIA AVIZ. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BBOSCSender.h"

@class TuioObject, BBOSCBundle, BBOSCAddress;

@interface TuioSender : BBOSCSender {
    NSMutableArray *activeObjects;
    NSString *sourceName;
    long currentFrame;
    NSDate* currentFrameTime;
    bool updateObject;
    BBOSCBundle *bundle;
    NSTimer *timer;
    BBOSCAddress *address;
    id delegate;
    
}

@property(readwrite,nonatomic,retain)NSMutableArray *activeObjects;
@property(readwrite,assign)bool updateObject;
@property(readwrite,nonatomic,retain)NSString *sourceName;
@property(readwrite,nonatomic,retain)BBOSCBundle *bundle;
@property(readwrite,nonatomic,retain)NSTimer *timer;
@property(copy)NSDate *currentFrameTime;
@property(assign)long currentFrame;
@property(readwrite,nonatomic,retain)BBOSCAddress *address;
@property(assign) id delegate;

- (void) update;
- (void) objectValueChanged:(int)objectID value:(float)val1 x:(float)xVal y:(float)yVal;
- (void) objectStarted:(int)objectID value:(float)val1 x:(float)xVal y:(float)yVal;
- (void) objectEnded:(int)objectID value:(float)val1 x:(float)xVal y:(float)yVal;
- (void) initFrame:(NSDate*)now;
- (void) commitFrame;
- (void) sendEmptyObjectBundle;
- (void) startObjectBundle;
- (void) addObjectMessageForObject:(TuioObject*)obj;
- (void) sendObjectBundleWithSeqNo:(long)fseq;
- (void) updateTuioObject:(TuioObject*)obj val1:(float)val1 val2:(float)val2 val3:(float)val3 active:(float)flag;

@end
