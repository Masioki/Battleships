var stompClient = null;
var username = null;

/**
 * Takes username from model, sets random ships and tries to connect to websocket
 */
window.onload = function () {
    randomizeShips();
    connect();
    username = $("#username").val();
}

function connect() {
    const socket = new SockJS("/stomp");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        //TODO
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
}

/**
 * Takes current ships, sends it to server with join request, after success subscribes to games topic.
 * @param gameID
 */
function joinGame(gameID) {
    if (stompClient != null) {
        const ships = getShips();
        $.post({
            url: "/join/" + gameID,
            dataType: "json",
            data: JSON.stringify(ships),
            success: [function () {
                stompClient.subscribe("/topic/game/" + gameID, function (message) {
                    decodeMessage(JSON.parse(message.body));
                });
                showMessageToUser("You joined the game");
            }],
            error: [function () {
                showMessageToUser("Game is already full");
            }]
        });
    } else showMessageToUser("Server connection problem, please reload.");
}

function leaveGame() {
    if (stompClient != null) {
        stompClient.unsubscribe();
        showMessageToUser("You left the game");
    }
}

function showMessageToUser(message) {
    $("#infoMessages").append(message);
}

/**
 * Decodes Message object
 * @param mes
 * @see Message.MessageType
 */
function decodeMessage(mes) {
    switch (mes.type) {
        case 'OPPONENT_DISCONNECTED': {
            showMessageToUser(mes.text);
            break;
        }
        case 'WRONG_MOVE': {
            showMessageToUser(mes.text);
            break;
        }
        case 'SHIP_DESTROYED': {
            move(mes.move, true, false);
            break;
        }
        case 'GAME_FINISHED': {
            move(mes.move, true, true);
            break;
        }
        case 'OK':
            move(mes.move, false, false);
    }
}

function move(moveDTO, shipDestroyed, gameFinished) {
    if (username == null) showMessageToUser("Error");
    else if (moveDTO.type === 'SURRENDER') surrender(moveDTO.username === username);
    else if (username === moveDTO.username) { //current user move confirmed by server
        if (gameFinished === true) {
            destroyPoint(false, moveDTO.x, moveDTO.y, true);
            showMessageToUser("Congratulations, You won!");
        } else if (shipDestroyed === true) {
            destroyPoint(false, moveDTO.x, moveDTO.y, true);
            showMessageToUser("You've destroyed opponents ship!");
        } else {
            destroyPoint(false, moveDTO.x, moveDTO.y, true);
        }
    } else { //opponents move
        if (gameFinished === true) {
            destroyPoint(true, moveDTO.x, moveDTO.y, true);
            showMessageToUser("Unfortunately, You lost ;/");
        } else if (shipDestroyed === true) {
            destroyPoint(true, moveDTO.x, moveDTO.y, true);
            showMessageToUser("Opponent has destroyed Your ship ;/");
        } else {
            destroyPoint(true, moveDTO.x, moveDTO.y, false);
        }
    }
}

function destroyPoint(mine, x, y, shipDestroyed) {

}

function surrender(me) {
    if (me === true) showMessageToUser("Congratulations, You won!");
    else showMessageToUser("Unfortunately, You lost ;/ ")
}

function getShips() {

}

function randomizeShips() {

}

