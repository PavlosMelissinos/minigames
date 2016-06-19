import itertools, random, time, getpass

board_width = 3
board_height = 3
board_size = board_width * board_height

default_token = '-'
tokens = ['X', 'O']

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

def player_move(board):
    move = 0
    valid_moves = free_spaces(board)
    while move not in [str(m) for m in valid_moves]:
        move = raw_input("What's your next move? (1-"+ str(board_size) + ") - ")
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

    center = 5
    corners = [1,3,7,9]
    random.shuffle(corners)
    middles = [2,4,6,8]
    random.shuffle(middles)

    if len(fs) == board_size:
        random.shuffle(corners)
        return corners[0]
    if center in fs:
        return center
    
    if (1 not in fs and 9 not in fs) or (3 not in fs and 7 not in fs):
        for move in middles:
            if move in fs:
                return move

    for move in corners:
        if move in fs:
            return move

    policy = [center] + corners + middles
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
    r1 = b1 == b2 == b3 != '-'
    r2 = b4 == b5 == b6 != '-'
    r3 = b7 == b8 == b9 != '-'
    c1 = b1 == b4 == b7 != '-'
    c2 = b2 == b5 == b8 != '-'
    c3 = b3 == b6 == b9 != '-'
    d1 = b1 == b5 == b9 != '-'
    d2 = b3 == b5 == b7 != '-'
    return r1 or r2 or r3 or c1 or c2 or c3 or d1 or d2

player_types = ['human', 'computer', 'ai']

def get_player_type(i):
    answer = raw_input('Player ' + i + ' type: ([0]-human/1-computer/2-ai) - ')
    ptype = int(answer) if answer and int(answer) in range(len(player_types)) else 0
    if answer == '2':
        print 'Creating a superintelligence just for you. Please wait...'
        time.sleep(5)
        print 'Almost ready...'
        time.sleep(2)
        print 'AI loading complete.'
        time.sleep(1)
        print 'Preparing a greeting...'
        time.sleep(2)
        print 'Hello, ' + getpass.getuser() + '!'
        ptype = 1
    return player_types[ptype]

players = 2

while True:
    player_types = [get_player_type(str(i+1)) for i in range(players) ]
    board = blank_board()
    draw_board()
    i = 0
    while True:
        player_id = i % players
        turn = player_types[i % len(player_types)]
        token = tokens[i % len(tokens)]
        print 'Now playing: Player ' + str(player_id + 1) + ' (' + turn + ')'
        if turn == 'human':
            move = player_move(board)
            board = place(board, move, token)
            draw_board()
        else:
            move = computer_move(token, tokens[(i+1) % len(tokens)])
            board = place(board, move, token)
            print 'Board state after computer play:\n'
            draw_board()
        if game_over(board):
            print 'Final state'
            draw_board()
            print turn.upper(), 'WON!'
            break
        elif free_spaces(board) == []:
            print 'Final state'
            draw_board()
            print 'DRAW!'
            break
        i += 1
    play_again = raw_input('Play again? ([yes]/no) - ')
    if play_again == 'no':
        print 'Thanks for playing! See you around!'
        exit()
