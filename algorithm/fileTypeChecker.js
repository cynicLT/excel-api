    whosFileIsIt: function(id) {
      try {
        var ss = SpreadsheetApp.openById(id);
        var sheet = ss.getSheets()[0];
        
        var range = sheet.getRange("A1:L7");
        var rangeArray;
        var arrayFileInfo = [];
        var isVendor = -2;
        type = "";
        
        if (!range.isBlank()) {
          rangeArray = range.getValues();

          if ((rangeArray[6][1] == "Effective:") || (rangeArray[0][11].toLowerCase().indexOf("zip beverage") > 0)) { //zip beverage
            Logger.log("ZIP BEVERAGE found");
            var effectiveDate =  rangeArray[6][2];
            var title = rangeArray[5][1];
            var month = effectiveMonth(effectiveDate);
            
            if (title.toLowerCase().indexOf("quantity buys") > 0) {
              Logger.log("Quantity Buy Sheet Found");
              type = "Zip Quantity Buy";
              isVendor = 2;
              
            } else {              
              Logger.log("Post Off found");
              type = "Zip Post Off";
              isVendor = 1;
            }
            
          } else if (rangeArray[4][0] == "ITEM") { //intermountain #1a Distributor
            Logger.log("Intermountain Wine found");
            type = "Intermountain Wine Post Off";
            isVendor = 1;
            
          } else if (rangeArray[4][0] == "ITEM#") { //intermountain #1b Distributor
            Logger.log("Intermountain Beer found");
            type = "Intermountain Beer Post Off";
            isVendor = 1;
                        
          } else if (rangeArray[1][6] == "GPC Code") { //ROSAUERS
            Logger.log("Rosauers found");
            type = "Rosauers";
            isVendor = 0;
            
          } else if (rangeArray[1][3] == "Item UPC") { //Zip Trip Found
            Logger.log("Zip Trip");
            isVendor = 0;
            type = "Zip Trip";
            
          } else if (rangeArray[0][0] == "VIN") { //TownPump found
            Logger.log("Town Pump found");
            isVendor = 0;
            type = "Town Pump"
            
          } else if (rangeArray[0][0] == "ZIP BW- CURRENT PRICING") { //Thriftway
            Logger.log("Thriftway Found");
            isVendor = 0;
            type =  "Thriftway"
            
          } else if (rangeArray[0][5] == "SAFEWAY INC") { //Safeway Albertsons
            Logger.log("Safeway and Albertsons Found");
            isVendor = 0;
            type =  "Safeway and Albertsons"
            
          } else if (rangeArray[0][0] == "Holiday Monthly Beer and Alcohol Price Sheet") { // Holiday
            Logger.log("Holiday Found");
            isVendor = 0;
            type =  "Holiday"
            
          } else if (rangeArray[3][7] == "Vendor Name:") { // MFM
            Logger.log("Missoula Fresh Market Found");
            isVendor = 0;
            type =  "Fresh Market"
            
          } else {
            isVendor = -1; //edited to check
            type = "unknown"
            Logger.log("Unknown Sheet Type, please confirm this is the correct spreadsheet or contact support@blendedmarket.com")
          }
          //returns the type, if they are a vendor and the sheetId just in case.
          fileInfo.type = type;
          fileInfo.isVendor =  isVendor;
          return fileInfo;
        } else {
          Logger.log("Uh is the sheet blank?");
        }
        
      } catch (err) {
        Logger.log(err);
      }
      
    },
    