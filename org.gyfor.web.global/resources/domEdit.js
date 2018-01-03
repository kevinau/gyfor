var domEditCallback = {
	addChildren: function(selector, htmlSource) {
				// Add the html to the specified container (identified by containerSelector).
				// The html can be multiple elements.  A wschange event is fired after all
				// children have been added.
				console.log ("addChildren: " + selector + ": " + htmlSource);
				container = document.querySelector(selector);
				console.log ("container: " + container);
  				if (!container) {
  					console.log("Cannot find DOM element matching selector: " + selector);
  					return;
  				}
				dx = document.createElement('div');
  				dx.innerHTML = htmlSource;
  				children = dx.childNodes;
    			while (children.length > 0) {
    				container.appendChild(children[0]);
    			}
			    event1 = new Event('wschange');
       	        container.dispatchEvent(event1);
	},

	replaceNode: function(selector, htmlSource) {
				// Remove the existing element (identified by node selector), then add the 
				// html in its place.  The html can be multiple elements.  This action does
				// NOT fire a wschange event.
				console.log ("replaceNode: " + selector + ": " + htmlSource);
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
	},

	replaceChildren: function(selector, htmlSource) {
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
	},
	
	deleteChildren: function(selector) {
				// Remove the child nodes of the node that matches the container selector.
				// A wschange event is fired after child nodes have been removed.
				container = document.querySelector(selector);
			    let last;
			    while (last = container.lastChild) container.removeChild(last);
			    event1 = new Event('wschange');
       	        container.dispatchEvent(event1);
	},
	
	deleteNodes: function(selector) {
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
};
