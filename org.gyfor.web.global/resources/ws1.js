function websocketStart(baseURL, arg, onOpenFunction) {
	if (window.WebSocket) {
		let websocketURL = "ws://" + baseURL + "/" + arg;
		let websocket2 = new WebSocket(websocketURL);
		websocket2.onmessage = function(event) {
			console.log("websocket " + websocketURL + ": onmessage");
			//alert("On message: " + event.data);
			let n1 = event.data.indexOf("|");
			if (n1 == -1) {
				// Bad message
				return;
			}
			let action = event.data.substring(0, n1);
			let n2 = event.data.indexOf("|", n1 + 1);
			if (n2 == -1) {
				// Bad message
				return;
			}
			let selector = event.data.substring(n1 + 1, n2);
			let htmlSource = event.data.substring(n2 + 1);
			console.log("onmessage " + action + " " + selector + " " + htmlSource);
			
			let container;
			let dx;
			let children;
			let event1;
			
			switch (action) {
			case "addChild" : 		
			case "addChildren" : 		
				// Add the html to the specified container (identified by containerSelector).
				// The html can be multiple elements.  A wschange event is fired after all
				// children have been added.
				////console.log ("addChildren: " + selector + ": " + htmlSource);
				container = document.querySelector(selector);
  				
				dx = document.createElement('div');
  				dx.innerHTML = htmlSource;
  				children = dx.childNodes;
    			while (children.length > 0) {
    				container.appendChild(children[0]);
    			}
			    event1 = new Event('wschange');
       	        container.dispatchEvent(event1);
    			break;
			case "replaceNode" :
				// Remove the existing element (identified by node selector), then add the 
				// html in its place.  The html can be multiple elements.  This action does
				// NOT fire a wschange event.
				////console.log ("replaceNode: " + selector + ": " + htmlSource);
				let node = document.querySelector(selector);
				
				if (node && node.parentNode) {
					container = node.parentNode;
					container.removeChild(node);

					dx = document.createElement('div');
	  				dx.innerHTML = htmlSource;
	  				children = dx.childNodes;
	    			while (children.length > 0) {
	    				container.appendChild(children[0]);
	    			}
				}
				break;
			case "replaceChildren" :
				// Remove the children of the existing element (identified by container selector), then add the 
				// html in its place.  The html can be multiple elements. A wschange event is fired after all
				// children have been replaced.
				console.log ("replaceChildren: " + selector + ": " + htmlSource);
				container = document.querySelector(selector);
				if (container) {
				    let last;
				    while (last = container.lastChild) container.removeChild(last);

  				    dx = document.createElement('div');
  				    dx.innerHTML = htmlSource;  				    
  				    children = dx.childNodes;
    				while (children.length > 0) {
    					container.appendChild(children[0]);
    				}
    			    event1 = new Event('wschange');
           	        container.dispatchEvent(event1);
				} else {
					console.log("replaceChildren: cannot locate: " + selector);
				}
				break;
			case "deleteChildren" :
				// Remove the child nodes of the node that matches the container selector.
				// A wschange event is fired after child nodes have been removed.
				container = document.querySelector(selector);
			    let last;
			    while (last = container.lastChild) container.removeChild(last);
			    event1 = new Event('wschange');
       	        container.dispatchEvent(event1);
				break;
			case "deleteNodes" : 
				// Removing those nodes that match the node selector.  The container
				// selector is not used.  This action does NOT fire a wschange event.
				let matches = document.querySelectorAll(selector);
				for (let i = 0; i < matches.length; i++) {
					let node = matches[i];
					if (node.parentNode) {
						node.parentNode.removeChild(node);
					}
				}
				break;
			default :
				alert("Unknown action: " + action);
				break;
			}
		};
		websocket2.onopen = function(event) {
			console.log("websocket " + websocketURL + ": onopen");
			if (onOpenFunction) {
				onOpenFunction(websocket);
			}
		};
		websocket2.onclose = function(event) {
			console.log("websocket " + websocketURL + ": onclose");
		};
		websocket2.onerror = function(event) {
			console.log("websocket " + websocketURL + ": onerror " + event);
		};
		return websocket2;
	} else {
		alert("Your browser does not support Websockets :-(");
		return null;
	}
}


function sendMessage(websocket2, message) {
	if (!websocket2) {
		return;
	}
	if (websocket2.readyState == WebSocket.OPEN) {
		//alert("Sending message: " + message);
		websocket2.send(message);
	} else {
		// alert("The socket is not open.");
	}
}
