// Define some globals here
var UC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
var LC = "abcdefghijklmnopqrstuvwxyz";
var NU = "0123456789";
var SP = " ";
var CR = "\x0D";
var LF = "\x0A";

function checkCharSet(stringToCheck,charSet) {
    // charSet contains all valid characters allowable
    // Check each char in stringToCheck and ensure it contains valid chars defined by charSet
    var myChar;
    var loopCounter;
    var stringIndex;
    var strlen = stringToCheck.length;
    for (loopCounter = 0; loopCounter < strlen; loopCounter++) {
    	myChar = stringToCheck.charAt(loopCounter);
    	stringIndex = charSet.indexOf(myChar);
    	if (stringIndex == -1) {
            if (myChar == " ") {
		myChar = "<space>";
            }
            alert ("The following character is not allowed ---> " + myChar);
            return false;
	}
    }
    return true;
}

function deleteCommas(stringToCheck) {
    // Eliminate all commas
    var myChar;
    var loopCounter;
    var resultString = "";
    var strlen = stringToCheck.length;
    for (loopCounter = 0; loopCounter < strlen; loopCounter++) {
    	myChar = stringToCheck.charAt(loopCounter);
        if (myChar != ",") {
            resultString += myChar;
	}
    }
    return resultString;
}

function deleteLeadingSpaces(argwork) {
    // I delete beginning spaces from a string
    var work = argwork;
    while (work.charCodeAt(0) == 32) {
        work = work.slice(1);
    }
    return work;
}

function deleteLeadingZeroes(work) {
    // I delete leading zeroes
    if (work.length > 1) {
        while (work.charCodeAt(0) == 48) {
            work = work.slice(1);
        }
        if (work.length == 0) {
            work = "0";
        }
    }
    return work;
}

function deleteSpaces(stringToCheck) {
    // Eliminate all spaces
    var myChar;
    var loopCounter;
    var resultString = "";
    var strlen = stringToCheck.length;
    for (loopCounter = 0; loopCounter < strlen; loopCounter++) {
        myChar = stringToCheck.charAt(loopCounter);
	if (myChar != " ") {
            resultString += myChar;
	}
    }
    return resultString;
}

function deleteTrailingSpaces(argwork) {
    // I delete trailing spaces from a string
    var work = argwork;
    var lastc = work.length - 1;
    while (work.charCodeAt(lastc--) == 32) {
        work = work.slice(0,-1);
    }
    return work;
}

function getSelectedRadioValue(radioGroup) {
    var selectedRadioValue = "";
    var radioIndex;
    for (radioIndex = 0; radioIndex < radioGroup.length; radioIndex++) {
        if (radioGroup[radioIndex].checked) {
            selectedRadioValue = radioGroup[radioIndex].value;
            break;
	}
    }
    return selectedRadioValue;
}

function launch(file,name,winwidth,winheight) {
    var string = "width=" + winwidth + ",height=" + winheight
	+ "toolbar=yes,directories=yes,menubar=yes,resizable=yes,dependent=no";
    var hwnd = window.open(file,name,string);
    if (navigator.appName == "Netscape") {
	hwnd.focus();
    }
}

function numericOnly(stringToCheck) {
    // Eliminate all but numeric characters
    var myChar;
    var loopCounter;
    var resultString = "";
    var strlen = stringToCheck.length;
    for (loopCounter = 0; loopCounter < strlen; loopCounter++) {
    	myChar = stringToCheck.charAt(loopCounter);
        if (myChar >= "0" && myChar <= "9") {
            resultString += myChar;
	}
    }
    return resultString;
}

function reduceSpaces(argwork) {
    // replace multiple spaces with one
    var work = argwork;
    var myIndex = work.indexOf("  ");
    while (myIndex != -1) {
    	work=work.slice(0,myIndex) + work.slice(myIndex + 1);
        myIndex = work.indexOf("  ");
    }
    return work;
}

function replaceCarriageReturns(stringToCheck) {
    var myChar;
    var loopCounter;
    var resultString = "";
    var strlen = stringToCheck.length;
    for (loopCounter = 0; loopCounter < strlen; loopCounter++) {
        myChar = stringToCheck.charAt(loopCounter);
	if (myChar != CR) {
            if (myChar != LF) {
                resultString += myChar;
            } else {
                //alert ("I found a LF!");
                resultString += SP;
            }
        }
    }
    return resultString;
}

function ucAndNumericOnly(stringToCheck) {
    // Eliminate all but numeric characters and upper case alpha
    var myChar;
    var loopCounter;
    var resultString = "";
    var strlen = stringToCheck.length;
    for (loopCounter = 0; loopCounter < strlen; loopCounter++) {
        myChar = stringToCheck.charAt(loopCounter);
        if ((myChar >= "0" && myChar <= 9) || (myChar >= "A" && myChar <= "Z")) {
            resultString += myChar;
        }
    }
    return resultString;
}
