import itertools, random

board_width = 3
board_height = 3
board_size = board_width * board_height

default_token = '-'
tokens = ['X', 'O']
players = ['player', 'computer']

def blank_board():
    board = [default_token for i in range(board_size)]
    return board

def draw_board():
    for i in range(0, board_size, board_width):
        print ' '.join(board[i : i + board_width])

def place(board, i, token):
    i = int(i)
    i = (i - 1) % board_size
    board[i] = token
    return board

def player_move():
    move = 0
    valid_moves = range(1, board_size + 1)
    while move not in [str(m) for m in valid_moves]:
        move = raw_input("Move must be a number in" + str(valid_moves) + ". Enter your move - ")
    return move

def free_spaces(board):
    return [idx + 1 for idx, cell in enumerate(board) if cell == default_token]

def computer_move(token, other_token):
    fs = free_spaces(board)
    # try to find winning move
    for move in fs:
        board_copy = list(board)
        board_copy = place(list(board), move, token)
        if game_over(board_copy):
            return move

    # stop other player from winning
    for move in fs:
        board_copy = list(board)
        board_copy = place(list(board), move, other_token)
        if game_over(board_copy):
            return move

    center = [5]
    corners = [1,3,7,9]
    random.shuffle(corners)
    rest = [2,4,6,8]
    random.shuffle(rest)
    policy = center + corners + rest # first center, then corners, then rest
    for move in policy:
        if move in fs:
            return move

def ai_move():
    print "TODO"
    pass
        
def game_over(b):
    b1 = b[0]
    b2 = b[1]
    b3 = b[2]
    b4 = b[3]
    b5 = b[4]
    b6 = b[5]
    b7 = b[6]
    b8 = b[7]
    b9 = b[8]
    r1 = b1 == b2 and b2 == b3 and b1 != '-'
    r2 = b4 == b5 and b5 == b6 and b4 != '-'
    r3 = b7 == b8 and b8 == b9 and b7 != '-'
    c1 = b1 == b4 and b4 == b7 and b1 != '-'
    c2 = b2 == b5 and b5 == b8 and b2 != '-'
    c3 = b3 == b6 and b6 == b9 and b3 != '-'
    d1 = b1 == b5 and b5 == b9 and b1 != '-'
    d2 = b3 == b5 and b5 == b7 and b3 != '-'
    return r1 or r2 or r3 or c1 or c2 or c3 or d1 or d2

#player_types = ['human', 'computer']
#player_types = ['computer', 'computer']

def get_player_type(i):
    answer = raw_input('Is player ' + i + ' a computer? ([yes]/no) - ')
    return 'human' if answer == 'no' else 'computer'

players = 2

while True:
    player_types = [get_player_type(str(i+1)) for i in range(players) ]
    board = blank_board()
    draw_board()
    i = 0
    while True:
        turn = player_types[i % len(player_types)]
        token = tokens[i % len(tokens)]
        if turn == 'human':
            move = player_move()
            board = place(board, move, token)
            draw_board()
        else:
            move = computer_move(token, tokens[(i+1) % len(tokens)])
            board = place(board, move, token)
            print 'Board state after computer play:\n'
            draw_board()
        if game_over(board):
            draw_board()
            print turn.upper(), 'WON!'
            break
        elif free_spaces(board) == []:
            print 'DRAW!'
            break
        i += 1
    play_again = raw_input('Play again? ([yes]/no) - ')
    if play_again == 'no':
        print 'Thanks for playing! See you around!'
        exit()
