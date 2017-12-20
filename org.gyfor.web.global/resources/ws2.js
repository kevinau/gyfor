class WS {
	var websocket2;
	
	constructor (baseURL, arg, onOpenFunction) {
		let websocketURL = "ws://" + baseURL + "/" + arg;
		websocket2 = new WebSocket(websocketURL);
		websocket2.onmessage = function(event) {
			console.log("websocket " + websocketURL + ": onmessage");
			let n1 = event.data.indexOf(" ");
			if (n1 == -1) {
				// Bad message
				console.log("error: no function name delimiter (space)");
				return;
			}
			let fnName = event.data.substring(0, n1);
			let fn = window[fnName];
			if (typeof v !== "function") {
				// Bad message
				console.log("error: " + fnName + " does not name a gloably known function");
				return;
			}
			let argx = envent.data.substring(n1 + 1);
			var arg;
			try {
				arg = JSON.parse(argx);
			} catch (e) {
				console.log("error: " + e);
			}
			console.log("onmessage " + fnName + " " + argx);
			if (isArray(arg)) {
				fn.apply(null, arg);
			} else {
				fn(arg);
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
	}


	function sendMessage(message) {
		if (websocket2.readyState == WebSocket.OPEN) {
			console.log("Sending message: " + message);
			websocket2.send(message);
		} else {
			console.log("The socket is not open.");
		}
	}
}
