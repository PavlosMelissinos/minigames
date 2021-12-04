(ns battleship.test-main
  (:require [clojure.test :refer :all]
            [battleship.main :as sut]))


(def fleet-info)

(deftest test-build-fleet
  (is (= [{:name "aircraft carrier" :size 5}
          {:name "battleship" :size 4}
          {:name "cruiser" :size 3}
          {:name "destroyer" :size 2}
          {:name "destroyer" :size 2}
          {:name "submarine" :size 1}
          {:name "submarine" :size 1}]
         (sut/build-fleet {:fleet-info [{:name "aircraft carrier" :num 1 :size 5}
                                        {:name "battleship"       :num 1 :size 4}
                                        {:name "cruiser"          :num 1 :size 3}
                                        {:name "destroyer"        :num 2 :size 2}
                                        {:name "submarine"        :num 2 :size 1}]}))))
