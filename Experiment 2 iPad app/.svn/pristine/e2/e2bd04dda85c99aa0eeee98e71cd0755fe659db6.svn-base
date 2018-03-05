//
//  ModelController.h
//  TangVizPilotApp
//
//  Created by Yvonne Jansen on 1/26/12.
//  Copyright (c) 2012 INRIA AVIZ. All rights reserved.
//

#import <Foundation/Foundation.h>

@class DataViewController;

@interface ModelController : NSObject <UIPageViewControllerDataSource, UIAlertViewDelegate>
//@property (strong) NSArray *countries;
@property (readonly, strong, nonatomic) NSArray *pageData;
//@property (readonly, strong, nonatomic) NSDictionary *allQuestions;
@property( strong, nonatomic) NSString *datasetName;
//@property (strong) NSArray *namesOfDataSets;
@property int indexOfCurrentDataSet;

- (DataViewController *)viewControllerAtIndex:(NSUInteger)index storyboard:(UIStoryboard *)storyboard;
- (NSUInteger)indexOfViewController:(DataViewController *)viewController;
- (void)setPageData:(NSArray *)pageData;

@end
