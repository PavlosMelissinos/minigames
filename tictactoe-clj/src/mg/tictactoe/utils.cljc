(ns mg.tictactoe.utils)

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
      (when (seq ss)
        (concat (map first ss) (apply interleave-all (map rest ss))))))))
