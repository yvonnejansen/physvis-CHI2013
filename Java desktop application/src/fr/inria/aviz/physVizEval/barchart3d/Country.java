package fr.inria.aviz.physVizEval.barchart3d;

import org.jzy3d.colors.*;

class Country {
	  String name;
	  Color col;
	  int rowNo;
	  
	  public Country(String name, Color col) {
	    this.name = name;

	    this.col = col;
	  }
	  
	  public void setRowNo (int no){
		  this.rowNo = no;
	  }
	  

	}
