(ns battleship.main)

(defn exit [return-code]
  (System/exit return-code))

(def fleet-info [{:name "aircraft carrier" :label "a" :num 1 :size 5}
                 {:name "battleship"       :label "b" :num 1 :size 4}
                 {:name "cruiser"          :label "c" :num 1 :size 3}
                 {:name "destroyer"        :label "d" :num 2 :size 2}
                 {:name "submarine"        :label "s" :num 2 :size 1}])

(defn mass-produce-ship [{:keys [num] :as ship-info}]
  (map #(dissoc % :num)
       (repeat num ship-info)))


(defn build-fleet [{:keys [fleet-info] :as ctx}]
  (flatten (map mass-produce-ship fleet-info)))

(defn grid [{:keys [height width] :as ctx}]
  (for [row    (range height)
        column (range width)]
    [row column]))

(defn allocate [{:keys [fleet height width]} g]
  (let [mask []]))

(defn main [args]
  (let [cfg {:height     8
             :width      8
             :fleet-info fleet-info}
        ctx (assoc cfg
              :fleet (build-fleet cfg))
        g   (grid ctx)
        g   (allocate ctx g)]
    (println g)))

(defn -main [& args]
  (main args)
  (exit 0))
