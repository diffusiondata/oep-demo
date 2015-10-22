var mode = "auto";
	var light = "off";
	var heat = "off";
	var topicName = "Sensors//";
	var graph;
	var seriesData = [ [], [], [], [], [], [], [] ];

	function onDataEvent(webClientMessage) {
		var fields = webClientMessage.getFields(0);
		var topic = webClientMessage.getTopic();

		var split = topic.split("/");
		var type = split[1];
		var sensor = split[2];
		var room = split[3];
		var now = new Date().getTime() / 1000;

		if (type == "Readings") {
			if (sensor == "Temp") {
				setTemp(room, parseFloat(fields[0]));
			} else if (sensor == "Light") {
				setLight(parseFloat(fields[0]));
			} else if (sensor == "PIR") {
				setPerson(room, parseBoolean(fields[0]));
			}
		} else if (type == "Control") {
			if (sensor == "Light") {
				lights(fields[0]);
			} else {
				heater(fields[0]);
			}
		}
	}

	function setTemp(room, value) {
		var roomId;
		if (room == "Bedroom 1") {
			roomId = 0;
		} else if (room == "Bedroom 2") {
			roomId = 1;
		} else if (room == "Bedroom 3") {
			roomId = 2;
		} else if (room == "Kitchen") {
			roomId = 3;
		} else if (room == "Living Room") {
			roomId = 4;
		} else if (room == "Outside") {
			roomId = 5;
		}

		new RGraph.Thermometer('thermometer' + roomId, 10, 40, value).Draw();

		var now = new Date().getTime() / 1000;

		var len = seriesData[roomId].push({
			x : now,
			y : value
		});

		if (len > 1000) {
			seriesData[roomId].shift();
		}
		graph.update();
	}

	function setLight(value) {
		var now = new Date().getTime() / 1000;
		var lenn = seriesData[6].push({
			x : now,
			y : value
		});

		if (len > 1000) {
			seriesData[6].shift();
		}
		graph.update();
	}

	function setPerson(room, value) {
		var roomId;
		if (room == "Bedroom 1") {
			roomId = 0;
		} else if (room == "Bedroom 2") {
			roomId = 1;
		} else if (room == "Bedroom 3") {
			roomId = 2;
		} else if (room == "Kitchen") {
			roomId = 3;
		} else if (room == "Living Room") {
			roomId = 4;
		} else if (room == "Outside") {
			roomId = 5;
		}

		var onState = "visible";
		if (value) {
			onState = "visible";
		}
		var pic = document.getElementById("person" + roomId);
		pic.style.visibility = onState;
	}

	function connected(isConnected) {
		DiffusionClient.trace("Connected to Diffusion Server");
		DiffusionClient.subscribe(topicName);
	}

	function initGraph() {
		for ( var i = 0; i < seriesData.length; i++) {
			seriesData[i].push({
				x : new Date().getTime() / 1000,
				y : 15
			});
		}
		var palette = new Rickshaw.Color.Palette({
			scheme : "munin"
		});

		graph = new Rickshaw.Graph({
			element : document.getElementById("chart"),
			renderer : 'line',
			series : [ {
				data : seriesData[0],
				color : palette.color(),
				name : 'Bedroom 1'
			}, {
				data : seriesData[1],
				color : palette.color(),
				name : 'Bedroom 2'
			}, {
				data : seriesData[2],
				color : palette.color(),
				name : 'Bedroom 3'
			}, {
				data : seriesData[3],
				color : palette.color(),
				name : 'Kitchen'
			}, {
				data : seriesData[4],
				color : palette.color(),
				name : 'Living Room'
			}, {
				data : seriesData[5],
				color : palette.color(),
				name : 'Outside'
			}, {
				data : seriesData[6],
				color : palette.color(),
				name : 'Light'
			} ]
		});

		new Rickshaw.Graph.Axis.Y({
			element : document.getElementById('y_axis'),
			graph : graph,
			orientation : 'left',
			tickFormat : Rickshaw.Fixtures.Number.formatKMBT
		});

		new Rickshaw.Graph.Axis.Time({
			graph : graph
		});

		new Rickshaw.Graph.HoverDetail({
			graph : graph
		});

		var legend = new Rickshaw.Graph.Legend({
			element : document.querySelector('#legend'),
			graph : graph
		});

		graph.render();
	}

	function autoMode() {
		if (mode == "auto") {
			mode = "manual";
			document.getElementById("auto-mode-display").innerHTML = "Manual";
			document.getElementById("auto-mode-icon").src = "images/auto-off.png";
			document.getElementById("light-switch-button").style.visibility = "visible";
			document.getElementById("heater-switch-button").style.visibility = "visible";
		} else {
			mode = "auto";
			document.getElementById("auto-mode-display").innerHTML = "Automatic";
			document.getElementById("auto-mode-icon").src = "images/auto-on.png";
			document.getElementById("light-switch-button").style.visibility = "hidden";
			document.getElementById("heater-switch-button").style.visibility = "hidden";
		}
		thresholds();
	}

	function lights(state) {
		var onState = "hidden";
		var offState = "visible";
		if (state == "on") {
			light = true;
			var onState = "visible";
			var offState = "hidden";
			document.getElementById("light-switch-button").src = "images/auto-on.png";
		} else {
			light = false;
			document.getElementById("light-switch-button").src = "images/auto-off.png";
		}

		for ( var i = 0; i < 5; i++) {
			document.getElementById("light" + i + "-on").style.visibility = onState;
			document.getElementById("light" + i + "-off").style.visibility = offState;
		}
	}

	function heater(state) {
		if (state == "on") {
			heat = true;
			document.getElementById("fire").src = "images/heat-on.png";
			document.getElementById("heater-switch-button").src = "images/auto-on.png";
		} else {
			document.getElementById("fire").src = "images/heat-off.png";
			document.getElementById("heater-switch-button").src = "images/auto-off.png";
			heat = false;
		}
	}

	function thresholds() {
		var ttl = document.getElementById("ttl").value;
		var tth = document.getElementById("tth").value;
		var tte;
		var tl = document.getElementById("tl").value;
		var topicMessage = new TopicMessage("Sensors/Control");
		topicMessage.putFields(mode, ttl, tth, tte, tl);

        console.log(topicMessage);
		DiffusionClient.sendTopicMessage(topicMessage);
	}

    function lightingThreshold(val) {
        document.getElementById("lighting-threshold").innerHTML = val;
    }

    function heatThresholdHigh(val) {
        document.getElementById('heat-threshold-high').innerHTML = val;
    }

    function heatThresholdLow(val) {
        document.getElementById('heat-threshold-low').innerHTML = val;
    }

	function lightSwitch() {
		var command = "on";
		if (light) {
			command = "off";
		}
		var topicMessage = new TopicMessage("Sensors/Control/Light", command);
		DiffusionClient.sendTopicMessage(topicMessage);
	}

	function heaterSwitch() {
		var command = "on";
		if (heat) {
			command = "off";
		}
		var topicMessage = new TopicMessage("Sensors/Control/Heater", command);
		DiffusionClient.sendTopicMessage(topicMessage);
	}

	function init() {
		lights("on");
		heater("on");

		var connectionDetails = {
			onDataFunction : onDataEvent,
			onCallbackFunction : connected
		};

		DiffusionClient.connect(connectionDetails);
		/* DiffusionClient.setDebugging(true); */

		initGraph();
		lights("off");
		heater("off");
	}

	function parseBoolean(value) {
		return value == "true" ? true : false;
	}
