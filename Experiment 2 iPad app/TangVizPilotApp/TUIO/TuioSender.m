//
//  TuioSender.m
//  FitzStudyPad
//
//  Created by Yvonne on 8/1/11.
//  Copyright 2011 INRIA AVIZ. All rights reserved.
//

#import "TuioSender.h"
#import "TuioObject.h"
#import "BBOSCBundle.h"
#import "BBOSCMessage.h"
#import "BBOSCArgument.h"
#import "BBOSCAddress.h"

#define OBJ_MESSAGE_SIZE 108
#define MAX_UDP_PACKET_SIZE 65507

@implementation TuioSender

@synthesize activeObjects, updateObject, sourceName, bundle, timer, currentFrameTime, currentFrame, address, delegate;

+ (id)senderWithDestinationHostName:(NSString*)aName portNumber:(int)aPortNumber
{
	TuioSender *newSender = [[TuioSender alloc] initWithDestinationHostName:aName portNumber:aPortNumber];

    if (newSender)
    {
        newSender.activeObjects = [[NSMutableArray alloc] init];
        for (int i = 0; i < 12; i++)
        {
            TuioObject *object = [TuioObject objectWithID:i];
            [newSender.activeObjects addObject:[object retain]];
        }
        newSender.currentFrame = 0;
        newSender.updateObject = NO;
        newSender.address = [BBOSCAddress addressWithString:@"/tuio/2Dobj"];
        newSender.sourceName = @"TuioPad@169.254.78.183";
//        newSender.timer = [NSTimer timerWithTimeInterval:1 target:newSender selector:@selector(update) userInfo:nil repeats:YES];
//        [[NSRunLoop mainRunLoop] addTimer:newSender.timer forMode:NSDefaultRunLoopMode];

    }
    return [newSender autorelease];
            
}


- (void) update
{
//    NSLog(@"update");
    if (currentFrameTime == [NSDate date]) return;
    [self initFrame:[NSDate date]];
    for (TuioObject *obj in activeObjects)
    {
        if (obj.changed)
        {
            [self updateTuioObject:obj val1:obj.val1 val2:obj.val2 val3:obj.val3 active:obj.activeTag];
            updateObject = YES;
        }
    }
    [self commitFrame];
}



// called by tuio objects
- (void) objectValueChanged:(int)objectID value:(float)val1 x:(float)xVal y:(float)yVal
{
    TuioObject *obj = [activeObjects objectAtIndex:objectID];
    if (val1 != obj.val1) // || val2 != obj.val2)
    {
        obj.changed = YES;
        obj.val1 = val1;
        obj.val2 = xVal;
        obj.val3 = yVal;
        [self update];
    }
}

- (void) objectEnded:(int)objectID value:(float)val1 x:(float)xVal y:(float)yVal
{
    TuioObject *obj = [activeObjects objectAtIndex:objectID];
//    if (val1 != obj.val1) // || val2 != obj.val2)
    {
        obj.changed = YES;
        obj.val1 = val1;
        obj.val2 = xVal;
        obj.val3 = yVal;
        obj.activeTag = 0;
        [self update];
    }
//    NSLog(@"object with id %i ended", objectID);

}


- (void) objectStarted:(int)objectID value:(float)val1 x:(float)xVal y:(float)yVal
{
    TuioObject *obj = [activeObjects objectAtIndex:objectID];
//    if (val1 != obj.val1) // || val2 != obj.val2)
    {
        obj.changed = YES;
        obj.val1 = val1;
        obj.val2 = xVal;
        obj.val3 = yVal;
        obj.activeTag = 1;
        [self update];
    }
    
}

// called by update
- (void) updateTuioObject:(TuioObject*)obj val1:(float)val1 val2:(float)val2 val3:(float)val3 active:(float)flag
{
    if (!obj) return;
    [obj updateVal1:val1 val2:val2 val3:val3 active:flag];
    self.updateObject = YES;
//    NSLog(@"updated object with id %i to %f, %f, %f, %f", obj.tuioID, val1, val2, val3, flag);
}


- (void) initFrame:(NSDate*)now
{
    currentFrameTime = now;
    currentFrame++;
}


- (void) commitFrame
{
    if (updateObject)
    {
        [self startObjectBundle];
        for (TuioObject* obj in activeObjects)
        {
            // if we can't fit another message into this packet send it and start a new bundle
            if (MAX_UDP_PACKET_SIZE - [bundle size] < OBJ_MESSAGE_SIZE)
            {
                [self sendObjectBundleWithSeqNo:currentFrame];
                [self startObjectBundle];
            }
            if (obj.changed) 
            {
                [self addObjectMessageForObject:obj];
                obj.changed = NO;
            }
        }
        [self sendObjectBundleWithSeqNo:currentFrame];
        updateObject = NO;
    }
    else
    {
        [self sendEmptyObjectBundle];
    }
}


- (void) sendEmptyObjectBundle
{
    [self startObjectBundle];
    BBOSCMessage *message = [BBOSCMessage messageWithBBOSCAddress:address];
    [message attachArgument:[BBOSCArgument argumentWithString:@"fseq"]];
    [message attachArgument:[BBOSCArgument argumentWithInt:-1]];
    
    [bundle attachObject:message];
//    [[delegate commClient] sendToAllServers:[bundle packetizedData]];
    [self sendOSCPacket:bundle];
}


- (void) startObjectBundle
{
    bundle = [BBOSCBundle bundleWithTimestamp:currentFrameTime];
    BBOSCMessage *aliveMessage = [BBOSCMessage messageWithBBOSCAddress:address];
    [aliveMessage attachArgument:[BBOSCArgument argumentWithString:[NSString stringWithFormat:@"source%@", sourceName]]];
    [aliveMessage attachArgument:[BBOSCArgument argumentWithString:[NSString stringWithFormat:@"alive"]]];
    for (TuioObject *obj in activeObjects) {
        [aliveMessage attachArgument:[BBOSCArgument argumentWithInt:obj.sessionID]];
    }
    [bundle attachObject:aliveMessage];
}


- (void) addObjectMessageForObject:(TuioObject*)obj
{
    BBOSCMessage *message = [BBOSCMessage messageWithBBOSCAddress:address];
    [message attachArgument:[BBOSCArgument argumentWithString:@"set"]];
    [message attachArgument:[BBOSCArgument argumentWithInt:obj.sessionID]];
    [message attachArgument:[BBOSCArgument argumentWithInt:obj.tuioID]];
    [message attachArgument:[BBOSCArgument argumentWithFloat:obj.val2]];
    [message attachArgument:[BBOSCArgument argumentWithFloat:obj.val3]];
    [message attachArgument:[BBOSCArgument argumentWithFloat:obj.val1]];
    [message attachArgument:[BBOSCArgument argumentWithFloat:obj.activeTag]];
    [message attachArgument:[BBOSCArgument argumentWithFloat:0]];
    [message attachArgument:[BBOSCArgument argumentWithFloat:0]];
    [message attachArgument:[BBOSCArgument argumentWithFloat:0]];
    [message attachArgument:[BBOSCArgument argumentWithFloat:0]];
    
    [bundle attachObject:message];
}


- (void) sendObjectBundleWithSeqNo:(long)fseq
{
    BBOSCMessage *message = [BBOSCMessage messageWithBBOSCAddress:address];
    [message attachArgument:[BBOSCArgument argumentWithString:[NSString stringWithFormat:@"fseq"]]];
    [message attachArgument:[BBOSCArgument argumentWithInt:fseq]];
    
    [bundle attachObject:message];
//    [[delegate commClient] sendToAllServers:[bundle packetizedData]];
    [self sendOSCPacket:bundle];
//    NSLog(@"send packet");
}



@end
