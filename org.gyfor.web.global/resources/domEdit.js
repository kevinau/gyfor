	function addChildren(selector, order, htmlSource) {
				// Add the html to the specified container (identified by containerSelector).
				// The html can be multiple elements.  A wschange event is fired after all
				// children have been added.
				console.log ("addChildren: " + selector + ": " + htmlSource);
				let container = document.querySelector(selector);
  				if (!container) {
  					console.log("Cannot find DOM element matching selector: " + selector);
  					return;
  				}
  				
  				// Find insert location
  				let insertElem = null;
  				if (container.hasChildNodes) {
  					let nodeList = container.childNodes;
  					for (let i = 0; i < nodeList.length; i++) {
  						if (nodeList[i] instanceof Element) {
    						let childOrder = nodeList[i].getAttribute("data-order");
  	    					if (childOrder > order) {
  		    					insertElem = nodeList[i];
  			    				break;
  					    	}
  						}
  					}
  				}
  				
				let dx = document.createElement(container.tagName);
  				dx.innerHTML = htmlSource;
  				let children = dx.childNodes;
    			while (children.length > 0) {
    				if (children[0] instanceof Element) {
    					children[0].setAttribute("data-order", order);
    				}
    				container.insertBefore(children[0], insertElem);
    			}
    			dx.remove();
			    let event1 = new Event('wschange');
       	        container.dispatchEvent(event1);
	}

	function replaceNode(selector, htmlSource) {
				// Remove the existing element (identified by node selector), then add the 
				// html in its place.  The html can be multiple elements.  This action does
				// NOT fire a wschange event.
				////console.log ("replaceNode: " + selector + ": " + htmlSource);
				let node = document.querySelector(selector);
				
				if (node && node.parentNode) {
					let container = node.parentNode;
					container.removeChild(node);

					let dx = document.createElement('div');
	  				dx.innerHTML = htmlSource;
	  				let children = dx.childNodes;
	    			while (children.length > 0) {
	    				container.appendChild(children[0]);
	    			}
				}
	}

	function syncChildren(parentSelector, nodeSelector, index, htmlSource) {
		console.log ("syncChildren: " + nodeSelector + ": " + htmlSource);
		let node = document.querySelector(nodeSelector);
		if (node) {
			replaceChildren(nodeSelector, htmlSource);
		} else {
			addChildren(parentSelector, index, htmlSource);
		}
	}
	
	
	function replaceChildren(selector, htmlSource) {
				// Remove the children of the existing element (identified by container selector), then add the 
				// html in its place.  The html can be multiple elements. A wschange event is fired after all
				// children have been replaced.
				console.log ("replaceChildren: " + selector + ": " + htmlSource);
				let container = document.querySelector(selector);
				if (container) {
				    let last;
				    while (last = container.lastChild) container.removeChild(last);

  				    let dx = document.createElement('div');
  				    dx.innerHTML = htmlSource;  				    
  				    let children = dx.childNodes;
    				while (children.length > 0) {
    					container.appendChild(children[0]);
    				}
    			    let event1 = new Event('wschange');
           	        container.dispatchEvent(event1);
				} else {
					console.log("replaceChildren: cannot locate: " + selector);
				}
	}
	
	function deleteChildren(selector) {
				// Remove the child nodes of the node that matches the container selector.
				// A wschange event is fired after child nodes have been removed.
				let container = document.querySelector(selector);
			    let last;
			    while (last = container.lastChild) container.removeChild(last);
			    let event1 = new Event('wschange');
       	        container.dispatchEvent(event1);
	}
	
	function deleteNodes(selector) {
				// Removing those nodes that match the node selector.  The container
				// selector is not used.  This action does NOT fire a wschange event.
				let matches = document.querySelectorAll(selector);
				for (let i = 0; i < matches.length; i++) {
					let node = matches[i];
					if (node.parentNode) {
						node.parentNode.removeChild(node);
					}
				}
	}

	
	function setValue(nodeId, value) {
		console.log("set value: " + nodeId + " " + value);
		let elem = document.querySelector("#node" + nodeId + " td.input input");
		elem.value = value;
	}
	
	
	function noteError(nodeId, errorType, message) {
		console.log("note error: " + nodeId + " " + errorType + " " + message);
		let elem = document.querySelector("#node" + nodeId + " td.input");
		elem.setAttribute("data-status", errorType);
		let elem2 = elem.querySelector("span.message");
		if (elem2) {
			//console.log("existing span.message");
			elem2.textContent = message;
		} else {
			//console.log("no existing span.message");
			let dx = document.createElement('span');
			dx.classList.add("message");
			dx.textContent = message;
			elem2 = elem.parentNode.querySelector("div.status");
			elem2.appendChild(dx);
		}
		adjustButton(elem);
	}

	
	function clearError(nodeId) {
		let elem = document.querySelector("#node" + nodeId + " td.input");
		let classList = elem.classList;
		elem.removeAttribute("data-status");
		classList.remove("error", "warning", "incomplete");
		let msgElem = elem.querySelector("span.message");
		if (msgElem) {
			msgElem.parentNode.removeChild(msgElem);
		}
		adjustButton(elem);
	}


	function adjustButton(elem) {
		// Find nodeId of the enclosing form element.
		let formElem = elem.parentNode;
		while (formElem.tagName != "FORM") {
			formElem = formElem.parentNode;
		}
		// Find the action button
		let actionButton = formElem.querySelector("button.action");
		
		// Are there any items with errors?
		let errors = formElem.querySelector("td.input[data-status]");
		if (errors) {
			actionButton.disabled = true;
		} else {
			actionButton.disabled = false;
		}
	}

	
	function setEntryMode(nodeId, mode) {
		console.log("set entry mode: " + nodeId + " " + mode);
		let elem = document.querySelector("#node" + nodeId);
		if (elem) {
    		console.log("............... " + elem + "  " + nodeId);
	    	let elem2 = elem.querySelector("td.input input");
		    console.log("............... " + elem2.tagName);
		    switch (mode) {
		    case "HIDDEN" :
		    	// Change the item elements
		    	elem.classList.add("hidden");
		    	// Remove the other modes from the input elements
		    	elem2.classList.remove("view", "enabled", "disabled");
		    	break;
		    case "VIEW" :
		    	elem.classList.remove("hidden");
		    	elem2.classList.add("view");
		    	elem2.classList.remove("enabled", "disabled");
		    	break;
		    case "ENABLED" :
		    	elem.classList.remove("hidden");
		    	elem2.classList.add("enabled");
		    	elem2.classList.remove("view", "disabled");
		    	break;
		    case "DISABLED" :
		    	elem.classList.remove("hidden");
		    	elem2.classList.add("disabled");
		    	elem2.classList.remove("view", "enabled");
		    	break;
		    default :
		    	console.log("Unknown mode type: " + mode);
		    }
		}
	}
	
	function changeAction(name, available) {
		console.log("changeAction " + name + " " + available);
		let matches = document.querySelectorAll("button[value=" + name + "]");
		console.log("changeAction " + name + " " + available + " " + matches.length);
		for (let i = 0; i < matches.length; i++) {
			let node = matches[i];
			if (available == "true") {
				node.removeAttribute("disabled");
				node.classList.remove("hidden");
			} else {
				if (node.getAttribute("data-show") != "always") {
					node.classList.add("hidden");
				}
				node.setAttribute("disabled", "");
			}
		}
	}
	
	function setTitle(title) {
		document.title = title;
	}


export {addChildren, syncChildren, replaceNode, replaceChildren, deleteChildren, deleteNodes,
	    setValue, noteError, clearError, setEntryMode, setTitle, changeOption};
