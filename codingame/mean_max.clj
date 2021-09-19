(ns Player
  (:require [clojure.string :as str])
  (:gen-class))

; Auto-generated code below aims at helping you parse
; the standard input according to the problem statement.

(defn output [msg] (println msg) (flush))
(defn debug [msg] (binding [*out* *err*] (println msg) (flush)))

(def unit-keys [:id :type :player :mass :radius :x :y :vx :vy :extra :extra2])
(def unit-coercions
  (mapv #(if (= :mass %) Float/parseFloat Integer/parseInt) unit-keys))

(defn euclidean-distance [[x1 y1] [x2 y2]]
  (let [x-diff (- x1 x2)
        y-diff (- y1 y2)]
    (Math/sqrt (+ (* x-diff x-diff) (* y-diff y-diff)))))

(defn distance [{:keys [] x1 :x y1 :y} {x2 :x y2 :y}]
  (let [raw-dist (euclidean-distance [x1 y1] [x2 y2])]
    raw-dist))

(defn unit [unit-raw]
  (let []
    (->> (str/split unit-raw #" ")
         (map #(%1 %2) unit-coercions)
         (zipmap unit-keys))
    #_{:id (Integer/parseInt unit-id)
     :type (Integer/parseInt unitType)
     :player (Integer/parseInt player)
     :mass (Float/parseFloat mass)
     :radius (Integer/parseInt radius)
     :x (Integer/parseInt x)
     :y (Integer/parseInt y)
     :vx (Integer/parseInt vx)
     :vy (Integer/parseInt vy)
     :extra (Integer/parseInt extra)
     :extra2 (Integer/parseInt extra2)}))

(defn initialize-turn []
  (let [my-score (Integer/parseInt (read-line))
        enemy-score1 (Integer/parseInt (read-line))
        enemy-score2 (Integer/parseInt (read-line))
        my-rage (Integer/parseInt (read-line))
        enemy-rage1 (Integer/parseInt (read-line))
        enemy-rage2 (Integer/parseInt (read-line))
        unit-count (Integer/parseInt (read-line))
        units-raw  (repeatedly unit-count read-line)
        ]
    {:me/score my-score
     :me/rage my-rage
     :enemy-1/score enemy-score1
     :enemy-2/score enemy-score2
     :enemy-1/rage enemy-rage1
     :enemy-2/rage enemy-rage2
     :units (map unit units-raw)}}))

(defn -main [& args]
  (while true
    (let [state (initialize-turn)]
      (dotimes [i unitCount]
        (let [[unitId unitType player mass radius x y vx vy extra extra2] (str/split (read-line) #" ")]))

      ; (debug "Debug messages...")

      ; Write action to stdout
      (output "WAIT")
      (output "WAIT")
      (output "WAIT"))))
