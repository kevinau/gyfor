/**
 * Support for a searchable text field.
 */
class SearchInput {

	static getDropListChildren (dropList, s, callback) {
		//console.log("::::::::::::::: " + dropList.getAttribute("data-loaded") + "  " + dropList.classList.contains("data-loaded") + " " + dropList + "  " + s);

//		if (dropList.getAttribute("data-loaded") != "true") {
//			//console.log("Data loaded is not true");
//			let priorOnload = dropList.onload;
//			if (priorOnload) {
//				dropList.onload = function(node) {
//					callback(node.children);
//					priorOnload(node.children);
//				};
//			} else {
//				dropList.onload = function(node) {
//					//console.log(".............. droplist onload called");
//					dropList.setAttribute("data-loaded", "true");
//					//console.log("............... " + dropList.getAttribute("data-loaded"));
//					dropList.classList.add("data-loaded");
//					SearchInput.setSizeOfSearchInput(node.parentElement);
//					callback(node.children);
//				};
//				// Get a list of descriptions (but from what channel?)
//				doSend ("getDescriptions");
//			}
//		} else {
			console.log("Call back using existing dropList: " + dropList.children.length);
//			SearchInput.setSizeOfSearchInput(dropList.parentElement);
			callback(dropList.children);
//		}
	}

	static setShadowValue (shadowElem, value) {
		let v = shadowElem.value;
		if (v != value) {
			shadowElem.value = value;
			//console.log("Shadow value set to: " + value);
		}
	}

	// All calls to setToggleButton should be protected, and only called
	// within a callback from getDropListChildren
	static setToggleButton (e, checked) {
		let toggleButton = e.parentElement.querySelector("button.upDownButton");
		let dropList = e.parentElement.querySelector("div.dropList");
		let dropListItems = dropList.children;

		let n = SearchInput.countShowing(dropList);
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

	// All calls to countShowing should be protected, and only called
	// within a callback from getDropListChildren
	static countShowing (dropList) {
		let dropListItems = dropList.children;
		let n = 0;
		for (let i = 0; i < dropListItems.length; i++) {
			if (dropListItems[i].classList.contains("show")) {
				n++;
			}
		}
		return n;
	}

	static activateSearchInput(ev) {
		console.log(">>> activateSearchInput");
		let searchInput = ev.target.parentElement;
		let dropList = searchInput.querySelector("div.dropList");
		SearchInput.getDropListChildren(dropList, 12, function(dropListItems) {
			// Drop list items are not used, but the above preloads the search input
			// list
			let oldActive = document.querySelector(".searchInput.active");
			if (oldActive) {
				searchInput.classList.remove("active");
			}
			searchInput.classList.add("active");
		});
		SearchInput.filterItems2(ev.target);
	}

	static deactivateSearchInput(ev) {
		let searchInput = ev.target.parentElement;

		if (ev.relatedTarget == null) {
			// Not transfering to an input control
		} else if (ev.relatedTarget.parentElement === searchInput) {
			// Focus going to a related element
		} else {
			searchInput.classList.remove("active");
		}
	}

	static toggleItemList2 (ev, e) {
		let dropList = e.parentElement.querySelector("div.dropList");
		SearchInput.getDropListChildren(dropList, 2, function(dropListItems) {
			SearchInput.toggleItemList(ev, e);
		});
	}

	static toggleItemList (ev, e) {
		let toggleButton = e.parentElement.querySelector("button.upDownButton");
		let dropList = e.parentElement.querySelector("div.dropList");
		let dropListItems = dropList.children;
		if (toggleButton.checked) {
			SearchInput.filterItems(e);
		} else {
			dropList.classList.remove("someShowing");
			dropList.classList.add("allShowing");
			SearchInput.setToggleButton(e, true);
		}
		ev.stopPropagation();
	}

	static exactMatch (target, list) {
		for (let i = 0; i < list.length; i++) {
			if (target == list[i].innerText) {
				return list[i].getAttribute("data-value");
			}
		}
		return null;
	}


	//	All calls to clearDropList should be protected, and only called
	//	within a callback from getDropListChildren
	static clearDropList (inputElem, inputBackground, dropList) {
		let elem = inputBackground;
		while (elem.firstChild) elem.removeChild(elem.firstChild);
		console.log("padding left " + inputElem.paddingLeft);
		
		inputElem.style.paddingLeft = "6px"; //null; //inputElem.paddingLeft;
		inputElem.style.width = (inputElem.elemWidth - 6) + "px";

		let dropListItems = dropList.children;
		for (let i = 0; i < dropListItems.length; i++) {
			dropListItems[i].classList.remove("show", "highlight");
		}
		dropList.classList.remove("allShowing", "someShowing");
	}


	static filterItems2 (e) {
		let dropList = e.parentElement.querySelector("div.dropList");
		SearchInput.getDropListChildren(dropList, 11, function(dropListItems) {
			SearchInput.filterItems(e);
		});
	}


	//	All calls to filterItems should be protected, and only called
	//	within a callback from getDropListChildren
	static filterItems (e) {
		let dropList = e.parentElement.querySelector("div.dropList");
		let inputElem = e.parentElement.querySelector("input.visible");
		let inputShadow = e.parentElement.querySelector("input.shadow");
		let inputBackground = e.parentElement.querySelector("div.inputBackground");

		let dropListItems = dropList.children;
		if (inputElem.value.length == 0) {
			SearchInput.clearDropList(inputElem, inputBackground, dropList);
			inputElem.classList.remove("error", "incomplete");
			inputElem.classList.add("required");
			SearchInput.setToggleButton(e, false);
			SearchInput.setShadowValue(inputShadow, "");
			return;
		}
		let dataValue = SearchInput.exactMatch(inputElem.value, dropListItems);
		if (dataValue) {
			SearchInput.clearDropList(inputElem, inputBackground, dropList);
			inputElem.classList.remove("error", "incomplete", "required");
			SearchInput.setToggleButton(e, false);
			SearchInput.setShadowValue(inputShadow, dataValue);
			return;
		}
		let partial = inputElem.value.toLowerCase();
		let singleValue;
		let n = 0;
		let x0 = 0;
		let i0 = 0;
		for (let i = 0; i < dropListItems.length; i++) {
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
			SearchInput.clearDropList(inputElem, inputBackground, dropList);
			inputElem.classList.remove("incomplete", "required");
			inputElem.classList.add("error");
			SearchInput.setShadowValue(inputShadow, "");
			break;
		case 1 :
			// Single value found
			let elem = inputBackground;
			while (elem.firstChild) elem.removeChild(elem.firstChild);

			let s0 = document.createTextNode(singleValue.substring(0, x0));
			let span0 = document.createElement("span");
			span0.appendChild(s0);

			let s1 = document.createTextNode(inputElem.value);
			let span1 = document.createElement("span");
			span1.style.visibility = "hidden";
			span1.appendChild(s1);

			let s2 = document.createTextNode(singleValue.substring(x0 + inputElem.value.length));
			let span2 = document.createElement("span");
			span2.appendChild(s2);

			elem.appendChild(span0);
			elem.appendChild(span1);
			elem.appendChild(span2);

			for (let i = 0; i < dropListItems.length; i++) {
				if (i == i0) {
					dropListItems[i].classList.add("show");
				} else {
					dropListItems[i].classList.remove("show", "highlight");
				}
			}
			let offsetLeft = span1.offsetLeft - inputElem.offsetLeft;
			inputElem.style.paddingLeft = offsetLeft + "px";
			inputElem.style.width = (inputElem.elemWidth - offsetLeft) + "px";
			inputElem.classList.remove("error", "incomplete", "required");
			SearchInput.setShadowValue(inputShadow, dataValue);
			break;
		default :
			// Multiple values found
			let elem2 = inputBackground;
			while (elem2.firstChild) elem2.removeChild(elem2.firstChild);
			inputElem.style.paddingLeft = "6px"; //null; //inputElem.paddingLeft;
			inputElem.style.width = (inputElem.elemWidth - 6) + "px";
			inputElem.classList.remove("error", "required");
			inputElem.classList.add("incomplete");

			for (let i = 0; i < dropListItems.length; i++) {
				let t = dropListItems[i].innerText.toLowerCase();
				let x = t.indexOf(partial);
				if (x == -1) {
					dropListItems[i].classList.remove("show", "highlight");
				} else {
					dropListItems[i].classList.add("show");
				}
			}
			SearchInput.setShadowValue(inputShadow, "");
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
		SearchInput.setToggleButton(e, false);
	}

	static menuKeyHandler (ev, e) {
		console.log(">>> menuKeyHandler " + ev + "  " + e);
		let dropList = e.parentElement.querySelector("div.dropList");
		SearchInput.getDropListChildren(dropList, 6, function(dropListItems) {
			let code = ev.keyCode ? ev.keyCode : ev.which;
			let toggleButton = 0;
			
			switch (code) {
			case 27 :			// ESC
			case 37 :			// Left arrow
				toggleButton = e.parentElement.querySelector("button.upDownButton");
				if (toggleButton.checked) {
					// If drop list is showing, hide it
					SearchInput.toggleItemList(ev, e);
				} else {
					ev.stopPropagation();
				}
				break;
			case 39 :			// Right arrow
				toggleButton = e.parentElement.querySelector("button.upDownButton");
				if (!toggleButton.checked) {
					// If drop list is not showing, show it
					SearchInput.toggleItemList(ev, e);
					break;
				}
				// Else treat as a down arrow
				code = 40;
				// DROP THROUGH
			case 38 :			// Up arrow
			case 40 :			// Down arrow
				let i = 0;
				while (i < dropListItems.length) {
					let dropListItem = dropListItems[i];
					if (dropListItem.classList.contains("highlight")) {
						dropListItem.classList.remove("highlight");
						break;
					}
					i++;
				}
				let dropList = e.parentElement.querySelector("div.dropList");
				let allShowing = dropList.classList.contains("allShowing");
				if (i < dropListItems.length) {
					// We found a highlight item
					(code == 38) ? i-- : i++;
					while (i >= 0 && i < dropListItems.length) {
						let dropListItem = dropListItems[i];
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
						let dropListItem = dropListItems[i];
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
			case 13 :			// Enter
				if (!ev.shiftKey) {
					let inputElem = e.parentElement.querySelector("input.visible");
					let inputShadow = e.parentElement.querySelector("input.shadow");
					let inputBackground = e.parentElement.querySelector("div.inputBackground");
					let dropList = e.parentElement.querySelector("div.dropList");
					let i = 0;
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
						inputElem.classList.remove("error", "incomplete", "required");
						SearchInput.setShadowValue(inputShadow, dropListItems[i].getAttribute("data-value"));
					} else {
						// Look for a partial match with only one result
						let partial = inputElem.value.toLowerCase();
						let singleValue;
						let n = 0;
						let i0 = 0;
						for (let i = 0; i < dropListItems.length; i++) {
							let t = dropListItems[i].innerText.toLowerCase();
							let x = t.indexOf(partial);
							if (x != -1) {
								singleValue = dropListItems[i].innerText;
								i0 = i;
								n++;
							}
						}
						if (n == 1) {
							// We found a matching item (i.e. the partial matches a
							// value)
							inputElem.value = dropListItems[i0].innerText;
							inputElem.classList.remove("error", "incomplete", "required");
							SearchInput.setShadowValue(inputShadow, dropListItems[i0].getAttribute("data-value"));
						} else {
							SearchInput.setShadowValue(inputShadow, "");
						}
					}
					SearchInput.clearDropList(inputElem, inputBackground, dropList);
					SearchInput.setToggleButton(e, false);
					let searchInput = document.querySelector(".searchInput.active");
					if (searchInput) {
						searchInput.classList.remove("active");
					}
				}
				break;
			}
		});
	}

	static setSizeOfSearchInput (searchInput) {
		let dropList = searchInput.querySelector("div.dropList");
		console.log("Before calc " + dropList.clientWidth);
		dropList.classList.add("calcSize");
		let inputElem = searchInput.querySelector("input.visible");
		let inputBackground = searchInput.querySelector("div.inputBackground");
		let elemWidth = dropList.clientWidth;   // width with padding
		console.log("Calulating size of search input to " + elemWidth);

		// Remove padding width
		let computedStyle = getComputedStyle(inputBackground);
		elemWidth -= parseFloat(computedStyle.paddingLeft) + parseFloat(computedStyle.paddingRight);
		console.log("Without padding:" + elemWidth);

		inputElem.style.paddingLeft = "6px"; //null; //inputElem.paddingLeft;
		inputElem.style.width = (elemWidth - 6) + "px";
		inputBackground.style.width = elemWidth + "px";
		inputElem.elemWidth = elemWidth;
		// The padding left includes the "px" suffix.
		// inputElem.paddingLeft = computedStyle.paddingLeft;

		let upDownButton = searchInput.querySelector("button.upDownButton");
		// upDownButton.style.left = dropList.offsetWidth + "px";
		let inputHeight = inputElem.offsetHeight;
		// upDownButton.style.width = inputHeight + "px";
		// upDownButton.style.height = inputHeight + "px";
		dropList.classList.remove("calcSize");

		// searchInput.style.height = inputHeight + "px";
	}

	static selectItem (e) {
		let searchInput = e.parentElement.parentElement;
		let inputElem = searchInput.querySelector("input.visible");
		let inputShadow = searchInput.querySelector("input.shadow");
		let inputBackground = searchInput.querySelector("div.inputBackground");
		let dropList = e.parentElement;
		SearchInput.getDropListChildren(dropList, 9, function(dropListItems) {
			// Remove any highlight
			let highlighted = dropList.querySelector("div.highlight");
			if (highlighted) {
				highlighted.classList.remove("highlight");
			}
			inputElem.value = e.innerText;
			inputElem.classList.remove("error", "incomplete", "required");
			SearchInput.clearDropList(inputElem, inputBackground, dropList);
			SearchInput.setToggleButton(e.parentElement, false);

			let activeElement = document.activeElement;
			if (inputElem === activeElement) {
				// Don't deactive the search input
			} else {
				let upDownButton = searchInput.querySelector("button.upDownButton");
				if (upDownButton === activeElement) {
					// Ditto
				} else {
					searchInput.classList.remove("active");
				}
			}
			//console.log("select item: " + e.getAttribute("data-value"));
			SearchInput.setShadowValue(inputShadow, e.getAttribute("data-value"));
		});
	}

	
	//	Close the drop down if the user clicks outside of it
	static outsideClick (event) {
		//console.log("click event handler");
		let searchInput = document.querySelector(".searchInput.active");
		if (searchInput) {
			if (event.target.matches(".searchInput.active input.visible")) {
				// On the search input field, so do nothing
				//console.log("over input.visible");
				return;
			}
			if (event.target.matches(".searchInput.active button.upDownButton")) {
				//console.log("over up/down button");
				// On the up/down button, so do nothing
				return;
			}
			if (event.target.matches(".searchInput.active div.dropList")) {
				// On the drop down list, so do nothing
				//console.log("over dropList:");
				return;
			}
			// if (event.target.matches(".searchInput.active div.dropList div")) {
			// On the drop down list item, so select it
			// selectItem(event.target);
			// return;
			// }
			searchInput.classList.remove("active");
		}
	}

	
	static init (websocket, searchInputId) {
		let searchInput = document.getElementById(searchInputId);
		console.log("Init search input by sending message via websockets");
		console.log("Search input: " + searchInput.getAttribute("data-args"));
		let dropList = searchInput.querySelector("div.dropList");
		dropList.addEventListener('wschange', function (e) {
			SearchInput.setSizeOfSearchInput(searchInput);
		}, false);
		let args = searchInput.getAttribute("data-args");
	    console.log("getAllDescriptions|#" + dropList.id + "|" + args);
	    sendMessage(websocket, "getAllDescriptions|#" + dropList.id + "|" + args);
	}

}
