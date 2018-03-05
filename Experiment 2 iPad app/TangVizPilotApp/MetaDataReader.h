//
//  MetaDataReader.h
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 4/7/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface MetaDataReader : NSObject
{
    NSMutableString *theData;
    NSMutableArray *ticks;
    NSMutableArray *colors;
    float minAxisValue, maxAxisValue;
    NSString * title;
}

@property float minAxisValue, maxAxisValue;
@property(strong, nonatomic) NSMutableArray *ticks, *colors;
@property(strong, nonatomic) NSString *title;

- (id) initWithString:(NSString*)string;


@end
