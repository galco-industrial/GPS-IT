
function checkAuditUserID() {
    // Check User ID
    var myForm = document.form1;
    var work = myForm.auditUserID.value;
    work = deleteSpaces(work);
    work = work.toUpperCase();
    myForm.auditUserID.value = work;
    if (work.length > 0) {
        if (checkCharSet(work, UC + NU) == false || work.length != 4) {
            alert ("Please enter a valid User ID.");
            myForm.auditUserID.focus();
            return;
        }
    }
}

function hideTip() {
    var myForm = document.form1;
    window.defaultStatus = "";
    if (myForm.enableToolTips.checked == true) {
        var oDiv = document.getElementById("virtualToolTip");
    	if (oDiv) {
            oDiv.style.visibility = "hidden";
        }
    }
}

function showTip(oEvent, tipKey, x, y) {
    hideTip();
    var tipText = null;
    if (tipKey) {
        if (isNaN(tipKey)) {
            tipText = getMessage(tipKey);
        } else {
            if (aToolTip[tipKey]) {
                tipText = aToolTip[tipKey];
            }
        }
    } 
    if (tipText) {
        window.defaultStatus = tipText;
        var myForm = document.form1;
        if (myForm.enableToolTips.checked == true) {
            if (!x) { x = 5; }
            if (!y) { y = 5; }
            var oDiv = document.getElementById("virtualToolTip");
            if (oDiv) {
                var oDiv2 = document.getElementById("virtualToolTipText");
                oDiv2.innerHTML = tipText;
                oDiv.style.left = oEvent.clientX + x;
                oDiv.style.top = oEvent.clientY + y;
                oDiv.style.visibility = "visible";
            }
        }
    }
}
