var canvas = null;
var id = 0;
var serviceCounter = 0;
var connectionCounter = 0;
//The first selected service for connection
var tempService = null;
var name = "";
var state = 'move';
var serviceObjs = {};
var connectionObjs = {};
var buttonObjs = [];
var publicVisible = false;
var sharedUsers = [];
// Get the modal
var saveModal = document.getElementById('saveModal')
var alternativeModal = document.getElementById('alternativeModal');

// Get the <span> element that closes the modal
var span1 = document.getElementsByClassName("close")[0];
var span2 = document.getElementsByClassName("close")[1];

var service1IdForAlternative;
var service2IdForAlternative;


var DEBUG = false;
var SERVICE_RADIUS = 50;
var ARROW_WIDTH = 20;
var ARROW_HEIGHT = 30;

var TOOLBAR_WIDTH = 50;

var data = {
    services:{},
    connections:{}
};

function loadCombination() {
    // load the current combination
    if ($('#comb').text() != "") {
        var comb = JSON.parse($('#comb').text());
        $.each(comb.productsInComb, function (id, service) {
            createService(service.xPosition, service.yPosition, service.product.name, service.product.id, service.id, "../images/test.png");
        });
        $.each(comb.connections, function (id, connection) {
            var sFrom = null;
            var sTo = null;
            $.each(data.services, function (id, service) {
                if (service.internalId === connection.sourceProduct) {
                    sFrom = "" + service.id;
                }
                if (service.internalId   === connection.targetProduct) {
                    sTo = "" + service.id;
                }
            });
            createConnection(sFrom, sTo, connection.compatibility);
        });
        name = comb.name;

        // fill save dialog
        $('#name').val(comb.name);
        $('#sharedUsers').val($.map(comb.reader, function(value) {return value.email}).join(', '));
        $('#public').prop('checked', comb.publicVisible);
    }
}

function __getConnections (serviceId, direction){
    var result = [];
    $.each(data.connections, function (connectionId, connection){

        var isOut = connection.sFrom == serviceId;
        var isIn = connection.sTo == serviceId;

        if (direction === 'OUT' && isOut || direction === 'IN' && isIn || direction === null && (isOut || isIn)) {
            result.push(connection)
        }
    });
    return result;
}

function getConnections (serviceId) {
    return __getConnections(serviceId, null);
}

function getConnectionsBetween (serviceId1, serviceId2) {
    var result = [];
    $.each(data.connections, function(connectionId, connection){
        if((connection.sFrom === serviceId1 && connection.sTo === serviceId2)
            || (connection.sTo === serviceId1 && connection.sFrom === serviceId2)){
            result.push(connection)
        }
    });
    return result;
}

function getService (serviceId) {
    return data.services[serviceId];
}


// ondragstart event handler
function drag (event) {
    event.dataTransfer.setData("data", JSON.stringify({
        id: event.target.getAttribute("service-id"),
        name: event.target.getAttribute("service-name"),
        imageUrl: event.target.getAttribute("service-image")
    }));
}

// ondragover event handler
function onDragOver(ev) {
    ev.preventDefault();
    return false;
}

// ondrop event handler
function drop(ev) {
    ev.preventDefault();
    var data = JSON.parse(ev.dataTransfer.getData("data"));

    var x = ev.pageX - $('#canvas').offset().left;
    var y = ev.pageY - $('#canvas').offset().top;

    var s = createService(x, y, data.name, data.id, null, data.imageUrl);
    drawService(s);

    setState('move');
    checkCanvasBounds(s);
    canvas.renderAll();
}

// add a new service to data
function createService(x, y, productName, productId, internalId, imageUrl) {
    var newId = serviceCounter++;
    data.services[newId] = {
        product:{
            name: productName,
            id: productId
        },
        id: newId,
        x: x,
        y: y,
        internalId: internalId,
        imageUrl: imageUrl
    };
    return newId;
}

// draw a service to the canvas
function drawService (serviceId) {
    var service = data.services[serviceId];

    var s = new fabric.Circle({
        left: service.x,
        top: service.y,
        strokeWidth: 3,
        radius: SERVICE_RADIUS,
        fill: 'white',
        stroke: 'black',
        id: serviceId,
        dataType: 'SERVICE',
        hasControls: false,
        hasBorders: true,
        image: null,
        label: null
    });

    if(service.imageUrl) {
        fabric.Image.fromURL(service.imageUrl, function (oImg) {
            oImg.set({
                left: service.x,
                top: service.y - SERVICE_RADIUS / 3,
                scaleX: SERVICE_RADIUS / oImg.height,
                scaleY: SERVICE_RADIUS / oImg.width,
                selectable: false,
                evented: false
            });

            canvas.moveTo(oImg, canvas.getObjects().indexOf(s));
            s.image = oImg;

            canvas.add(s.image);
        });
    }

    s.label = new fabric.Text(service.product.name || '', {
        left: s.left,
        top: s.top + SERVICE_RADIUS / 2,
        fontFamily: 'Comic Sans',
        fontSize: 18,
        selectable: false,
        evented: false
    });

    canvas.add(s);
    canvas.add(s.label);
    serviceObjs[serviceId] = s;

    onStateChange();

    return s;
}

// get the normal vector between to points
function getNormal(p0, p1) {
    var diff  = p1.subtract(p0);
    var normal = diff.divide(p0.distanceFrom(p1));

    return new fabric.Point(normal.y, - normal.x);
}

// update all connections on the canvas
function updateConnectionList(connections) {
    $.each(connections,function(index, connection){
        updateConnection(connection.id)
    });
}

// update a service on the canvas
function updateService (serviceId) {
    var service = data.services[serviceId];
    var serviceObj = serviceObjs[serviceId];

    serviceObj.set({
        left: service.x,
        top: service.y
    });

    serviceObj.label.set({
        left: service.x,
        top: service.y + SERVICE_RADIUS / 2
    });

    if(serviceObj.image) {
        serviceObj.image.set({
            left: service.x,
            top: service.y - SERVICE_RADIUS / 3
        });
    }
}

// update a connection on the canvas
function updateConnection(connectionId) {
    var connection = data.connections[connectionId];
    var serviceFrom = getService(connection.sFrom);
    var serviceTo = getService(connection.sTo);

    var p1 = new fabric.Point(serviceFrom.x, serviceFrom.y);
    var p2 = new fabric.Point(serviceTo.x, serviceTo.y);

    var startGap = 10 + SERVICE_RADIUS;
    var endGap = (startGap + ARROW_HEIGHT / 2);

    var length = p1.distanceFrom(p2);

    var curve = 0.0;

    if (connectionExists(connection.sTo, connection.sFrom)) {
        var maxCurveHeight = SERVICE_RADIUS * 2;
        curve = length / 3;
        curve = curve > maxCurveHeight ? maxCurveHeight : curve;
    }

    var curveControl = p1.midPointFrom(p2).add(getNormal(p1, p2).multiply(curve));

    var curveStart = p1.lerp(p2, startGap / length);
    var curveEnd =  p2.lerp(p1, endGap / length);

    if (curve > 0){
        var offsetAngle = fabric.util.degreesToRadians(25);
        curveStart = fabric.util.rotatePoint(curveStart, p1, -offsetAngle);
        curveEnd = fabric.util.rotatePoint(curveEnd, p2, offsetAngle);
    }

    var curveCenter = getPointOnCurve(curveStart, curveEnd, curveControl, 0.5);

    var line = connectionObjs[connectionId];

    setCurvePoints(line, curveStart, curveEnd, curveControl);

    var angle = fabric.util.radiansToDegrees(Math.atan2(curveControl.y - p2.y, curveControl.x - p2.x)) % 360;

    line.arrow.set({
        'left': curveEnd.x,
        'top': curveEnd.y,
        'angle': angle - 90
    });

    line.compIcon.set({
        'left': curveCenter.x,
        'top': curveCenter.y
    });
    line.setCoords();
    line.arrow.setCoords();
    line.compIcon.setCoords();
}

// get a point on a quadratic curve (defined by start, end, and control)
// t should be a value between 0 and 1
function getPointOnCurve (p0, p1, control, t) {
    function calc(c) {
        return Math.pow(1 - t, 2) * p0[c] + 2 * (1 - t) * t * control[c] + Math.pow(t, 2) * p1[c]
    }

    return new fabric.Point(calc('x'), calc('y'));
}

// create a curve object, that will be added to the canvas later
function getCurve () {
    // the path definition must not be empty. this is just a placeholder
    var p = new fabric.Path('M 0 0 Q 200 200 300 500');
    p.set({
        fill: '',
        originX: 'left',
        originY: 'top',
        stroke: 'black',
        strokeWidth: 5,
        selectable: false,
        evented: false,
        hoverCursor: 'default',
        objectCaching: false, // for editing the path points directly
        dataType: 'CONNECTION',
        noScaleCache: true
    });
    return p;
}

// set the points an a quadratic curve
function setCurvePoints (curve, start, end, control) {
    curve.path[0][1] = start.x;
    curve.path[0][2] = start.y;

    curve.path[1][1] = control.x;
    curve.path[1][2] = control.y;
    curve.path[1][3] = end.x;
    curve.path[1][4] = end.y;

    curve.set({
        pathOffset: {
            x: curve.getScaledWidth() / 2,
            y: curve.getScaledHeight() / 2
        }
    });
}

// retrieve the compability degree of two services from the server
function checkCompatibility(s1, s2){
       var response = $.ajax({
            type: "GET",
            url: "products/compatibility/" + s1 + "/" + s2,
            async: false
        });
    return response.responseJSON;
}

// add a new connection to data
function createConnection (service1, service2, compatibility) {

    if (connectionExists(service1, service2)) {
        return null;
    }
    var newId = connectionCounter++;

    data.connections[newId] = {
        id: newId,
        sFrom: service1,
        sTo: service2,
        compatibility: compatibility
    };

    return newId;
}

// draw a connection to the canvas
function drawConnection(connectionId) {

    var connectionData = data.connections[connectionId];
    var c = getCurve();

    connectionObjs[connectionId] = c;

    c.arrow = new fabric.Triangle({
        width: ARROW_WIDTH,
        height: ARROW_HEIGHT,
        fill: 'black',
        selectable: false,
        evented: false,
        hoverCursor: 'default'
    });

    var color = '#e52020';
    var icon = '\uf00d'; // font-awesome: times
    var cursor = 'default';
    if (connectionData.compatibility === 'COMPATIBLE') {
        color = '#0bc300';
        icon = '\uf00c'; // font-awesome: check
    }
    else if (connectionData.compatibility === 'COMPATIBLE_WITH_ALTERNATIVE') {
        color = '#e5d500';
        icon = '\uf126'; // font-awesome: code-branch
        cursor = 'pointer';
    }

    c.compIcon = new fabric.Text(icon, {
        dataType: 'COMPATIBILITY',
        fill: color,
        fontFamily: 'Font Awesome 5 Free',
        fontWeight: 900,
        fontSize: 36,
        selectable: false,
        hoverCursor: cursor,
        connection: connectionId
    });

    canvas.add(c);
    canvas.add(c.arrow);
    canvas.add(c.compIcon);

    canvas.sendToBack(c);
    canvas.sendToBack(c.arrow);
    canvas.bringToFront(c.compIcon);

    return c;
}

// get a connection from a service to another
function getConnectionFromTo(serviceId1, serviceId2) {
    var result = null;
    $.each(data.connections, function(connectionId, connection){
        if((connection.sFrom === serviceId1 && connection.sTo === serviceId2)){
            result = connection;
            // exit this loop
            return false;
        }
    });
    return result;
}

// check if a connection between two services exists
function connectionExists(serviceId1, serviceId2){
    return getConnectionFromTo(serviceId1, serviceId2) !== null;
}

// check if an object is within the canvas and return its new coordinates
function checkCanvasBounds (obj) {
    var result = {
        left: obj.left,
        top: obj.top
    };

    var w = obj.width / 2;
    var h = obj.height / 2;

    if (obj.left < w + TOOLBAR_WIDTH) {
        result.left = w + TOOLBAR_WIDTH;
    }
    else if (obj.left > canvas.width - w){
        result.left = canvas.width - w;
    }

    if (obj.top < h) {
        result.top = h;
    }
    else if (obj.top > canvas.height - h){
        result.top = canvas.height - h;
    }

    return result;
}

// entry point:
// load Fonts and setup the canvas
$( document ).ready(function(){
    var fontobserver = new FontFaceObserver('Font Awesome 5 Free', {
        weight: 900
    });
    fontobserver.load().then(function(){
        setupCanvas();
    });
});

// update button styles and lock/unlock movement according to the current state
function onStateChange(){
    for(var i = 0; i < buttonObjs.length; i++){
        var b = buttonObjs[i];
        var color = 'black';
        if(b.stateName === state) {
            color = 'red';
        }
        b.set({fill: color});
    }

    var notMoving = (state !== 'move');
    var serviceCursor = notMoving ? 'pointer': 'grab';
    var connectionCursor = (state === 'delete') ? 'pointer' : 'default';

    $.each(serviceObjs, function (serviceId, canvasService){
        canvasService.set({
            lockMovementX: notMoving,
            lockMovementY: notMoving,
            hoverCursor: serviceCursor
        })
    });

    $.each(connectionObjs, function (connectionId, canvasConnection){
        var connection = data.connections[connectionId];
        if (connection.compatibility === 'POSSIBLE') {
            connectionCursor = 'pointer';
        }
        canvasConnection.compIcon.set({
            hoverCursor: connectionCursor
        })
    });
}

// set the state
function setState(newState) {
    state = newState;
    onStateChange();
}

// move button event handler
function onMove () {
    setState('move');
}

// connect button event handler
function onConnect () {
    setState('connect');
}

// delete button event handler
function onDelete () {
    setState('delete');
}

// save button event handler
function onSave () {
    saveModal.style.display = "block";
}

// send to the server
function save(){
    // hide dialog
    saveModal.style.display = "none";

    // initialize
    var saveData = {
        productsInComb:{},
        connections: [],
        publicVisible: publicVisible,
        sharedUsers: sharedUsers,
        name: name
    };

    // read services from data
    $.each(data.services, function (id, service){
        saveData.productsInComb[id]={
            id: service.id,
            xPosition: service.x,
            yPosition: service.y,
            product: service.product
        }
    });

    // read connections from data
    $.each(data.connections, function(connectionId, connection){
        saveData.connections[connectionId] = {
            sourceProduct: connection.sFrom,
            targetProduct: connection.sTo,
            compatibility: connection.compatibility
        };
    });

    // read name
    name = $('#name').val();
    if(name === ""){
        alert("Der Kombinationsname darf nicht leer sein.")
        onSave();
    }else {
        saveData.name = name;

        // read sharing options
        if ($('#public').prop('checked')) {
            publicVisible = true;
            sharedUsers = "";
        }
        else if ($('#private').prop('checked')) {
            publicVisible = false;
            sharedUsers = "";
        }
        else if ($('#users').prop('checked')) {
            publicVisible = false;
            sharedUsers = $('#sharedUsers').val();
        }else{
            alert("Eine Freigabemöglichkeit muss ausgewählt sein.")
            onSave();
            return false;
        }
        var sharedUsersList = sharedUsers.split(", ");
        saveData.sharedUsers = sharedUsersList;
        saveData.publicVisible = publicVisible;

        // send post request
        $.ajax({
            type: "POST",
            url:"combinations",
            // The key needs to match your method's input parameter (case-sensitive).
            data: JSON.stringify(saveData),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function(result){
            console.log(result);
            //$(location).attr('href', '/combinations/' + value);
        }).fail(function(result){
            console.log(result);
        });
    }
}

// setup canvas
function setupCanvas() {
    loadCombination();
    canvas = this.__canvas = new fabric.Canvas('canvas', {
        selection: false,
        preserveObjectStacking: true
    });
    fabric.Object.prototype.originX = fabric.Object.prototype.originY = 'center';

    $.each(data.services, function(serviceId, service) {
        drawService(serviceId);
    });

    $.each(data.connections, function(connectionId, connection) {
        drawConnection(connectionId);
        updateConnection(connectionId);
    });

    // add toolbar
    var toolBarBackground = new fabric.Rect({
        left: 0,
        top: 0,
        originX: 'left',
        originY: 'top',
        fill: '#dedede',
        stroke: '#000000',
        width: TOOLBAR_WIDTH,
        height: canvas.height,
        rx: 0,
        ry: 0,
        selectable: false,
        evented: false
    });

    canvas.add(toolBarBackground);

    // buttons
    // [FontAwesome code, options, stateName, handler]
    // an empty array creates a horizontal line
    var buttons = [
        ['\uf0b2', {},  'move', onMove],
        ['\uf30c', {angle: 45}, 'connect', onConnect],
        ['\uf2ed', {}, 'delete', onDelete],
    ];

    var loggedInButtons = [
        [],
        ['\uf0c7', {}, 'save', onSave]
    ];

    if ($('#isAuthenticated').val()) {
        buttons = buttons.concat(loggedInButtons);
    }

    var margin = 10;
    var size = TOOLBAR_WIDTH - margin;

    var offset = margin + size / 2;
    for(var i = 0; i < buttons.length; i++){
        if(buttons[i].length === 0){
            var line = new fabric.Line([margin / 2, offset - size / 2, TOOLBAR_WIDTH - (margin / 2), offset - size / 2],{
                strokeWidth: 1,
                stroke: '#555555',
                selectable: false,
                evented: false
            });
            canvas.add(line);
            offset += margin;
            continue;
        }
        var btn = new fabric.Text(buttons[i][0], {
            left: 25,
            top: offset,
            fontSize: size,
            fontFamily: 'Font Awesome 5 Free',
            fontWeight: 900,
            hasControls: false,
            hasBorders: false,
            lockMovementX: true,
            lockMovementY: true,
            hoverCursor: 'pointer'
        });

        btn.set(buttons[i][1]);

        btn.stateName = buttons[i][2];
        btn.onClick = buttons[i][3];

        buttonObjs.push(btn);

        btn.on('mousedown', function(event){
            event.target.set({scaleX: 1.0, scaleY: 1.0});
            event.target.animate({scaleX: 0.9, scaleY: 0.9}, {
                duration: 100,
                onChange: canvas.renderAll.bind(canvas),
                onComplete: function() {
                    event.target.animate({scaleX: 1.0, scaleY: 1.0}, {
                        duration: 100,
                        onChange: canvas.renderAll.bind(canvas),
                    });
                }
            });
            event.target.onClick();
        });
        canvas.add(btn);

        offset += margin + size;
    }
    onStateChange();

    canvas.renderAll();

    canvas.on('object:moving', function(e) {
        var serviceObj = e.target;
        var serviceId = e.target.id;

        var newPos = checkCanvasBounds(serviceObj);

        data.services[serviceObj.id].x = newPos.left;
        data.services[serviceObj.id].y = newPos.top;

        updateService(serviceId);

        $.each(getConnections(serviceObj.id), function (index, connectionData) {
            updateConnection(connectionData.id);
        });

        canvas.renderAll();
    });

    // mouse click event
    canvas.on('mouse:down', function(e) {
        var target = e.target;
        if (!target || (target && target.dataType === undefined)) {
            if(tempService != null) {
                tempService.set({
                    strokeWidth: 3,
                    stroke: 'black'
                });
                tempService = null;
            }
            canvas.renderAll();
            return;
        }

        if (target.dataType === 'SERVICE') {
            if (state === 'connect') {
                if (tempService == null) {
                    tempService = target;
                    tempService.set({
                        strokeWidth: 5,
                        stroke: 'green'
                    })
                } else if(tempService.id === target.id){
                    tempService.set({
                        strokeWidth: 3,
                        stroke: 'black'
                    });
                    tempService = null;
                }else {
                    var newConnectionId = createConnection(tempService.id, target.id, checkCompatibility(getService(tempService.id).product.id, getService(target.id).product.id));
                    if (newConnectionId !== null) {
                        drawConnection(newConnectionId);
                        updateConnectionList(getConnectionsBetween(tempService.id, target.id));
                    }
                    tempService.set({
                        strokeWidth: 3,
                        stroke: 'black'
                    });
                    tempService = null;
                }
            }
            else if (state === 'delete') {
                deleteService(target);
            }
        }
        else if (target.dataType === 'COMPATIBILITY') {
            var connection = data.connections[target.connection];
            if (state === 'delete') {
                deleteConnection(connection);
                updateConnectionList(getConnectionsBetween(connection.sFrom, connection.sTo));
            }
            else if (connection.compatibility === 'COMPATIBLE_WITH_ALTERNATIVE') {
                addAlternative(connection.sFrom, connection.sTo);
            }
        }
        canvas.renderAll();
    });

    // show bounding boxes
    if (DEBUG) {
        canvas.on('after:render', function () {
            canvas.contextContainer.strokeStyle = '#555';

            canvas.forEachObject(function (obj) {
                var bound = obj.getBoundingRect();

                canvas.contextContainer.strokeRect(
                    bound.left + 0.5,
                    bound.top + 0.5,
                    bound.width,
                    bound.height
                );
            })
        });
    }
}

// retrieve alternatives from the server
function addAlternative(serviceId1, serviceId2){
    var service1 = data.services[serviceId1];
    var service2 = data.services[serviceId2];
    service1IdForAlternative = serviceId1;
    service2IdForAlternative = serviceId2;

    var response = $.ajax({
        type: "GET",
        url: "products/alternatives/" + service1.product.id + "/" + service2.product.id,
        async: false
    });

    // create some HTML
    var altServices = JSON.parse(response.responseText);
    var alternativeHtml = '<form>';
  $.each(altServices, function(index, alternative){
      alternativeHtml = alternativeHtml + '<input type="radio" id="alternative'
      alternativeHtml = alternativeHtml + index
      alternativeHtml = alternativeHtml + '" name="alternatives"'
          if(alternative.products.length==1) {
              alternativeHtml = alternativeHtml + ' prod1id="'+ alternative.products[0].id
                  +'" prod1name="'+ alternative.products[0].name + '">' + alternative.products[0].name
          }else {
              alternativeHtml = alternativeHtml + ' prod1id="' + alternative.products[0].id + '" prod2id="'
                  + alternative.products[1].id +'" prod1name="' + alternative.products[0].name + '" prod2name="'
                  + alternative.products[1].name + '">'
                  + alternative.products[0].name  + ' und ' + alternative.products[1].name
          }

      alternativeHtml = alternativeHtml +'</input>';
  })
    alternativeHtml = alternativeHtml +'</form>';
    document.getElementById("alternativeButtons").innerHTML=alternativeHtml;
    alternativeModal.style.display = "block";
}

// add an alternative
function selectAlternative(){
    alternativeModal.style.display = "none";
    var altIds = [];
    var altNames = [];
    var alternatives = document.getElementsByName("alternatives");
    $.each(alternatives, function(index, alternative){
        if(alternative.checked){
            console.log(alternative)
            altIds.push(alternative.attributes.prod1id.value);
            altNames.push(alternative.attributes.prod1name.value)
            if(alternative.attributes.prod2id){
                altIds.push(alternative.attributes.prod2id.value);
                altNames.push(alternative.attributes.prod2name.value);

            }
        }
    });
    var service1 = data.services[service1IdForAlternative];
    var service2 = data.services[service2IdForAlternative];
    if(altIds.length === 1) {
        var p1 = new fabric.Point(service1.x, service1.y);
        var p2 = new fabric.Point(service2.x, service2.y);
        var midPoint = p1.midPointFrom(p2);
           var altService = createService(midPoint.x, midPoint.y, altNames[0], altIds[0], '../images/test.png');
           drawService(altService);

           drawConnection(createConnection(service1IdForAlternative, altService, 'COMPATIBLE'));
           drawConnection(createConnection(altService, service2IdForAlternative, 'COMPATIBLE'));

           deleteConnectionFromTo(service1IdForAlternative, service2IdForAlternative);

           updateConnectionList(getConnectionsBetween(service1IdForAlternative, altService));
           updateConnectionList(getConnectionsBetween(altService, service2IdForAlternative));
           updateConnectionList(getConnectionsBetween(service1IdForAlternative, service2IdForAlternative));

    }else if(altIds.length === 2) {
        var p1 = new fabric.Point(service1.x, service1.y);
        var p2 = new fabric.Point(service2.x, service2.y);
        var midPoint = p1.midPointFrom(p2);
        var altService1 = createService(midPoint.x+50, midPoint.y+50, altNames[0], altIds[0], '../images/test.png');
        var altService2 = createService(midPoint.x-50, midPoint.y-50, altNames[1], altIds[1], '../images/test.png');

        drawService(altService1);
        drawService(altService2);


        drawConnection(createConnection(service1IdForAlternative, altService1, 'COMPATIBLE'));
        drawConnection(createConnection(altService1, altService2, 'COMPATIBLE'));
        drawConnection(createConnection(altService2, service2IdForAlternative, 'COMPATIBLE'));

        deleteConnectionFromTo(service1IdForAlternative, service2IdForAlternative);

        updateConnectionList(getConnectionsBetween(service1IdForAlternative, altService1));
        updateConnectionList(getConnectionsBetween(altService1, altService2));
        updateConnectionList(getConnectionsBetween(altService2, service2IdForAlternative));
        updateConnectionList(getConnectionsBetween(service1IdForAlternative, service2IdForAlternative));
    }
}

// delete service
// remove it from the canvas and from data
function deleteService(service){
    canvas.remove(service);
    canvas.remove(service.label);
    if (service.image) {
        canvas.remove(service.image);
        // remove the image manually. canvas.remove() isn't working correctly with images.
        canvas._objects.splice(canvas._objects.indexOf(service.image), 1);
    }
    $.each(getConnections(service.id), function (index, connectionData) {
        deleteConnection(connectionData);
    });
    delete serviceObjs[service.id];
    delete data.services[service.id];
}

// delete connection
// remove it from the canvas and from data
function deleteConnection(connection){
    if (connection !== null) {
        var connectionObj = connectionObjs[connection.id];
        canvas.remove(connectionObj.compIcon);
        canvas.remove(connectionObj.arrow);
        canvas.remove(connectionObj);
        delete connectionObjs[connection.id];
        delete data.connections[connection.id];
    }
}

// delete a connection between two services
function deleteConnectionFromTo(s1, s2){
    var connection = getConnectionFromTo(s1, s2);
    deleteConnection(connection);
}

// When the user clicks on <span> (x), close the modal
span1.onclick = function() {
    console.log("clicked x")
    saveModal.style.display = "none";
    alternativeModal.style.display = "none";

}
span2.onclick = function() {
    console.log("clicked x")
    saveModal.style.display = "none";
    alternativeModal.style.display = "none";

}
