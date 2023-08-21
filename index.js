var state = "";

function init() {
    fetch('http://localhost:8080/api/start')
        .then(response => {
            return response.text();
        })
        .then(data => {
            state = data;
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

init();