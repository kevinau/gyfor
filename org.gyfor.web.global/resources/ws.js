function websocketStart(baseURL, arg, onOpenCommand) {
	if (window.WebSocket) {
		var websocketURL = "ws:" + baseURL + "/" + arg;
		websocket = new WebSocket(websocketURL);
		websocket.onmessage = function(event) {
			console.log("websocket " + websocketURL + ": onmessage");
			//alert("On message: " + event.data);
			var n1 = event.data.indexOf("|");
			if (n1 == -1) {
				// Bad message
				return;
			}
			var action = event.data.substring(0, n1);
			var n2 = event.data.indexOf("|", n1 + 1);
			if (n2 == -1) {
				// Bad message
				return;
			}
			var selector = event.data.substring(n1 + 1, n2);
			var htmlSource = event.data.substring(n2 + 1);
			console.log("onmessage " + action + " " + selector + " " + htmlSource);
			
			switch (action) {
			case "addChild" : 		
			case "addChildren" : 		
				// Add the html to the specified container (identified by containerSelector).
				// The html can be multiple elements.
				////console.log ("addChildren: " + selector + ": " + htmlSource);
				var container = document.querySelector(selector);
  				
				var dx = document.createElement('div');
  				dx.innerHTML = htmlSource;
  				var children = dx.childNodes;
    			for (var i = 0; i < children.length; i++) {
    				container.append(children[i]);
    			}
    			break;
			case "replaceNode" :
				// Remove the existing element (identified by node selector), then add the 
				// html in its place.  The html can be multiple elements.
				////console.log ("replaceNode: " + selector + ": " + htmlSource);
				var node = document.querySelector(selector);
				
				if (node && node.parentNode) {
					var container = node.parentNode;
					container.removeChild(node);

					var dx = document.createElement('div');
	  				dx.innerHTML = htmlSource;
	  				var children = dx.childNodes;
	    			for (var i = 0; i < children.length; i++) {
	    				container.append(children[i]);
	    			}
				}
				break;
			case "replaceChildren" :
				// Remove the children of the existing element (identified by container selector), then add the 
				// html in its place.  The html can be multiple elements.
				////console.log ("replaceChildren: " + selector + ": " + htmlSource);
				var container = document.querySelector(selector);
				if (container) {
				    var last;
				    while (last = container.lastChild) container.removeChild(last);

  				    var dx = document.createElement('div');
  				    dx.innerHTML = htmlSource;  				    
  				    var children = dx.childNodes;
    				for (var i = 0; i < children.length; i++) {
    					container.append(children[i]);
    				}
				}
				break;
			case "deleteChildren" :
				// Remove the child nodes of the node that matches the container selector.
				var container = document.querySelector(selector);
			    var last;
			    while (last = container.lastChild) container.removeChild(last);
				break;
			case "deleteNodes" : 
				// Removing those nodes that match the node selector.  The container
				// selector is not used.
				var matches = document.querySelectorAll(selector);
				for (var i = 0; i < matches.length; i++) {
					var node = matches[i];
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
		websocket.onopen = function(event) {
			console.log("websocket " + websocketURL + ": onopen");
			if (onOpenCommand) {
				websocket.send(onOpenCommand);
			}
		};
		websocket.onclose = function(event) {
			console.log("websocket " + websocketURL + ": onclose");
		};
		websocket.onerror = function(event) {
			console.log("websocket " + websocketURL + ": onerror " + event);
		};
		return websocket;
	} else {
		alert("Your browser does not support Websockets :-(");
		return null;
	}
}


function sendMessage(websocket, message) {
	if (!websocket) {
		return;
	}
	if (websocket.readyState == WebSocket.OPEN) {
		//alert("Sending message: " + message);
		websocket.send(message);
	} else {
		// alert("The socket is not open.");
	}
}
