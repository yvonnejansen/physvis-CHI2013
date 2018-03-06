package fr.inria.aviz.physVizEval;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.rendering.view.Camera;

class TimeSliceViz {
	  Country[] countries;
	  Disc[][] discs;
	  String[] years;
	  int cubeWidth = 800;
	  int cubeHeight = 800;
	  int sliceHeight = 20;
	  int maxX = 8;
	  GL2 gl;
	  GLU glu;
	  Camera cam;
	  
	  public TimeSliceViz (GL2 gl, GLU glu, Camera cam, String[] countries, String[] years, Color[] colors, Table x, Table y, Table area) {
	    this.gl = gl;
	    this.glu = glu;
	    this.cam = cam;
		this.countries = new Country[countries.length];
	    this.discs = new Disc[countries.length][years.length];
	    this.years = years;
	    if (!(countries.length <= colors.length)) System.out.println("Error: not enough colors predefined!");
	    else {
	      for (int i = 0; i < countries.length; i++) {
	        this.countries[i] = new Country(countries[i], colors[i]);
	        for (int j = 0; j < years.length; j++) {  
	          System.out.println("creating " + countries[i] + " for year " + years[j] + " with x " + x.getFloat(x.getColumnIndex(years[j]), x.getRowIndex(countries[i])) 
	                      + " y " + y.getFloat(y.getColumnIndex(years[j]), y.getRowIndex(countries[i]))
	                      + " area " +  area.getFloat(area.getColumnIndex(years[j]), area.getRowIndex(countries[i]))/100000);
	          this.discs[i][j] = new Disc(
	                                  this.countries[i], 
	                                  Util.map(x.getFloat(x.getColumnIndex(years[j]), x.getRowIndex(countries[i])), 0, maxX, -cubeWidth/2, cubeWidth/2),
	                                  Util.map(y.getFloat(y.getColumnIndex(years[j]), y.getRowIndex(countries[i])), 0, 85, -cubeWidth/2, cubeWidth/2),
	                                  cubeHeight / years.length * j - cubeHeight/2,
	                                  area.getFloat(area.getColumnIndex(years[j]), area.getRowIndex(countries[i])) / 100000,
	                                  sliceHeight);
	        }
	      }
	    }
	  }
	  
	  void draw() {
//	    pushMatrix();
//	    rotateZ(-PI);
	     drawDiscs();
	     drawConnectorLines();
	     drawGridlines();
	     drawSlices();
	     writeYears();
//	     popMatrix();
	  }
	  
	  void drawDiscs() {
	    // draw individual discs 
	    for (Disc[] country:discs) {
	      for (Disc disc:country) {
	       disc.draw(gl, glu, cam);
	      }
	     }
	    
	  }
	  
	  void drawConnectorLines(){
	  //  draw connecting lines between year slices
	    for (int j = 0; j < discs.length; j++){
	      stroke(discs[j][0].country.col, 255);
	      strokeWeight(4);
	      for (int i = 1; i < discs[j].length; i++) {
	        line(discs[j][i-1].x, discs[j][i-1].y, discs[j][i-1].z, discs[j][i].x, discs[j][i].y, discs[j][i].z);
	      }
	     }
	  }

	//  stroke(0);
	//  for (int i = 0; i < years.length; i++)
	//  {
//	    // write the years on each slice at each side
//	    textAlign(LEFT);
//	    pushMatrix();
//	    fill(0,150);
////	    rotateZ(HALF_PI);
//	    rotateZ(HALF_PI);
//	    text(years[i], -cubeWidth/2 + 10,  cubeWidth/2, cubeHeight / years.length * i - cubeHeight/2);
//	    popMatrix();
	//  }
////	    pushMatrix();
////	    translate(-sliceWidth/2, -sliceHeight/2, myYear.offset + sliceThickness/2);
////	    rotateZ(HALF_PI);
////	    text(myYear.curYear, 0,0,0);
////	    popMatrix();
	////
////	    pushMatrix();
////	    translate(sliceWidth/2, -sliceHeight/2, myYear.offset + sliceThickness/2);
////	    rotateZ(PI);
////	    text(myYear.curYear, 0,0,0);
////	    popMatrix();
	////
////	    pushMatrix();
////	    translate(sliceWidth/2, sliceHeight/2, myYear.offset + sliceThickness/2);
////	    rotateZ(-HALF_PI);
////	    text(myYear.curYear, 0,0,0);
////	    popMatrix();
	////
	////  }
	//////  popMatrix();
	////
	//  popMatrix();


	  void drawSlices(){
	   // draw the translucent boxes around the discs  
	     fill(0,0,80,15);
	     stroke(0,0,80,35);
	     strokeWeight(1);
	     for (int i = 0; i < years.length; i++) {
	       pushMatrix();
	       translate(0, 0, cubeHeight / years.length * i - cubeHeight/2);
	       box(cubeWidth, cubeWidth, sliceHeight);
	      popMatrix();
	     }
	  }


	  void writeYears(){
	    fill(0,150);
	    stroke(0,150);
	    textAlign(CENTER);
	    pushMatrix();
	    
	    for (int j = 0; j < 4; j++){
	      
	      for (int i = 0; i < years.length; i++){
//	      pushMatrix();
//	      translate(-cubeWidth/2 + 40, cubeWidth/2 + 2, cubeHeight / years.length * i - cubeHeight/2 - 10);
//	      rotateX(-HALF_PI);
//	      text(years[i], 0, 0, 0);
//	      popMatrix();

	        pushMatrix();
	        rotateZ(HALF_PI * j);
	        pushMatrix();
	        translate(-cubeWidth/2 + 40, cubeWidth/2 + 2, cubeHeight / years.length * i - cubeHeight/2 - 10);
	        rotateX(-HALF_PI);
	        text(years[i], 0, 0, 0);
	        popMatrix();
	        popMatrix();
	      }
	    }
	    popMatrix();
	  }


	  void drawGridlines() {
	     fill(0,0,80,15);
	     stroke(0,0,80,35);
	     strokeWeight(1);
	     for (int i = 0; i < years.length; i++) {
	       pushMatrix();
	       translate(-cubeWidth/2, -cubeWidth/2, cubeHeight / years.length * i - cubeHeight/2 + sliceHeight/2);
	       for (int j = 0; j < maxX; j++) {
	         fill(0,0,80,15);
	         stroke(0,0,80,35);
	         line(Util.map(j, 0, maxX, 0, cubeWidth),0, Util.map(j, 0, maxX, 0, cubeWidth),cubeWidth);
	        
	         fill(0,0,80,255);
	         stroke(0,0,80,255);
	         text(j,Util.map(j, 0, maxX, 0, cubeWidth),0);
	       }
	       for (int j = 0; j < 90; j+=10) {
	         fill(0,0,80,15);
	         stroke(0,0,80,35);
	         line(0,Util.map(j, 0, 85, 0, cubeWidth),cubeWidth, Util.map(j, 0, 85, 0, cubeWidth));

	         fill(0,0,80,255);
	         stroke(0,0,80,255);
	         text(j,0,Util.map(j, 0, 85, 0, cubeWidth));
	       }
	      popMatrix();
	     }
	    
	  }


	  
	}

