(ns mg.tictactoe.core-test
  (:require [mg.tictactoe.core :as sut]
            [clojure.test :refer [is deftest testing]]
            [clojure.string :as str]
            [clojure.pprint :as pp]
            [clojure.set :as set]))

(deftest test-turn
  (is (= ['(1 1)]
         (@#'sut/turn [] '(1 1)))))

(deftest test-index
  (is (= 10 (sut/index 2 4))))

(deftest test-fill
  (is (= ["_" "_" "_" "_" "_" "_" "_" "_" "X"]
         (sut/fill
          ["_" "_" "_" "_" "_" "_" "_" "_" "_"]
          [2 2]
          "X")))

  #_(is (= ["_" "_" "_" "_" "_" "_" "_" "_" "X"]
         (sut/fill
          ["_" "_" "_" "_" "_" "_" "_" "_" "_"]
          [2 2]
          "X"))))


(deftest test-render
  (is (= (sut/render [[2 0] [1 1] [2 2] [2 1]])
         ["_ _ _"
          "_ O _"
          "X O X"])))

(deftest test-derender
  (is (= (sut/derender ["_ _ _"
                        "_ O _"
                        "X O X"])
         [[2 0] [1 1] [2 2] [2 1]]))
  (is (= (sut/derender ["X _ _"
                        "_ O _"
                        "_ _ X"])
         [[0 0] [1 1] [2 2]])))

(deftest test-next-round
  (is (= 1 (sut/next-round
            (sut/derender ["_ _ _"
                           "_ _ _"
                           "_ _ _"]))))
  (is (= 1 (sut/next-round [[2 2]])))
  (is (= 3 (sut/next-round [[2 4] [2 3] [2 2] [2 1]]))))

(deftest test-next-player
  (is (= 1 (sut/next-player (sut/derender (sut/render [])))))
  (is (= 2 (sut/next-player [[2 2]])))
  (is (= 1 (sut/next-player (sut/derender ["_ _ _"
                                           "_ O _"
                                           "X O X"]))))
  (is (= 2 (sut/next-player (sut/derender ["X _ _"
                                           "_ O _"
                                           "_ _ X"])))))

(deftest test-player-won?
  (testing "horizontal wins"
    (is (= false (sut/player-won? [[0 0] [0 1] [2 2] [0 1]])))
    (is (= false (sut/player-won? (sut/derender ["X O _"
                                                 "_ _ _"
                                                 "_ _ X"]))))
    (is (= true (sut/player-won? [[2 0] [2 1] [2 2] [0 1]]))))

  (testing "vertical wins"
    (is (= true (sut/player-won? [[0 0] [1 0] [2 0]])))
    (is (= true (sut/player-won? [[0 0] [1 0] [1 1] [2 0]]))))

  (testing "diagonal wins"
    (is (= true (sut/player-won? [[0 0] [1 1] [2 2]])))
    (is (= true (sut/player-won? [[0 2] [1 1] [2 0]])))))

(deftest test-draw?
  (is (= true
         (sut/draw?
          (sut/derender ["X O X"
                         "X O O"
                         "O X X"]))))

  (is (= false (sut/draw?
                (sut/derender ["X _ _"
                               "_ O _"
                               "_ _ _"]))))

  (is (= false (sut/draw?
                (sut/derender ["X X O"
                               "X O O"
                               "X O X"])))))

(deftest test-game-over?
  (testing "Player 1 won"
    (is (= true (sut/game-over?
                 (sut/derender ["X _ O"
                                "X O _"
                                "X O X"]))))
    (is (= true (sut/game-over?
                 (sut/derender ["X X O"
                                "X O O"
                                "X O X"])))))
  (testing "Player 2 won"
    (is (= true (sut/game-over?
                 (sut/derender ["O X O"
                                "X X O"
                                "X _ O"])))))

  (testing "Game still in progress"
    (is (= false (sut/game-over?
                  (sut/derender ["X _ _"
                                 "_ O _"
                                 "_ _ _"])))))

  (testing "Draw"
    (is (= true (sut/game-over?
                 (sut/derender ["X O X"
                                "X O O"
                                "O X X"]))))))

(deftest test-valid-moves
  (is (= #{[0 1] [0 2] [1 0] [1 1] [1 2] [2 0] [2 1] [2 2]}
         (sut/valid-moves [[0 0]]))))
