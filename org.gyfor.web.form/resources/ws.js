var websocket;

function websocketStart(baseURL, entityClassName) {
	if (window.WebSocket) {
		websocket = new WebSocket("ws:" + baseURL + "/" + entityClassName);
		websocket.onmessage = function(event) {
			//alert("On message: " + event.data);
			var n1 = event.data.indexOf("|");
			if (n1 == -1) {
				// Bad message
				return;
			}
			var containerId = event.data.substring(0, n1);
			var n2 = event.data.indexOf("|", n1 + 1);
			if (n2 == -1) {
				// Bad message
				return;
			}
			var action = event.data.substring(n1 + 1, n2);
			var n3 = event.data.indexOf("|", n2 + 1);
			if (n3 == -1) {
				// Bad message
				return;
			}
			var nodeId = event.data.substring(n2 + 1, n3);
			var htmlSource = event.data.substring(n3 + 1);
			
			switch (action) {
			case 'A': // Adding a new element to a container
				console.log ("adding node " + nodeId + ": " + htmlSource);
				var node = document.getElementById("node-" + nodeId);
				if (node && node.parentNode) {
					console.log ("found node-" + nodeId + " for replacement");
  				    var dx = document.createElement('div');
  				    dx.innerHTML = htmlSource;
  				    var html = dx.firstChild;
					node.replaceWith(html);
				} else {
					console.log ("adding node to end of socket-" + containerId);
  				    var container = document.getElementById("socket-" + containerId);
  				    var dx = document.createElement('div');
  				    dx.innerHTML = htmlSource;
  				    
  				    var children = dx.childNodes;
    				for (var i = 0; i < children.length; i++) {
    					container.append(children[i]);
    				}
  				    // Force a redraw...
  				    ////var redraw = html.offsetHeight;
  				    // ... so that transitions triggered from the following are
  				    // acted on.
  				    ////html.classList.remove("slideclosed");
  				    ////html.classList.add("slideopen");
				}
				break;
			case 'R': // Removing a node from a container
				//alert ("removong node " + nodeId);
				var node = document.getElementById("node-" + nodeId);
				if (node && node.parentNode) {
					////node.classList.remove("slideopen");
					////node.classList.add("slideclosed");
					////node.addEventListener("transitionend", function() {
						node.parentNode.removeChild(node);
					////}, true);
				}
				break;
			}
		};
		websocket.onopen = function(event) {
			send("new" + "|" + entityClassName);
			// alert("Web Socket opened!");
		};
		websocket.onclose = function(event) {
			// alert("Web Socket closed.");
		};
	} else {
		alert("Your browser does not support Websockets :-(");
	}
}

function send(message) {
	if (!window.WebSocket) {
		return;
	}
	if (websocket.readyState == WebSocket.OPEN) {
		//alert("Sending message: " + message);
		websocket.send(message);
	} else {
		// alert("The socket is not open.");
	}
}
