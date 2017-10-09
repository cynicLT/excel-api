 /*
 ********************* VALUE MAPS ***************************************
  
    -The below objects hold the row numbers so you can access the values via the "key"
    -This is for readability and easy contol if speadsheets are updated
    -The safeway type sheets are different in that they hold and array value to access the information
    -Each Sheet uses two objects: 
      1. This object that maps to the this.values sheet column numbers 
      2. This object maps to the this.extractedValues to access the array
  */
  
  //Distributors
  
  //Maps to this.values
  this.zipPostOffs = {
    'startRow': '8', //horizontal "row"
    'vendorId': '1', //the rest are vertical "columns"
    'UPC': '4',
    'regularPrice': '5',
    'postOffAmount': '6',
    'net': '7'
  };
  //Maps to this.extactedValues 
  this.ZPOExtracted = {
    'vendorId': '0',
    'UPC': '1',
    'regularPrice': '2',
    'postOffAmount': '3',
    'net': '4',
  };
  
  // Maps to this.values
  // Intermountain   //#2 Distributor
  this.interMountainPostOffWine = {
    'startRow': '7', //horizontal "row"
    'vendorId': '0', //the rest are vertical "columns"
    'UPC': '1',
    'regularPrice': '7',
    'postOffAmount': '8',
    'net': '9',
    'newDeal' : '10'
  };
  
    this.interMountainPostOffBeer = {
    'startRow': '7', //horizontal "row"
    'vendorId': '0', //the rest are vertical "columns"
    'UPC': '4',
    'regularPrice': '5',
    'postOffAmount': '6',
    'net': '7'
  };
  
  //Maps to this.extactedValues 
  this.IMPOExtracted = {
    'vendorId': '0',
    'UPC': '1',
    'regularPrice': '2',
    'postOffAmount': '3',
    'net': '4',
    'newDeal' : '5'
  };  

  //Retailers
  
  //Maps to this.values
  this.rosauers = {
    'startRow': '2',
    'startColumn':'5',
    'vendorId': '5',
    'UPC': '6',
    'currentCost': '7',
    'currentEffectiveDate': '8',
    'futureCost': '9',
    'futureEffectiveDate': '10',
    'PA1Amount': '11',
    'PA1EffectiveStartDate': '12',
    'PA1EffectiveEndDate': '13',
    'PostedPA1Amount' : '21',
  };
  //Maps to this.extactedValues 
   this.RExtracted = {
    'vendorId': '0',
    'UPC': '1',
    'currentCost': '2',
    'currentEffectiveDate': '3',
    'PostedPA1Amount' : '4',
    'futureCost': '5',
    'futureEffectiveDate': '6',
    'PA1Amount': '7',
    'PA1EffectiveStartDate': '8',
    'PA1EffectiveEndDate': '9',

  };
  //Maps to this.values
  this.townPump = {
    'startRow': '1',
    'startColumn':'8',
    'vendorId': '0',
    'UPC': '5',
    'currentEverydayCost': '7',
    'newEverydayCost': '8',
    'newEffectiveDate': '9',
    'discountinuedDate': '10',
    'promoEffectiveDate': '11',
    'promoCost': '12',
    'promoEndDate': '13',
  };
  //Maps to this.extactedValues 
  this.TPExtracted = {
    'vendorId': '0',
    'UPC': '1',
    'currentEverydayCost': '2',
    'newEverydayCost': '3',
    'newEffectiveDate': '4',
    'discountinuedDate': '5',
    'promoEffectiveDate': '6',
    'promoCost': '7',
    'promoEndDate': '8',
  };
  
  // sub arrays are for the location of the "safeway row" that the parseSafeway() function creates
  // Each safeway row has multiple spreadsheet rows
  // Maps to this.extactedValues. Dont need one tha maps to this.values as we are writing the entrire rows back  
  this.safeway = {
    //this is the cell number for a single safeway item 2d array
    'startRow': 21,
    'UPC': [0, 0],
    'vendCost': [0, 7],
    'pendingCostAmount': [2, 8],
    'effectiveDate': [2, 9],
    'allowAmount': [0, 10],
    'arrivalStart': [0, 11],
    'arrivalEnd': [0, 12],
    'addRevText': [2, 5],
    'addText': [2, 6],
    'addPrice': [2, 10],
    'addStart': [2, 11],
    'addEnd': [2, 12],
	};
  
 this.thriftway = {
   'startRow': '2',
   'startColumn': '1',
   'vendorId': '6',
   'UPC': '2',
   'purchaseCost': '9',
   'newCaseCost': '10'
 };
  
  this.THRIFTExtracted = {
    'vendorId': '0',
    'UPC': '1',
    'purchaseCost': '2',
    'newCaseCost': '3'
  };
  
   //Maps to this.values
  this.freshMarket = {
    'startRow': '10',
    'startColumn':'2',
    'vendorId': '2',
    'UPC': '3',
    'caseCost': '12',
    'effectiveDate': '14',
    'ioAllowance': '17',
    'ioStartDate': '18',
    'ioEndDate': '19',
  };
    //Maps to this.extactedValues 
   this.FMExtracted = {
    'vendorId': '0',
    'UPC': '1',
    'caseCost': '2',
    'effectiveDate': '3',
    'ioAllowance': '4',
    'ioStartDate': '5',
    'ioEndDate': '6',
  };
   
  /* NATE */
  this.zipTrip = {
    'startRow': '3',
    'startColumn':'1',
    'vendorId': '11',
    'UPC': '3',
    'effectiveDate': '45',        // column AT #46
    'cost': '56',                 // column BE #57
  };
  //Maps to this.extactedValues 
  this.ZTExtracted = {
    'vendorId': '0',
    'UPC': '1',
    'effectiveDate': '6',
    'cost': '7',
    'startRow' : "3",
  };

  
  
  //these are column indexes
  this.vendorId = '';
  this.UPC = '';
  this.startRow = '';
  this.startColumn = '';
  this.currentCost = '';
  this.effectiveDates = {};
  this.type = '';
  this.init();
}
