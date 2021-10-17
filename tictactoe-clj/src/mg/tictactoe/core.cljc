(ns mg.tictactoe.core
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [clojure.set :as set]))

(def initial-state [])

(def grid-size 3)
(def rows grid-size)
(def cols grid-size)

(def free-symbol "_")

(s/def :t3/coord integer?)
(s/def :t3/coords (s/coll-of :t3/coord :into [] :count 2))
(s/def :t3/state (s/coll-of :t3/coords :into [] :type vector?))

(defn next-round [state]
  (-> state count (/ 2) long inc))

(defn next-player [state]
  (-> state count (mod 2) inc))

(defn player-won? [player-moves]
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
