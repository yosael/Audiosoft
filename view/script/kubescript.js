var activeModal = new function() {
	this.activeModalPanel = null;
	this.setActiveModalPanel = function(a) {
		this.activeModalPanel = a;
	};
};

function upperCaseAllInputs() {
	jQuery("input.upper").each( function() {
		this.value = this.value.toUpperCase();
	});
	jQuery("textarea.upper").each( function() {
		this.value = this.value.toUpperCase();
	});
}



function lowerCaseAllInputs() {
	jQuery("input.lower").each( function() {
		this.value = this.value.toLowerCase();
	});
	jQuery("textarea.lower").each( function() {
		this.value = this.value.toLowerCase();
	});
}

function openWindow(pageUrl, name) {
	var options = "toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, width=255, height=305, top=85, left=140";
	window.open(pageUrl, name, options);
}

function printArea(name, title) {
	var ficha = document.getElementById(name);
	var options = "toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, width=600, height=600, top=10, left=10";
	var ventimp = window.open(" ", title, options);
	ventimp.document.write(ficha.innerHTML);
	ventimp.document.close();
	// ventimp.print();
	// ventimp.close();
}

function moveContent(source, destiny) {
	var myDiv = document.getElementById(source);
	if (myDiv != null) {
		var master = document.getElementById(destiny);
		master.innerHTML = myDiv.innerHTML;
	}
}

function isDecimalRestrict(evt, elemento, maxDecimals)
{
    try {
        var charCode = (evt.which) ? evt.which : event.keyCode;
        if(charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
            return false;
        }
        else {
            //Validamos si no esta excediendo la cantidad de decimales especificada
            if(elemento.value.indexOf('.') != -1) {
                var valor = elemento.value;
                var currdecimals = valor.substring(valor.indexOf('.') + 1, valor.length);
                //Validamos que no hayan mas decimales que los permitidos cuando quiera escribir decimales,
                //es decir que el cursor este despues del punto decimal
                if(currdecimals.length + 1 > maxDecimals && charCode != 8 && caretPosition(elemento) > valor.indexOf('.'))
                    return false;
                //Validaciones respecto al punto decimal
                if(charCode == 46 && valor.indexOf('.') != -1) { //Si ya tenia un punto decimal, no permitir otro
                    return false;
                }
            }
        }
    }catch(e){}
    return true;
}

function restrictNumDigits(evt, elemento, maxDigits) {
    try {
        var charCode = (evt.which) ? evt.which : event.keyCode;
        if(charCode > 31 && (charCode < 48 || charCode > 57) ) {
            return false;
        }
        else {
            var valor = elemento.value;
            var currdigits = valor.substring(0, valor.length);
            //Validamos que no hayan mas decimales que los permitidos cuando quiera escribir decimales,
            //es decir que el cursor este despues del punto decimal
            if(currdigits.length + 1 > maxDigits && charCode != 8)
                return false;
        }
    }catch(e){}
    return true;
}

function verifyDecimalDot(elemento) {
if(elemento.value.length -1 == elemento.value.indexOf('.'))
	elemento.value += '0';
}

function restrictNumDigitsDecimals(evt, elemento, maxDigits, maxDecimals)
{
    var arrCaret = caretPosition(elemento);
    try {
        var charCode = (evt.which) ? evt.which : event.keyCode;
        if(charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
            return false;
        }
        else {
            //Validamos si no esta excediendo la cantidad de decimales especificada
            if( (parseInt(arrCaret[1]) - parseInt(arrCaret[0])) == parseInt(arrCaret[2]) )
                return true;
            if(elemento.value.indexOf('.') != -1) {
                var valor = elemento.value;
                var currdecimals = valor.substring(valor.indexOf('.') + 1, valor.length);
                var currdigits = 0;
                if(valor.indexOf('.') != -1)
                    currdigits = valor.substring(0, valor.indexOf('.'));
                else
                    currdigits = valor.substring(0, valor.length);
                //Validamos que no hayan mas decimales que los permitidos cuando quiera escribir decimales,
                //es decir que el cursor este despues del punto decimal
                if(currdigits.length + 1 > maxDigits && charCode != 8 &&
                        (arrCaret[1] <= valor.indexOf('.') || valor.indexOf('.') == -1))
                    return false;
                if(currdecimals.length + 1 > maxDecimals && charCode != 8 && arrCaret[1] > valor.indexOf('.'))
                    return false;
                //Validaciones respecto al punto decimal
                if(charCode == 46 && valor.indexOf('.') != -1) { //Si ya tenia un punto decimal, no permitir otro
                    return false;
                }
            }
            else {
                currdigits = elemento.value.substring(0, elemento.value.length);
                if(currdigits.length + 1 > maxDigits && charCode != 8 && charCode != 46)
                    return false;
            }
        }
    }catch(e){}
    return true;
}

function caretPosition(elemento)
{
        var campo = elemento;
        if (document.selection) {// IE Support
                campo.focus();                                        // Set focus on the element
                var oSel = document.selection.createRange();        // To get cursor position, get empty selection range
                oSel.moveStart('character', -campo.value.length);    // Move selection start to 0 position
                campo.selectionEnd = oSel.text.length;
                oSel.setEndPoint('EndToStart', document.selection.createRange() );
                campo.selectionStart = oSel.text.length; // The caret position is selection length
        }
        var arrRet = new Array(3);
        arrRet[0] = campo.selectionStart;
        arrRet[1] = campo.selectionEnd;
        arrRet[2] = elemento.value.length;
        return arrRet;
}