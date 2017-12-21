class WS {

	static deepValue(obj, path) {
	    path = path.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
	    //path = path.replace(/^\./, '');           // strip a leading dot
	    let px = path.split('.');
	    for (var i = 0; i < px.length; i++){
	        obj = obj[px[i]];
	    };
	    return obj;
	};
	
	constructor (url, arg, onOpenFunction) {
		let ws = this;
		let domain = location.hostname + (location.port ? ':' + location.port : '');
		let websocketURL = "ws://" + domain + url;
		if (arg) {
			websocketURL += "/" + arg;
		}
		let websocket2 = new WebSocket(websocketURL);
		websocket2.onmessage = function(event) {
			console.log("websocket " + websocketURL + ": onmessage: " + event.data);
			let msgParts = event.data.split("\t");
			if (msgParts.length == 0) {
				// Bad message
				console.log("error: empty message");
				return;
			}
			let v = WS.deepValue(window, msgParts[0]);
			if (typeof v !== "function") {
				// Bad message
				console.log("error: " + msgParts[0] + " does not name a gloably known function");
				return;
			}
//			let argx = envent.data.substring(n1 + 1);
//			var arg;
//			try {
//				arg = JSON.parse(argx);
//			} catch (e) {
//				console.log("error: " + e);
//			}
//			console.log("onmessage " + fnName + " " + argx);
//			if (isArray(arg)) {
//				fn.apply(null, arg);
//			} else {
//				fn(arg);
//			}
		};
		websocket2.onopen = function(event) {
			console.log("websocket " + websocketURL + ": onopen");
			ws.sendMessage("hello");
//			if (onOpenFunction) {
//				onOpenFunction(ws);
//			}
		};
		websocket2.onclose = function(event) {
			console.log("websocket " + websocketURL + ": onclose");
		};
		websocket2.onerror = function(event) {
			console.log("websocket " + websocketURL + ": onerror " + event);
		};
		this._websocketURL = websocketURL;
		this._websocket2 = websocket2;
	}


	sendMessage(message) {
		if (this._websocket2.readyState == WebSocket.OPEN) {
			console.log("Sending message: " + message);
			this._websocket2.send(message);
		} else {
			console.log("websocket " + this._websocketURL + " is not open.");
		}
	}
	
	
	sendInput(id, value) {
		if (this._websocket2.readyState == WebSocket.OPEN) {
			let message = "input\t" + id + "\t" + value;
			console.log("Sending message: " + message);
			this._websocket2.send(message);
		} else {
			console.log("websocket " + this._websocketURL + " is not open.");
		}
	}

}
