(ns sum-of-spirals
  (:require [clojure.string :as str])
  (:gen-class))

; Auto-generated code below aims at helping you parse
; the standard input according to the problem statement.

(defn output [msg] (println msg) (flush))
(defn debug [msg] (binding [*out* *err*] (println msg) (flush)))

(defn steps [n]
  (let [maybe-skip-last
        (fn [steps]
          (if (even? n)
            (drop-last steps)
            steps))]
    (->> (range (dec n) 0 -2)
         (mapcat #(repeat 4 %))
         maybe-skip-last)))

(defn corners [start n]
  (let [corners (reduce (fn [corners step] (conj corners (+ (last corners) step))) [start] (steps n))]))

(defn solve [start n]
  (apply + (corners start n)))

(comment

  (steps 5)
  (steps 4)
  (corners 1 2)
  (corners 1 3)

  (all-corners 1 5)
  ;; => (1 5 9 13 17 19 21 23 25)
  [ 1  2  3  4 5]
  [16 17 18 19 6]
  [15 24 25 20 7]
  [14 23 22 21 8]
  [13 12 11 10 9]

  [ 1  2  3  4  5  6]
  [20 21 22 23 24  7]
  [19 32 33 34 25  8]
  [18 31 36 35 26  9]
  [17 30 29 28 27 10]
  [16 15 14 13 12 11]
  (solve 1 6)
  (all-corners 1 6)

  (let [n 3
        xc (corners 1 3)]
    (concat xc (corners (last xc) (- n 2))))

  (let [start 1
        n 5
        step (dec n)]
    (->> (iterate #(+ step %) start)
         (take 4)))

  (let [start 1
        n 5]
    (->> (iterate 1)))

  )

(defn -main [& args]
  (let [n (Integer/parseInt (read-line))]
    (debug n)
    (output (sum-of-corners 1 n))))
