/** 
 * Support for a searchable text field.
 */
function setToggleButton (e, checked) {
	let toggleButton = e.parentElement.querySelector("button.upDownButton");
	let dropList = e.parentElement.querySelector("div.dropList");
	let dropListItems = dropList.children;

	let n = countShowing(dropList);
	if (checked) {
		if (n == 0) {
    		toggleButton.checked = true; 
			toggleButton.classList.remove("upAndFilter")
			toggleButton.classList.add("up");
			toggleButton.disabled = false;
			toggleButton.setAttribute("title", "Hide all");
		} else {
    		toggleButton.checked = true; 
			toggleButton.classList.remove("up");
			toggleButton.classList.add("upAndFilter")
			toggleButton.disabled = false;
			toggleButton.setAttribute("title", "Filter");
		}			
	} else {
		if (n == dropListItems.length) {
    		toggleButton.checked = true; 
			toggleButton.classList.remove("up", "upAndFilter");
			toggleButton.disabled = true;
			toggleButton.removeAttribute("title");
		} else {
			toggleButton.checked = false; 
			toggleButton.classList.remove("up", "upAndFilter");
			toggleButton.disabled = false;
			toggleButton.setAttribute("title", "Show all");
		}
	}
}

function activateSearchInput(ev) {
	let searchInput = ev.target.parentElement;
	let oldActive = document.querySelector(".searchInput.active");
	if (oldActive) {
		searchInput.classList.remove("active");
	}
	searchInput.classList.add("active");
}

function deactivateSearchInput(ev) {
	let searchInput = ev.target.parentElement;

	if (ev.relatedTarget == null) {
		// Not transfering to an input control
	} else if (ev.relatedTarget.parentElement === searchInput) {
		// Focus going to a related element
	} else {
		searchInput.classList.remove("active");
	}
}

function toggleItemList (ev, e) {
	let dropList = e.parentElement.querySelector("div.dropList");
	let dropListItems = dropList.children;
	let toggleButton = e.parentElement.querySelector("button.upDownButton");

	if (toggleButton.checked) {
		filterItems(e);
	} else {
		dropList.classList.remove("someShowing");
		dropList.classList.add("allShowing");
		setToggleButton(e, true);
	}
    ev.stopPropagation();
}

function exactMatch (target, list) {
	for (var i = 0; i < list.length; i++) {
		if (target == list[i].innerText) {
			return list[i].getAttribute("data-value");
		}
	}
	return null;
}

function clearDropList (inputElem, inputBackground, dropList) {
	var elem = inputBackground;
	while (elem.firstChild) elem.removeChild(elem.firstChild);
	inputElem.style.paddingLeft = inputElem.paddingLeft;
	inputElem.style.width = inputElem.elemWidth + "px";
    
	let dropListItems = dropList.children;
	for (var i = 0; i < dropListItems.length; i++) {
		dropListItems[i].classList.remove("show", "highlight");
	}
	dropList.classList.remove("allShowing", "someShowing");
}

function filterItems (e) {
	let dropList = e.parentElement.querySelector("div.dropList");
	let dropListItems = dropList.children;
	let inputElem = e.parentElement.querySelector("input.visible");
	let inputShadow = e.parentElement.querySelector("input.shadow");
	let inputBackground = e.parentElement.querySelector("div.inputBackground");

	if (inputElem.value.length == 0) {
		clearDropList(inputElem, inputBackground, dropList);
		inputElem.classList.remove("error", "incomplete");
		inputElem.classList.add("required");
		inputShadow.value = "";
		setToggleButton(e, false);
		return;
	}
	let dataValue = exactMatch(inputElem.value, dropListItems);
	if (dataValue) {
		clearDropList(inputElem, inputBackground, dropList);
		inputElem.classList.remove("error", "incomplete", "required");
		inputShaddow.value = dataValue;
		setToggleButton(e, false);
		return;
	}
	partial = inputElem.value.toLowerCase();
	let singleValue;
	let n = 0;
	let x0 = 0;
	let i0 = 0;
	for (var i = 0; i < dropListItems.length; i++) {
		let t = dropListItems[i].innerText.toLowerCase();
		let x = t.indexOf(partial);
		if (x != -1) {
			singleValue = dropListItems[i].innerText;
			dataValue = dropListItems[i].getAttribute("data-value");
			x0 = x;
			i0 = i;
			n++;
		}
	}
	switch (n) {
	case 0 :
		// No match found
		clearDropList(inputElem, inputBackground, dropList);
		inputElem.classList.remove("incomplete", "required");
		inputElem.classList.add("error");
		inputShadow.value = "";
	    break;
	case 1 :
   		// Single value found
		var elem = inputBackground;
		while (elem.firstChild) elem.removeChild(elem.firstChild);
        
		var s0 = document.createTextNode(singleValue.substring(0, x0));
		var span0 = document.createElement("span");
		span0.appendChild(s0);
		
		var s1 = document.createTextNode(inputElem.value);
		var span1 = document.createElement("span");
		span1.style.visibility = "hidden";
		span1.appendChild(s1);

		var s2 = document.createTextNode(singleValue.substring(x0 + inputElem.value.length));
		var span2 = document.createElement("span");
		span2.appendChild(s2);
		
		elem.appendChild(span0);
		elem.appendChild(span1);
		elem.appendChild(span2);

		for (var i = 0; i < dropListItems.length; i++) {
			if (i == i0) {
				dropListItems[i].classList.add("show");
			} else {
				dropListItems[i].classList.remove("show", "highlight");
			}
		}
		let offsetLeft = span1.offsetLeft - inputElem.offsetLeft;
		inputElem.style.paddingLeft = offsetLeft + "px";
		inputElem.style.width = (inputElem.elemWidth - offsetLeft + 10) + "px";
	    inputElem.classList.remove("error", "incomplete", "required");
	    inputShadow.value = dataValue;
		break;
	default :
    	// Multiple values found
		var elem = inputBackground;
		while (elem.firstChild) elem.removeChild(elem.firstChild);
		inputElem.style.paddingLeft = inputElem.paddingLeft;
		inputElem.style.width = inputElem.elemWidth + "px";
		inputElem.classList.remove("error", "required");
		inputElem.classList.add("incomplete");
		inputShadow.value = "";

		for (var i = 0; i < dropListItems.length; i++) {
			let t = dropListItems[i].innerText.toLowerCase();
			let x = t.indexOf(partial);
			if (x == -1) {
				dropListItems[i].classList.remove("show", "highlight");
			} else {
				dropListItems[i].classList.add("show");
			}
		}
		break;
	}
	if (n == 0) {
		dropList.classList.remove("allShowing", "someShowing");
	} else if (n == dropListItems.length) {
		dropList.classList.remove("someShowing");
		dropList.classList.add("allShowing");
	} else {
		dropList.classList.remove("allShowing");
		dropList.classList.add("someShowing");
	}
	setToggleButton(e, false);
}

function countShowing (dropList) {
	let dropListItems = dropList.children;

	let n = 0;
	for (var i = 0; i < dropListItems.length; i++) {
		if (dropListItems[i].classList.contains("show")) {
			n++;
		}
	}
	return n;
}

function menuKeyHandler (ev, e) {
	var code = ev.keyCode ? ev.keyCode : ev.which;
	switch (code) {
	case 27 :			// ESC
	case 37 :			// Left arrow
		var toggleButton = e.parentElement.querySelector("button.upDownButton");
		if (toggleButton.checked) {
			// If drop list is showing, hide it
			toggleItemList(ev, e);
		} else {
            ev.stopPropagation();
		}
		break;
	case 39 :			// Right arrow
		var toggleButton = e.parentElement.querySelector("button.upDownButton");
		if (!toggleButton.checked) {
			// If drop list is not showing, show it
			toggleItemList(ev, e);
	        break;
		}
		// Else treat as a down arrow
		code = 40;
		// DROP THROUGH
	case 38 :			// Up arrow			
	case 40 :			// Down arrow
    	var dropList = e.parentElement.querySelector("div.dropList");
    	var dropListItems = dropList.children;
        
        var i = 0;
        while (i < dropListItems.length) {
        	let dropListItem = dropListItems[i];
            if (dropListItem.classList.contains("highlight")) {
            	dropListItem.classList.remove("highlight");
            	break;
            }
            i++;
        }
        var allShowing = dropList.classList.contains("allShowing");
        if (i < dropListItems.length) {
        	// We found a highlight item
        	(code == 38) ? i-- : i++;
            while (i >= 0 && i < dropListItems.length) {
               	dropListItem = dropListItems[i];
               	if (allShowing || dropListItem.classList.contains("show")) {
                   	dropListItem.classList.add("highlight");
                   	break;
               	}
            	(code == 38) ? i-- : i++;
            }
        } else {
        	// There is no highlight item
        	(code == 38) ? i = dropListItems.length - 1 : i = 0;
            while (i >= 0 && i < dropListItems.length) {
              	dropListItem = dropListItems[i];
               	if (allShowing || dropListItem.classList.contains("show")) {
                   	dropListItem.classList.add("highlight");
                   	break;
               	}
            	(code == 38) ? i-- : i++;
            }
       	}
        ev.stopPropagation();
        break;
	case 9 :			// Tab
		if (!ev.shiftKey) {
			var inputElem = e.parentElement.querySelector("input.visible");
			var inputShadow = e.parentElement.querySelector("input.shadow");
			var inputBackground = e.parentElement.querySelector("div.inputBackground");
    		var dropList = e.parentElement.querySelector("div.dropList");
    		var dropListItems = dropList.children;
        	
        	var i = 0;
        	while (i < dropListItems.length) {
        		let dropListItem = dropListItems[i];
            	if (dropListItem.classList.contains("highlight")) {
            		dropListItem.classList.remove("highlight");
            		break;
            	}
            	i++;
        	}
			if (i < dropListItems.length) {
				// Use the highlighted value as the input value
		    	inputElem.value = dropListItems[i].innerText;
				inputShadow.value = dropListItems[i].getAttribute("data-value");
		    	inputElem.classList.remove("error", "incomplete", "required");
			} else {
				// Look for a partial match with only one result
	    		let partial = inputElem.value.toLowerCase();
    			let singleValue;
    			let n = 0;
    			let i0 = 0;
    			for (var i = 0; i < dropListItems.length; i++) {
    				let t = dropListItems[i].innerText.toLowerCase();
    				let x = t.indexOf(partial);
    				if (x != -1) {
    					singleValue = dropListItems[i].innerText;
      					i0 = i;
    					n++;
    				}
    			}
    		
        		if (n == 1) {
        			// We found a matching item (i.e. the partial matches a value)
			    	inputElem.value = dropListItems[i0].innerText;
                   	inputShadow.value = dropListItems[i0].getAttribute("data-value");
			    	inputElem.classList.remove("error", "incomplete", "required");
        		} else {
        			inputShadow.value = "";
     		}
			}
    		clearDropList(inputElem, inputBackground, dropList);
    		setToggleButton(e, false);
    		let searchInput = document.querySelector(".searchInput.active");
    		if (searchInput) {
       			searchInput.classList.remove("active");
    		}
		}
		break;
	}
}

function setSizeOfSearchInput () {
	let searchInputs = document.querySelectorAll(".searchInput");
	for (var i = 0; i < searchInputs.length; i++) {
		let searchInput = searchInputs[i];
		let dropList = searchInput.querySelector("div.dropList");
		dropList.classList.add("calcSize");
		let inputElem = searchInput.querySelector("input.visible");
		let inputBackground = searchInput.querySelector("div.inputBackground");
		elemWidth = dropList.clientWidth;   // width with padding
		
		// Remove padding width
		let computedStyle = getComputedStyle(inputElem);
		elemWidth -= parseFloat(computedStyle.paddingLeft) + parseFloat(computedStyle.paddingRight);
		
		inputElem.style.width = elemWidth + "px";
		inputBackground.style.width = elemWidth + "px";
		inputElem.elemWidth = elemWidth;
		// The padding left includes the "px" suffix.
		inputElem.paddingLeft = computedStyle.paddingLeft;
		
		let upDownButton = searchInput.querySelector("button.upDownButton");
		upDownButton.style.left = dropList.offsetWidth + "px";
		let inputHeight = inputElem.offsetHeight;
		upDownButton.style.width = inputHeight + "px";
		upDownButton.style.height = inputHeight + "px";
		dropList.classList.remove("calcSize");
		
		searchInput.style.height = inputHeight + "px";
	}
}

function selectItem (e) {
	let searchInput = e.parentElement.parentElement;
	let inputElem = searchInput.querySelector("input.visible");
	let inputShadow = searchInput.querySelector("input.shadow");
	let inputBackground = searchInput.querySelector("div.inputBackground");
	let dropList = e.parentElement;
	let dropListItems = dropList.children;
	
	// Remove any highlight
	let highlighted = dropList.querySelector("div.highlight");
	if (highlighted) {
		highlighted.classList.remove("highlight");
	}
	inputElem.value = e.innerText;
	inputElem.classList.remove("error", "incomplete", "required");
	inputShadow.value = e.getAttribute("data-value");
	clearDropList(inputElem, inputBackground, dropList);
	setToggleButton(e.parentElement, false);
	
	let activeElement = document.activeElement;
	if (inputElem === activeElement) {
		// Don't deactive the search input
	} else {
		upDownButton = searchInput.querySelector("button.upDownButton");
		if (upDownButton === activeElement) {
			// Ditto
		} else {
			searchInput.classList.remove("active");
		}
	}
}
// function findNextTabStop(e) {
// 	// This method does not account for tabindex order, or element visibility
//     var universe = document.querySelectorAll('input, button, select, textarea, a[href]');
//     var list = Array.prototype.filter.call(universe, function(item) {return item.tabIndex >= 0});
//     for (var i = 0; i < list.length; i++) {
//     	console.log(i + ":")
//     	console.log(list[i]);
//     }
//     var index = list.indexOf(e);
//     return list[index + 1] || list[0];
// }
  
//function menuMouseWheelHandler (ev) {
//	var delta = Math.max(-1, Math.min(1, (ev.wheelDelta || -ev.detail)));
//	console.log("mouse wheel handler: delta " + delta);
//	return false;
//}

// Close the dropdown if the user clicks outside of it
window.addEventListener("click", function(event) {
	let searchInput = document.querySelector(".searchInput.active");
	if (searchInput) {
		if (event.target.matches(".searchInput.active input.visible")) {
			// On the search input field, so do nothing
			return;
		}
		if (event.target.matches(".searchInput.active button.upDownButton")) {
			// On the up/down button, so do nothing
			return;
		}
	  	if (event.target.matches(".searchInput.active div.dropList")) {
		  	// On the drop down list, so do nothing
		  	return;
	  	}
		searchInput.classList.remove("active");
	}
});
 