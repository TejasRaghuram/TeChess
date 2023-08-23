var state = "";
var moves = {};
var selection = '';
var canMove = false;

function init() {
    const board = document.getElementById('Board');
    var count = 0;
    for (let rank = 8; rank >= 1; rank--) {
        const row = document.createElement('div');
        row.className = 'Row';
        for (let file = 'a'; file != 'i'; file = String.fromCharCode(file.charCodeAt(0) + 1)) {
            const square = document.createElement('div');
            square.classList.add('Square');
            square.classList.add(count % 2 == 0 ? 'Light' : 'Dark');
            count++;

            const location = document.createElement('p');
            location.className = 'Location';
            location.textContent = file + rank;
            square.appendChild(location);

            const image = document.createElement('img');
            image.className = 'Piece';
            image.id = file + rank;
            image.src = './images/pieces/empty.png';
            image.onclick = function(){
                movement(this);
            };
            square.appendChild(image);

            row.appendChild(square);
        }
        board.appendChild(row);
        count++;
    }

    fetch('http://localhost:8080/api/start')
        .then(response => {
            return response.text();
        })
        .then(data => {
            state = data;
            fetch(`http://localhost:8080/api/moves/${state}`)
                .then(response => {
                    return response.json();
                })
                .then(data => {
                    moves = data;
                    canMove = true;
                });
            render();
        });
}

function render() {
    const pieces = {
        'K': './images/pieces/white_king.png',
        'k': './images/pieces/black_king.png',
        'Q': './images/pieces/white_queen.png',
        'q': './images/pieces/black_queen.png',
        'R': './images/pieces/white_rook.png',
        'r': './images/pieces/black_rook.png',
        'B': './images/pieces/white_bishop.png',
        'b': './images/pieces/black_bishop.png',
        'N': './images/pieces/white_knight.png',
        'n': './images/pieces/black_knight.png',
        'P': './images/pieces/white_pawn.png',
        'p': './images/pieces/black_pawn.png',
        '-': './images/pieces/empty.png',
    };
    const board = state.split(' ')[0];
    var rank = 8;
    for (const line of board.split('-')) {
        var file = 'a';
        for (const piece of line) {
            if (piece in pieces) {
                document.getElementById(file + rank).src = pieces[piece];
                file = String.fromCharCode(file.charCodeAt(0) + 1);
            } else {
                for (let i = 0; i < parseInt(piece); i++) {
                    document.getElementById(file + rank).src = pieces['-'];
                    file = String.fromCharCode(file.charCodeAt(0) + 1);
                }
            }
        }
        rank--;
    }

    document.getElementById("Status").textContent = state.split(' ')[1] == 'w' ? 'White to Move' : 'Black to Move';
}

function movement(square) {
    if (canMove) {
        refresh();
        if (selection != '' && moves[selection].includes(square.id)) {
            fetch(`http://localhost:8080/api/move/${state}/${selection}/${square.id}`)
                .then(response => {
                    return response.text();
                })
                .then(data => {
                    state = data;
                    moves = {}
                    selection = '';
                    canMove = false;
                    render();
                    computerMove();
                });
        } else if (selection != square.id && square.id in moves) {
            square.parentElement.classList.add('Selected');
            selection = square.id;
            for (const option of moves[square.id]) {
                document.getElementById(option).parentElement.classList.add('Option');
            }
        } else {
            selection = '';
        }
    }
}

function computerMove() {
    fetch(`http://localhost:8080/api/compute/${state}`)
        .then(response => {
            return response.text();
        })
        .then(data => {
            state = data;
            fetch(`http://localhost:8080/api/moves/${state}`)
                .then(response => {
                    return response.json();
                })
                .then(data => {
                    moves = data;
                    canMove = true;
                });
            render();
        });
}

function refresh() {
    for (let file = 'a'; file != 'i'; file = String.fromCharCode(file.charCodeAt(0) + 1)) {
        for (let rank = 1; rank <= 8; rank++) {
            document.getElementById(file + rank).parentElement.classList.remove('Selected');
            document.getElementById(file + rank).parentElement.classList.remove('Option');
        }
    }
}

init();