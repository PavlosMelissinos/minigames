import random
import time

EMPTY = '-'  # empty
OCCUPIED = 'X'  # occupied

HIT = '*'  # hit
MISSED = '@'  # hit

###### setup ######

def pieces(txtfile='pieces.txt'):
    with open(txtfile, 'r') as f:
        return [int(line.rstrip('\n')) for line in f]

def draw_board(n=8, m=8):
    n = max(n, 8)
    m = max(m, 8)
    board = [[EMPTY] * n for r in range(m)]   
    return board


def help():
    return '''Enter ship coordinates:
input row (1 to board height), column(1 to board width) and direction('d' for down, 'r' for right), separated by space
or just press enter to randomly place next ship
'''


def valid_starts(board, ship):
    positions = {'d':[], 'r':[]}
    for i, row in enumerate(board):
        for j, col in enumerate(row):
            if i + ship < len(board):
                positions['d'].append((i, j))
            if j + ship < len(board[i]):
                positions['r'].append((i, j))
    return positions


def valid(board, ship, r, c, d):
    vs = valid_starts(board, ship)
    if (r, c) not in vs[d]:
        print('ship starting from {} {} with direction {} does not fit in the grid'.format(r, c, d))
        return False
    for offset in range(ship):
        rt, ct = (r, c + offset) if d == 'r' else (r + offset, c)
        if board[rt][ct] == OCCUPIED:
            return False
    return True


def populate(board, ship, r, c, d):
    for offset in range(ship):
        rt, ct = (r, c + offset) if d == 'r' else (r + offset, c)
        board[rt][ct] = OCCUPIED
    return board


def populate_auto(board, ship):
    vs = valid_starts(board, ship)
    vs = [(start[0], start[1], key) for key, value in vs.iteritems() for start in value]
    random.shuffle(vs)
    for starts in vs:
	r, c, d = starts[0], starts[1], starts[2]
        if valid(board, ship, r, c, d):
            board = populate(board, ship, r, c, d)
            break
    return board, r, c, d


def populate_user(board, ship):
    mode = 'user'
    userinp = raw_input('coordinates for ship with length {}: '.format(ship))
    if userinp == 'help':
        print(help())
        board, mode = populate_user(board, ship)
    elif userinp == 'render':
        render(board)
        board, mode = populate_user(board, ship)
    elif userinp == '':
        board, r, c, d = populate_auto(board, ship)
        print('{} {} {}'.format(r, c, d))
    else:
        r, c, d = userinp.split(' ')
        r, c = int(r), int(c)
        if not valid(board, ship, r, c, d):
            print('Invalid input')
            print(help())
            board, mode = populate_user(board, ship)
        else:
            board = populate(board, ship, r, c, d)
    return board, mode


def populate_all(board, ships, mode='auto'):
    for ship in ships:
        if mode == 'auto':
            board, _, _, _ = populate_auto(board, ship)
        elif mode == 'user':
            board, mode = populate_user(board, ship)
    return board


def setup():
    game = {}
    game['human'] = {}
    game['ai'] = {}
    game['human']['board'] = populate_all(draw_board(), pieces(), mode='user')
    game['human']['name'] = 'Human'

    game['ai']['board'] = populate_all(draw_board(), pieces())
    game['human']['targets_board'] = draw_board()
    game['ai']['targets_board'] = draw_board()
    game['ships'] = pieces()
    game['ai']['name'] = 'The computer'
    return game


###### game ######


def human_play(board, targets_board):
    # human playing
    target = raw_input('Choose target (row and column, separated by space): ')
    while target == 'render':
        render(targets_board)
        target = raw_input('Choose target (row and column, separated by space): ')
    while ' ' not in target:
        print('Incorrect input format, please try again')
        target = raw_input('Choose target (row and column, separated by space): ')
    r, c = target.split(' ')
    r, c = int(r), int(c)
    # board = targets_board
    if r > len(board):
        print('Selected row exceeds bounds, truncating to maximum possible ({})'.format(len(board) - 1))
        r = len(board) - 1
    if c > len(board[r]):
        print('Selected column exceeds bounds, truncating to maximum possible ({})'.format(len(board[r]) - 1))
        c = len(board[r]) - 1
    targets_board[r][c] = HIT
    # if ai_board[r][c] == OCCUPIED:
    targets_board[r][c] = HIT if board[r][c] == OCCUPIED else MISSED
    return targets_board, targets_board[r][c]


def ai_play(board, targets_board):
    # ai playing
    # random strategy
    print('computer is thinking its next move')
    time.sleep(random.randint(2, 5))
    free_positions = []
    for ri, row in enumerate(targets_board):
        for ci, cell in enumerate(targets_board[ri]):
            if cell == EMPTY:
                free_positions.append((ri, ci))
    r, c = random.choice(free_positions)
    targets_board[r][c] = HIT if board[r][c] == OCCUPIED else MISSED
    return targets_board, targets_board[r][c]


def over(game):
    print('SHIPS IN THIS GAME: {}'.format(game['ships']))
    ai_hits = 0
    for i, row in enumerate(game['ai']['targets_board']):
        ai_hits += row.count('*')
    if ai_hits == sum(game['ships']):
        return True, 'ai'

    human_hits = 0
    for i, row in enumerate(game['human']['targets_board']):
        human_hits += row.count('*')
    if human_hits == sum(game['ships']):
        return True, 'human'

    return False, None


###### render ######


def render(board, type='setup'):
    if type == 'setup':
        mapper = {}  # TODO
    else:
        assert type == 'play'
        mapper = {}  # TODO
    print(' ' * 2 + ' '.join([str(i) for i in range(len(board))]))
    for idx, row in enumerate(board):
        print(' '.join([str(idx)] + row))


###### main ######

if __name__ == '__main__':
    playing = True
    while playing:
        G = setup()
        print('##### HUMAN BOARD #####')
        render(G['human']['board'])
        # print('##### AI BOARD #####')
        # render(ai_board)
        
        # ACTUAL GAME
        while not over(G)[0]:
            last = HIT
            while last == HIT:
                G['human']['targets_board'], last = human_play(G['ai']['board'], G['human']['targets_board'])
                # game = human_play(G)
                render(G['human']['targets_board'])
                if last == HIT:
                    print('Sucessful hit! Extra chance!')

            if over(G)[0]:
                break

            last = HIT
            while last == HIT:
                G['ai']['targets_board'], last = ai_play(G['human']['board'], G['ai']['targets_board'])
                render(G['ai']['targets_board'])
                if last == HIT:
                    print('We have been hit! The enemy plays again!')

        winner = over(G)[1]
        render(G['human']['board'])
        render(G['ai']['board'])
        if winner == 'human':
            print('Congratulations, you have won!')
        else:
            print('The computer has won! Better luck next time')
        while playing != 'n' and playing != 'y':
            playing = raw_input('Do you want to play another round? (Yn)').lower()
        playing = True if playing == 'y' else playing


