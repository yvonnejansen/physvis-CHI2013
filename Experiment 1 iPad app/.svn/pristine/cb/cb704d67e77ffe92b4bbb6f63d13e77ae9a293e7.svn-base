//
//  MetaDataReader.m
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 4/7/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import "MetaDataReader.h"

@implementation MetaDataReader

@synthesize ticks, minAxisValue, maxAxisValue, colors, title;

- (id) initWithString:(NSString*)string
{
    if (self) {
        theData = [string mutableCopy];
//        NSLog(@"metadata: %@", string);
        NSScanner *scanner = [NSScanner scannerWithString:theData];

        NSRange tl = [theData rangeOfString:@"<title>"];
        [scanner setScanLocation:tl.location+tl.length];
        [scanner scanUpToString:@"</title>" intoString:&title];
        
        NSRange min = [theData rangeOfString:@"<minAxisValue>"];
        
        [scanner setScanLocation:min.location+min.length];
        [scanner scanFloat:&minAxisValue];
        
        NSRange max = [theData rangeOfString:@"<maxAxisValue>"];
        [scanner setScanLocation:max.length + max.location];
        [scanner scanFloat:&maxAxisValue];
        
//        NSMutableDictionary *axisLabeling = [[NSMutableDictionary alloc] init];
        NSRange start = [theData rangeOfString:@"<ticks>"];
        [theData deleteCharactersInRange:NSMakeRange(0, start.location+start.length)];
        NSRange end = [theData rangeOfString:@"</ticks>"];
        [theData deleteCharactersInRange:NSMakeRange(end.location, [theData length]-end.location)];
        
        ticks = [[NSMutableArray alloc] init];
        NSArray *tickValues = [theData componentsSeparatedByString:@"\n"];
//        NSLog(@"line array: %@", [tickValues description]);
        for (int i = 1; i < [tickValues count] - 1; i++) {
            scanner = [NSScanner scannerWithString:[tickValues objectAtIndex:i]];
            NSRange prefix = [[tickValues objectAtIndex:i] rangeOfString:@"label=\""];
            int loc = prefix.length + prefix.location;
            [scanner setScanLocation:loc];
            NSString *number;
            [scanner scanUpToString:@"\"/>" intoString:&number];
//            NSLog(@"scanned number: %@", number);
            [ticks addObject:number];
        }
//        NSLog(@"min %.2f max %.2f ticks %@", minAxisValue, maxAxisValue, [ticks description]);
        
        // read the colors
        theData = [string mutableCopy];
        colors = [[NSMutableArray alloc] init];
        start = [theData rangeOfString:@"<palette>"];
        [theData deleteCharactersInRange:NSMakeRange(0, start.location+start.length)];
        end = [theData rangeOfString:@"</palette>"];
        [theData deleteCharactersInRange:NSMakeRange(end.location, [theData length]-end.location)];
        NSMutableArray *theColors = [[theData componentsSeparatedByString:@"\n"] mutableCopy];
        [theColors removeLastObject];
        [theColors removeObjectAtIndex:0];
       for (int i = 0; i < [theColors count]; i++)
        {
            CGFloat r, g, b, a;
            scanner = [NSScanner scannerWithString:[theColors objectAtIndex:i]];
            NSRange prefix = [[theColors objectAtIndex:i] rangeOfString:@"r=\""];
            int loc = prefix.length + prefix.location;
            [scanner setScanLocation:loc];
            [scanner scanFloat:&r];
            prefix = [[theColors objectAtIndex:i] rangeOfString:@"g=\""];
            loc = prefix.length + prefix.location;
            [scanner setScanLocation:loc];
            [scanner scanFloat:&g];
            prefix = [[theColors objectAtIndex:i] rangeOfString:@"b=\""];
            loc = prefix.length + prefix.location;
            [scanner setScanLocation:loc];
            [scanner scanFloat:&b];
            prefix = [[theColors objectAtIndex:i] rangeOfString:@"a=\""];
            loc = prefix.length + prefix.location;
            [scanner setScanLocation:loc];
            [scanner scanFloat:&a];
            UIColor *color = [UIColor colorWithRed:r/255.0 green:g/255.0 blue:b/255.0 alpha:a/255.0];
            [colors addObject:color];
        }
//        NSLog(@"colors: \n%@", colors);
    }
    return self;
}
@end
