function onInputChange(elem) {
	console.log("........ " + elem);
}

class TextInput extends React.Component {
  render() {
	return React.createElement('input', {type: 'text', length: 20, value: 'abcd', onInput: onInputChange(this)}, null);
  }
}


class EntityNode extends React.Component {
	constructor(props) {
		super(props);
		this.state = {childNodes: []};
	}

  	render() {
  		return React.createElement('form', null, childNodes);
  	}

}


class NameForm extends React.Component {
	constructor(props) {
		super(props);
		this.state = {value: ''};

		this.handleChange = this.handleChange.bind(this);
	}

  	handleChange(event) {
	  	console.log("Handle change event: " + event.target.value);
	  	this.setState({value: event.target.value});
  	}

  	render() {
  		return React.createElement('form', null, 
  					React.createElement('input', {type: 'text', 
  						                          value: this.state.value,
  											      onChange: this.handleChange}, null)
  		);
  	}
}
