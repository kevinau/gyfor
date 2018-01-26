	// Locally called method
	function deepValue (obj, path) {
		if (typeof obj === "function" && obj.name === path) {
			return obj;
		}
	    path = path.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
	    //path = path.replace(/^\./, '');           // strip a leading dot
	    let px = path.split('.');
	    for (var i = 0; i < px.length; i++){
	        obj = obj[px[i]];
	    };
	    return obj;
	}
	
	function openWebSocket (url, callback, onOpenFunction) {
		let domain = location.hostname + (location.port ? ':' + location.port : '');
		let websocketURL = "ws://" + domain + url;
		let websocket2 = new WebSocket(websocketURL);
		let ws = {
				websocket: websocket2,
				url: websocketURL,
				sendMessage: function(message) {
					if (websocket2.readyState == WebSocket.OPEN) {
						console.log("websocket " + url + ": send: " + message);
						websocket2.send(message);
					} else {
						console.log("websocket " + url + " is not open.");
					}

				},
		 		sendInput: function(id, value) {
		 			if (websocket2.readyState == WebSocket.OPEN) {
		 				let message = "input\t" + id + "\t" + value;
		 				console.log("websocket " + ws.url + ": send: " + message);
		 				websocket2.send(message);
		 			} else {
		 				console.log("websocket " + ws.url + " is not open.");
		 			}
		 		},
		 		sendClick: function(id, value) {
					if (websocket2.readyState == WebSocket.OPEN) {
						let message = "click\t" + id + "\t" + value;
						console.log("websocket " + ws.url + ": sendClick: " + message);
						websocket2.send(message);
					} else {
						console.log("websocket " + ws.url + " is not open.");
					}
				}
		}

		websocket2.onmessage = function(event) {
			console.log("websocket " + websocketURL + ": onmessage: " + event.data);
			let msgParts = event.data.split("\t");
			if (msgParts.length == 0) {
				// Bad message
				console.log("error: empty message");
				return;
			}
			
			let v = deepValue(callback, msgParts[0]);
			if (typeof v !== "function") {
				// Bad message
				console.log("error: " + msgParts[0] + " does not name a gloably known function");
				return;
			}
			let args = [];
			for (let i = 1; i < msgParts.length; i++) {
				let msgPart = msgParts[i];
//				let arg1;
//				try {
//					arg1 = JSON.parse(msgPart);
//				} catch (e) {
//					console.log("error: " + e);
//				}
				args.push(msgPart);
			}
			v(...args);
		};
		websocket2.onopen = function(event) {
			console.log("websocket " + websocketURL + ": onopen: " + onOpenFunction);
			//sendMessage(ws, "hello");
			if (onOpenFunction) {
				onOpenFunction(ws);
			}
		};
		websocket2.onclose = function(event) {
			console.log("websocket " + websocketURL + ": onclose");
		};
		websocket2.onerror = function(event) {
			console.log("websocket " + websocketURL + ": onerror " + event);
		};
		return ws;
	}


//	function sendMessage(ws, message) {
//		let websocket2 = ws.websocket;
//		if (websocket2.readyState == WebSocket.OPEN) {
//			console.log("websocket " + ws.url + ": send: " + message);
//			websocket2.send(message);
//		} else {
//			console.log("websocket " + ws.url + " is not open.");
//		}
//	}
	
	
	function sendInput(ws, id, value) {
		let websocket2 = ws.websocket;
		if (websocket2.readyState == WebSocket.OPEN) {
			let message = "input\t" + id + "\t" + value;
			console.log("websocket " + ws.url + ": sendInput: " + message);
			websocket2.send(message);
		} else {
			console.log("websocket " + ws.url + " is not open.");
		}
	}

	
	function sendClick(ws, id, value) {
		let websocket2 = ws.websocket;
		if (websocket2.readyState == WebSocket.OPEN) {
			let message = "click\t" + id + "\t" + value;
			console.log("websocket " + ws.url + ": sendClick: " + message);
			websocket2.send(message);
		} else {
			console.log("websocket " + ws.url + " is not open.");
		}
	}

export { openWebSocket, sendInput, sendClick };
