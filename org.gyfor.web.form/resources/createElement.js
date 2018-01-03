export function ce(name, props, ...children) {
  	let elem = document.createElement(name);
   	if (props) {
   		for (const key of Object.keys(props)) {
   			elem.setAttribute(key, props[key]);
   		}
   	}
   	if (children) {
   		for (const child in children) {
   			elem.appendChild(child);
   		}
   	}
   	return elem;
}
   	