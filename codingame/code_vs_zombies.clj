(ns Player
  (:require [clojure.string :as str])
  (:gen-class))

; Save humans, destroy zombies!

(def ash-speed 1000)
(def zombie-speed 400)

(defn output [msg] (println msg) (flush))
(defn debug [msg] (binding [*out* *err*] (println msg) (flush)))

(defn human [human-string]
;;   (let [h (zipmap [:id :x :y] (mapv #(Integer/parseInt %) (str/split human-string #" ")))]
;;   (assoc h :x-next (:x h)
;;            :y-next (:y h)))
  (zipmap [:id :x :y] (mapv #(Integer/parseInt %) (str/split human-string #" "))))

(defn zombie [zombie-string]
  (zipmap [:id :x :y :x-next :y-next] (mapv #(Integer/parseInt %) (str/split zombie-string #" "))))

(defn ash [player-string]
  (zipmap [:x :y] (mapv #(Integer/parseInt %) (str/split player-string #" "))))

(defn initialize-round []
  (let [player-string  (read-line)
        human-count    (Integer/parseInt (read-line))
        human-strings  (vec (repeatedly human-count read-line))
        zombie-count   (Integer/parseInt (read-line))
        zombie-strings (repeatedly zombie-count read-line)]
    {:ash     (ash player-string)
     :humans  (mapv human human-strings)
     :zombies (mapv zombie zombie-strings)}))

(defn euclidean-distance [a b]
  (Math/sqrt
    (+ (Math/pow (- (:x a) (:x b)) 2)
       (Math/pow (- (:y a) (:y b)) 2))))

(defn distance [a b]
  (let [speed (or (:speed a) (:speed b) 0)]
    (-> (euclidean-distance a b)
        (/ speed)
        (Math/ceil))))

(defn closest [a group]
  (first (sort-by (partial distance a) group)))

(defn hunts [zombies humans]
  (map (fn [z] (assoc z :target (closest z humans))) zombies))

(defmulti play-round :strategy)

(defmethod play-round :first-zombie [state]
  ;; ~20K points
  (let [target (->> state
                    :zombies
                    first)]
    {:x (:x-next target)
     :y (:y-next target)}))

(defmethod play-round :closest-zombie [state]
  (let [target (closest (:ash state) (:zombies state))]
    {:x (:x-next target)
     :y (:y-next target)}))

(defmethod play-round :closest-human [state]
  ;; ~10K points, ouch
  (let [target (closest (:ash state) (:humans state))]
    (cond
     (-> state :humans count (= 1))
      {:x (-> state :humans first :x)
       :y (-> state :humans first :y)}
     :else
     {:x (:x target)
      :y (:y target)})))

(defn -main [& args]
  (def round-counter (atom 0))
  (while true
    (swap! round-counter inc)
    (let [{:keys [x y] :as action} (play-round (assoc (initialize-round) :strategy :closest-zombie))]
      (debug @round-counter)
      (debug (str x " " y))
      ;(debug "Debug messages...")
      ;(debug round-state))
      ; Your destination coordinates
      (output (str x " " y)))))
