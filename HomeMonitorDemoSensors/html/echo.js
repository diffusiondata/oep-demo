var topicName = "EchoTest";

/**
 * Called when a message is delivered from Diffusion
 * 
 * @param webClientMessage
 *            Message from Diffusion
 */
function onDataEvent(webClientMessage) {	
	var messagesBox = document.getElementById('Messages');

	/* Get the 1st record (there should be at least 3 fields within) */
	var fields = webClientMessage.getFields( 0 );
	var content = fields[0], clientID = fields[1];
	if( clientID == DiffusionClient.getClientID() ) {
		clientID = "This client";
	}

	/* If your browser has a JS console, look for this on it ... */
	DiffusionClient.trace("Received a message with payload ... ->" + content + "<--");

	/* Hide the intro text .. if it's there */
	var intro = document.getElementById( "MessagesBox_intro" );
	intro.style.cssText = "display: none";
	
	/* Compose the HTML elements required: two divs and an <hr/> */
	var noticeElement = document.createElement( 'div' );
	noticeElement.innerHTML = content;
	
	var clientElement = document.createElement( 'div' );
	clientElement.setAttribute( 'class', 'ConnectionDetails floatRight' );
	clientElement.innerHTML = clientID;
	
	messagesBox.appendChild( clientElement );
	messagesBox.appendChild( noticeElement );
	messagesBox.appendChild( document.createElement( 'hr' ) );

	messagesBox.scrollTop = messagesBox.scrollHeight;
}

/**
 * Called to send a message back to Diffusion
 */
function sendMessage() {
	var messageElement = document.getElementById('MessageToSend');
	
	var topicMessage = new TopicMessage( topicName, messageElement.value);	
	DiffusionClient.sendTopicMessage(topicMessage);

	/* Reset the input box */
	messageElement.value= "";
}

/**
 * Called when the page is ready: places a connection to Diffusion, put the
 * keyboard focus into the input widget
 */
function onLoad() {
	connected( false );
	
	/* Assemble some sensible connection details ... */
	var connectionDetails = {
		topic : topicName,
		onDataFunction : onDataEvent,
		onCallbackFunction: function( isConnected ) {
			connected( isConnected );
		},
		onLostConnectionFunction: function () {
			connected( false );
		}
	};
		
	/* And place the connection */
	DiffusionClient.connect(connectionDetails);
	/* Turn the logging on  */
	DiffusionClient.setDebugging( true );
}

/**
 * Called whenever a key is pressed in any given bound input control. If the key
 * is the ENTER key, then send the message to Diffusion.
 */
function submitEnter( obj, e ) {
	var keycode;
	if( window.event ) {
		keycode = window.event.keyCode;
	} else {
		if( e ) {
			keycode = e.which;
		} else { 
			return true;
		}
	}
	
	if( keycode == 13 ) {
		sendMessage();
		return false; /* Tell the DOM not to propagate this event */
	} else {
	   return true; /* Tell the DOM to propagate the event */
	}
}

/**
 * Called whenever the state of the Diffusion connection changes.
 * 
 * @param isConnected
 */
function connected( isConnected ) {
	var messageElement = document.getElementById( 'MessageToSend' ),
		clientIdElement = document.getElementById( 'ClientID' );
	
	messageElement.disabled = !isConnected;
	if( isConnected ) {
		clientIdElement.innerHTML = "Connected as " + DiffusionClient.getClientID();
		messageElement.disabled = false;
		messageElement.placeholder = "Enter your message to post";
		messageElement.focus();
	} else {
		clientIdElement.innerHTML = "No connection to Diffusion";
		messageElement.value = "";
		messageElement.placeholder = "Connecting to Diffusion";
		messageElement.disabled = true;
	}
}

/**
 * Compose an email-invitation holding the externally available IP address of
 * this host.
 */
function sendInvite() {
	var addresses = <!--@DiffusionTag type="publisher" publisher="MyEchoPublisher" tagid="publicURL" -->;

	var locations = [];
	for( var i=0; i< addresses.length; i++ ) {
		var url = window.location.protocol + "//" + addresses[i] + ":" + window.location.port + window.location.pathname;
		locations.push( url );
	}

	var prefix = "Please find my Diffusion example " + ( locations.length > 1 ? "at one of the following: " : "at: " );
	var body = encodeURI( prefix + locations.join( ", ")), 
		subject = encodeURI( "Visit my Diffusion example" );
	document.location = "mailto:allMyFriends@acme.com?subject="+subject+"&body=" + body;
}