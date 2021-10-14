(ns Player
  (:require [clojure.string :as str])
  (:gen-class))

; Auto-generated code below aims at helping you parse
; the standard input according to the problem statement.

(defn output [msg] (println msg) (flush))
(defn debug [msg] (binding [*out* *err*] (println msg) (flush)))

(def unit-keys [:id :type :player :mass :radius :x :y :vx :vy :extra :extra2])
(def unit-coercions
  (mapv (fn [k] (if (= :mass k) #(Double/parseDouble %) #(Long/parseLong %))) unit-keys))

(defn euclidean-distance [[x1 y1] [x2 y2]]
  (let [x-diff (- x1 x2)
        y-diff (- y1 y2)]
    (Math/sqrt (+ (* x-diff x-diff) (* y-diff y-diff)))))

(defn manhattan-distance [[x1 y1] [x2 y2]]
  (let [x-diff (- x1 x2)
        y-diff (- y1 y2)]
    (+ x-diff y-diff)))

(defn distance [{x1 :x y1 :y} {x2 :x y2 :y}]
  (let [raw-dist (manhattan-distance [x1 y1] [x2 y2])]
    raw-dist))

(defn unit [unit-raw]
  (->> (str/split unit-raw #" ")
       (map #(%1 %2) unit-coercions)
       (zipmap unit-keys)))

(defn initialize-turn []
  (let [my-score (Long/parseLong (read-line))
        enemy-score1 (Long/parseLong (read-line))
        enemy-score2 (Long/parseLong (read-line))
        my-rage (Long/parseLong (read-line))
        enemy-rage1 (Long/parseLong (read-line))
        enemy-rage2 (Long/parseLong (read-line))
        unit-count (Long/parseLong (read-line))
        units-raw  (repeatedly unit-count read-line)]
    {:me/score my-score
     :me/rage my-rage
     :enemy-1/score enemy-score1
     :enemy-2/score enemy-score2
     :enemy-1/rage enemy-rage1
     :enemy-2/rage enemy-rage2
     :units (mapv unit units-raw)}))

(defmulti play (fn [_ _ strategy] strategy))

(defmethod play :closest-target [targets player _]
  (debug targets)
  (let [{:keys [x y]}
        (if (seq targets)
          (apply min-key #(distance player %) targets)
          player)

        throttle 300]
    (str x " " y " " throttle)))

(defn play-turn [{:keys [units] :as state}]
  (let [wrecks  (filter #(= (:type %) 4) units)
        tankers (filter #(= (:type %) 3) units)
        player-reaper
        (first (filter #(and (= (:player %) 0) (= (:type %) 0)) units))
        player-destroyer
        (first (filter #(and (= (:player %) 0) (= (:type %) 1)) units))]
    [(play wrecks player-reaper :closest-target)
     (play tankers player-destroyer :closest-target)
     "WAIT"]))

(defn -main [& args]
  (while true
    (let [state (initialize-turn)
          moves  (play-turn state)]
      ; (debug "Debug messages...")
      ;;(debug (count (:units state)))

      ; Write action to stdout
      (mapv output moves))))
