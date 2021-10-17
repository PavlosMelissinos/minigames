(ns mg.tictactoe.cli
  (:require [clojure.string :as str]
            [mg.tictactoe.core :as core]
            [mg.tictactoe.utils :as utils]))

#_(def grid-symbol core/grid-symbol)
(def free-symbol core/free-symbol)
(def rows core/rows)
(def cols core/cols)

(def x-symbol "X")
(def o-symbol "O")


(defn index [r c]
  (-> (* r cols)
      (+ c)))

(defn fill [grid coords grid-symbol]
  (vec (assoc grid (apply index coords) grid-symbol)))

(defn render [state]
  (let [xs (take-nth 2 state)
        os (take-nth 2 (rest state))
        grid (vec (repeat (* rows cols) free-symbol))
        grid (reduce #(fill %1 %2 x-symbol) grid xs)
        grid (reduce #(fill %1 %2 o-symbol) grid os)]
    (map #(str/join " " %)) (partition cols grid)))

(defn derender [rendered-state]
  (let [rendered-state (map #(str/split % #" ") rendered-state)
        cols (apply min (map count rendered-state))
        moves (->> (map #(take cols %) rendered-state)
                   (apply concat)
                   (map-indexed #(vector [(int (/ %1 cols)) (mod %1 cols)] %2))
                   (filter #(not= free-symbol (second %))))
        x-moves (->> moves
                     (filter #(= x-symbol (second %)))
                     (map first))
        o-moves (->> moves
                     (filter #(= o-symbol (second %)))
                     (map first))]
    (utils/interleave-all x-moves o-moves)))

(defn summary [state score]
  (if (core/game-over? state)
    (cond
      (core/x-won? state) "Game over! Congratulations Player 1!"
      (core/o-won? state) "Game over! Congratulations Player 2!"
      :else "Game over! You truly are equals!")
    (str/join "\n" (concat [(str "Score: " (str/join " " score))
                        (str "Round " (core/next-round state))]
                       (render state)
                       [(str "Player " (core/next-player state) "'s turn:")]))))

(defn run-game [[x-score o-score]]
  (loop [state []]
    (let [_         (println (summary state [x-score o-score]))
          move      (core/play-turn {:moves state :play-type :prompt})
          state     (if move (concat state [move]) state)]
      (if (core/game-over? state)
        (do
          (println (summary state [x-score o-score]))
          [(if (core/x-won? state) (inc x-score) x-score)
           (if (core/o-won? state) (inc o-score) o-score)])
        (recur state)))))

(defn menu-continue-game? []
  (println "Continue game? Press q to quit game, anything else to start a new game")
  (not= "q" (str/lower-case (read-line))))

(defn run []
  (loop [score       [0 0]
         in-progress true]
    (if (not in-progress)
      (println (str "The final score is " score))
      (recur (run-game score) (menu-continue-game?)))))

(comment
  (run))
