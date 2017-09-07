function isApplicableDocField(docField, classList) {
    if (docField.isCurrency) {
      if (classList.contains("CURRENCY")) {
        return true;
      }
    }
    if (docField.isDate) {
      if (classList.contains("DATE")) {
        return true;
      }
    }
    return false;
}

/* When the user clicks on the button, 
toggle between hiding and showing the dropdown content */

var targetLabel;
var targetLabelRect;

function findOverlappingFields(classList, label) {
	var rect1 = label.getBoundingClientRect();
	
	var fields = null;
	if (classList.contains("CURRENCY")) {
		fields = document.querySelectorAll("div.CURRENCY");
	} else if (classList.contains("DATE")) {
		fields = document.querySelectorAll("div.DATE");
	}
	if (fields) {
		for (var i = 0; i < fields.length; i++) {
			var field = fields[i];
			var rect2 = field.getBoundingClientRect();
			var overlap = !(rect1.right < rect2.left + 1 || 
							rect1.left + 1 > rect2.right || 
							rect1.bottom < rect2.top || 
							rect1.top > rect2.bottom);
			if (overlap) {
				//label.classList.add("overlapped");
				return true;
			}
		}
	}
	return false;
}


function placeSegmentLabel(elem, label) {
    label.style.right = (elem.offsetWidth - 2) + "px";
    label.style.top = null;
    if (findOverlappingFields(elem.classList, label)) {
    	label.style.right = "-2px";
    	label.style.top = -(elem.offsetHeight + 2) + "px";
    };
}

function makeShowThrough(elem) {
	//console.log("setting show through on " + elem.target);
	var label = elem.target;
	//if (label.classList.contains("overlapped")) {
	//	targetLabel = label;
	//	targetLabelRect = label.getBoundingClientRect();
	//	console.log(".. " + targetLabelRect);
	//	label.removeEventListener("mouseenter", makeShowThrough, false);
	//	document.body.addEventListener("mousemove", unmakeShowThrough, false);
	//	label.style.opacity = "0";
	//	label.style.transition = "opacity 1s ease-in-out";
	//	setTimeout(function() {
	//		label.style.display = "none";
	//	}, 1000);
	//} else {
		label.style.opacity = "0";
		label.style.transition = "opacity 1s ease-in-out";
	//}
	//elem.target.classList.add("showThrough");
}

//function unmakeShowThrough(elem) {
//	if (targetLabel) {
//		var rect = targetLabelRect;
//		//console.log("mouse move " + elem.clientX);
//		//console.log("    coords " + rect.left + "  " + (rect.left + rect.width));
//		if (elem.clientX < rect.left || 
//				elem.clientX > rect.left + rect.width ||
//				elem.clientY < rect.top ||
//				elem.clientY > rect.top + rect.height) {
//			elem.stopPropagation();
//			var targetLabelx = targetLabel;
//			targetLabel = null;
//			targetLabelRect = null;
//			//console.log("unmake " + elem.clientX + "  " + elem.clientY);
//			// Mouse move co-ordinates are outside the label bounds
//			//document.body.removeEventListener("mousemove", unmakeShowThrough, false);
//			targetLabelx.style.display = null;
//			targetLabelx.style.opacity = "1";
//			targetLabelx.style.transition = "opacity 0.5s ease-in-out";
//			targetLabelx.addEventListener("mouseenter", makeShowThrough, false);
//		}
//	}
//}

function clearShowThrough(elem) {
	//elem.target.style.width = null;
	//elem.target.classList.remove("showThrough");
	var label = elem.target;
	//if (!label.classList.contains("overlapped")) {
		label.style.opacity = "1";
    	label.style.transition = "opacity 0.5s ease-in-out";
	//}
}

function myFunction(elem, updateBackend) {
    var fieldNameSelect = document.getElementById("field-name-select");
    if (fieldNameSelect) {
      fieldNameSelect.remove();
      //console.log("-----------");
      //console.log(elem);
      //console.log(fieldNameSelect.triggeringElement);
      //console.log(elem == fieldNameSelect.triggeringElement);
      if (elem == fieldNameSelect.triggeringElement) {
        return;
      }
    }
    fieldNameSelect = document.createElement("div");
    fieldNameSelect.classList.add("dropdown");
    fieldNameSelect.id = "field-name-select";
    fieldNameSelect.setAttribute("tabindex", "0");
    fieldNameSelect.triggeringElement = elem;
    var computedStyle = window.getComputedStyle(elem);
    var marginTop = parseInt(computedStyle.marginTop);
    fieldNameSelect.setAttribute("style", "position:relative; top:" + (elem.offsetHeight + marginTop) + "px;");
    
    var newDropContent = document.createElement("div");
    newDropContent.classList.add("dropdown-content");
    fieldNameSelect.appendChild(newDropContent);
    
    for (var [key, docField] of docFields) {
      if (isApplicableDocField(docField, elem.classList)) {
        var newDropItem = document.createElement("a");
        newDropItem.id = key;
        newDropItem.setAttribute("href", "javascript:void(0)");
        var newDropLabel = document.createTextNode(docField.title);
        newDropItem.appendChild(newDropLabel);
        newDropItem.onclick = function (e) {
          var itemPath = "ItemPath_" + e.target.id;

          var docField1 = docFields.get(e.target.id);
          docField1.targetId = elem.id;

		  // Get any segment label before checking for existing elements
          var segmentLabel = elem.querySelector("span");
          
          var oldSegmentLabel = document.getElementById(itemPath);
          
          if (segmentLabel == null) {
            //console.log("add new span ");
            // Remove any existing segment label defined elsewhere
            if (oldSegmentLabel) {
              var parentSegment = oldSegmentLabel.parentNode;
              parentSegment.targetId = null;
              parentSegment.removeChild(oldSegmentLabel);
            }
            
            // Create a new segment label
            segmentLabel = document.createElement("span");
            segmentLabel.setAttribute("title", "");
            segmentLabel.innerText = docField1.title;
            segmentLabel.id = itemPath;
            segmentLabel.addEventListener("mouseenter", makeShowThrough, false);
            segmentLabel.addEventListener("mouseleave", clearShowThrough, false);
            segmentLabel.style.opacity = "0";
            elem.appendChild(segmentLabel);
            placeSegmentLabel(elem, segmentLabel);
            segmentLabel.style.opacity = "1";
            updateBackend(itemPath, elem.getAttribute("data-value"));
          } else {
            if (segmentLabel.parentNode.id == elem.id && segmentLabel.id == itemPath) {
              //console.log("toggle off element");
              // We're selecting the same segment label, so remove it
              var parentSegment = segmentLabel.parentNode;
              parentSegment.targetId = null;
              parentSegment.removeChild(segmentLabel);
              updateBackend(itemPath, null);
            } else {
              //console.log("update segement label");
              // Remove any existing segment label defined elsewhere
              if (oldSegmentLabel) {
                var parentSegment = oldSegmentLabel.parentNode;
                parentSegment.targetId = null;
                parentSegment.removeChild(oldSegmentLabel);
              }
              
              // Upate the segment label
              segmentLabel.innerText = docField1.title;
              segmentLabel.id = itemPath;
              placeSegmentLabel(elem, segmentLabel);
              updateBackend(itemPath, elem.getAttribute("data-value"));
            }
          }
        };
        newDropItem.onmouseover = function (e) {
          var fieldNameSelect = document.getElementById("field-name-select");
          var newDropComponent = fieldNameSelect.firstChild;
          var itemElems = newDropComponent.children;
          
          for (var i = 0; i < itemElems.length; i++) {
            var itemElem = itemElems[i];
            if (itemElem.classList.contains("covet")) {
              itemElem.classList.remove("covet");
              break;
            }
          }
          e.target.classList.add("covet"); 
        };
        newDropContent.appendChild(newDropItem);
      }
    }
    elem.appendChild(fieldNameSelect);
    fieldNameSelect.focus();
    fieldNameSelect.onkeydown = function (e) {
      var code = e.keyCode ? e.keyCode : e.which;
      if (code === 38 || code == 40) { // Up or down key
        var fieldNameSelect = document.getElementById("field-name-select");
        var newDropComponent = fieldNameSelect.firstChild;
        var itemElems = newDropComponent.children;
        var found = false;
        
        for (var i = 0; i < itemElems.length; i++) {
          var itemElem = itemElems[i];
          if (itemElem.classList.contains("covet")) {
            itemElem.classList.remove("covet");
            if (code == 38) {
              // Up key         
              if (i == 0) {
                i = itemElems.length - 1;
              } else {
                i = i - 1;
              }
            } else {
              // Up key         
              if (i == itemElems.length - 1) {
                i = 0;
              } else {
                i = i + 1;
              }
            }
            itemElems[i].classList.add("covet");
            found = true;
            break;
          }
        }
        if (!found) {
          if (code == 38) {
            i = itemElems.length - 1;
          } else {
            i = 0;
          }
          itemElems[i].classList.add("covet");
        }
        e.stopPropagation();
        return false;
      }
      //
    };
}

window.addEventListener('load', function(event) {
    var elems = document.getElementsByClassName('TEXT');
    for (i = 0; i < elems.length; i++) {
      var elem = elems[i];
      var elem2= elem.children[0];
      var letterSpacing = (elem.offsetWidth - elem2.offsetWidth) / elem2.innerText.length;
      elem2.style.letterSpacing = letterSpacing + "px";
      //console.log(".... " + elem2.innerText + " ... " + elem2.innerText.length + " .... " + elem.offsetWidth + " ..... " + elem2.offsetWidth + " = " + letterSpacing);
    }
    elems = document.getElementsByClassName('COMPANY_NUMBER');
    for (i = 0; i < elems.length; i++) {
      var elem = elems[i];
      var elem2= elem.children[0];
      var letterSpacing = (elem.offsetWidth - elem2.offsetWidth) / elem2.innerText.length;
      elem2.style.letterSpacing = letterSpacing + "px";
      //console.log(".... " + elem2.innerText + " ... " + elem2.innerText.length + " .... " + elem.offsetWidth + " ..... " + elem2.offsetWidth + " = " + letterSpacing);
    }
}, false);

// Close the dropdown if the user clicks outside of it
window.addEventListener('click', function(event) {
  if (!event.target.matches('.dropbtn')) {
      if (event.target.id == "field-name-select") {
        // do nothing
      } else {
        var fieldNameSelect = document.getElementById("field-name-select");
        if (fieldNameSelect) {
          fieldNameSelect.remove();
        }
      }
  }
}, false);
