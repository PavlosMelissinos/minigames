(ns mg.tictactoe.core
  (:require [clojure.string :as str]
            [clojure.pprint :as pp]
            [clojure.spec.alpha :as s]
            [clojure.set :as set]))

(def initial-state [])

(def grid-size 3)
(def rows grid-size)
(def cols grid-size)

(def free-symbol "_")
(def x-symbol "X")
(def o-symbol "O")

(s/def :t3/coord integer?)
(s/def :t3/coords (s/coll-of :t3/coord :into [] :count 2))
(s/def :t3/state (s/coll-of :t3/coords :into [] :type vector?))

(defn- turn [state coords]
  (conj state coords))
(s/fdef turn
  :args (s/cat :state :t3/state
               :coords :t3/coords))

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
    ;;(reductions #(fill %1 %2 x-symbol) grid xs)
    (->> (partition cols grid)
         (map #(str/join " " %))
         #_(str/join "\n"))))

(defn interleave-all
  "Returns a lazy seq of the first item in each coll, then the second, etc.
  Unlike `clojure.core/interleave`, the returned seq contains all items in the
  supplied collections, even if the collections are different sizes.
  source: https://github.com/weavejester/medley/blob/1.3.0/src/medley/core.cljc#L255-L272"
  {:arglists '([& colls])}
  ([] ())
  ([c1] (lazy-seq c1))
  ([c1 c2]
   (lazy-seq
    (let [s1 (seq c1), s2 (seq c2)]
      (if (and s1 s2)
        (cons (first s1) (cons (first s2) (interleave-all (rest s1) (rest s2))))
        (or s1 s2)))))
  ([c1 c2 & colls]
   (lazy-seq
    (let [ss (remove nil? (map seq (conj colls c2 c1)))]
      (if (seq ss)
        (concat (map first ss) (apply interleave-all (map rest ss))))))))

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
    (def x-moves x-moves)
    (def o-moves o-moves)
    (interleave-all x-moves o-moves)))

(comment
  (split-with #(= ))
  (derender ["_ _ _"
             "_ O _ _"
             "X O X"])
  (take 3 ["_" "O" "_" "_"]))

(defn next-round [state]
  (-> state count (/ 2) long inc))

(defn next-player [state]
  (-> state count (mod 2) inc))

(comment
  (next-round [1 2 3 4])
  (next-player [1 2 3 4]))

(defn all-rows [n-rows n-cols])

(defn all-cols [])
(defn all-diags [])

(defn player-won? [player-moves]
  (def p-m-glb player-moves)
  (let [player-moves  (set player-moves)
        moves-by-row  (vals (group-by first player-moves))
        moves-by-col  (vals (group-by second player-moves))
        moves-by-diag [(filter #(apply = %) player-moves)
                       (filter #(= (dec grid-size) (apply + %)) player-moves)]]
    (->> (apply concat [moves-by-row moves-by-col moves-by-diag])
         (some #(>= (count %) grid-size))
         boolean)))

(defn x-won? [state] (player-won? (take-nth 2 state)))
(defn o-won? [state] (player-won? (take-nth 2 (rest state))))

(defn draw? [state]
  (and (= (* rows cols) (count state))
       (not (x-won? state))
       (not (o-won? state))))

(defn game-over? [state]
  (or (x-won? state)
      (o-won? state)
      (draw? state)))

(defn valid-moves [move-history]
  (let [all-possible-moves (set (for [r (range rows)
                                      c (range cols)]
                                  [r c]))]
    (set/difference all-possible-moves (set move-history))))

(defmulti play-turn :play-type)

(defmethod play-turn :prompt [{:keys [moves]}]
  (let [choices (vec (valid-moves moves))
        _       (println
                 (str/join "\n" (map-indexed #(str %1 ": " %2) choices))
                 "\n"
                 "Make your choice")
        choice  (read-line)]
    (try
      (do
        (nth choices (Long/parseLong choice)))
      (catch Exception _
        (do
          (println choice "is not a valid input")
          nil)))))

(defmethod play-turn :default [state] state)

(comment
  (play-turn {:play-type :prompt
              :moves     []}))

(defn summary [state score]
  (if (game-over? state)
    (cond
      (x-won? state) "Game over! Congratulations Player 1!"
      (o-won? state) "Game over! Congratulations Player 2!"
      :else "Game over! You truly are equals!")
    (str/join "\n" (concat [(str "Score: " (str/join " " score))
                        (str "Round " (next-round state))]
                       (render state)
                       [(str "Player " (next-player state) "'s turn:")]))))

(defn run-game [[x-score o-score]]
  (loop [state []]
    (let [_         (println (summary state [x-score o-score]))
          ;; _ (mapv println (render state))
          ;; _ (print (str "Player " (next-player state) "'s turn:"))
          move      (play-turn {:moves state :play-type :prompt})
          state     (if move (concat state [move]) state)]
      (if (game-over? state)
        (do
          (println (summary state [x-score o-score]))
          [(if (x-won? state) (inc x-score) x-score)
           (if (o-won? state) (inc o-score) o-score)])
        (recur state)))))

(defn menu-continue-game? []
  (println "Continue game? Press q to quit game, anything else to start a new game")
  (not= "q" (str/lower-case (read-line))))

(defn run []
  #_(let [in-progress (atom true)
        score       (atom [0 0])]
    (while @in-progress
      (swap! score (run-game @score))
      (swap! in-progress)
      (menu-continue-game?)))
  (loop [score       [0 0]
         in-progress true]
    (if (not in-progress)
      (println (str "The final score is " score))
      (recur (run-game score) (menu-continue-game?)))))

(comment
  (println (render []))
  (println (render [[0 0] [0 2] [1 1]]))
  (int (/ 3 2)))
