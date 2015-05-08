(ns clanhr.memory-gateway.core-test
  (:require [clojure.test :refer :all]
            [clanhr.memory-gateway.core :as core]))

(deftest saving-with-global-atoms
  (let [model {:text "Hello"}
        result (core/save! model)]
    (let [loaded (core/get-model (result :_id))]
      (is (= "Hello" (:text loaded))))))

(deftest saving-with-given-atoms
  (let [datastore (core/datastore-atom)
        counter (core/counter-atom)
        model {:text "Hello"}
        result (core/save! model datastore counter)]
    (let [loaded (core/get-model (result :_id) datastore)]
      (is (= "Hello" (:text loaded)))
      (let [loaded (core/get-model (str (result :_id)) datastore)]
        (is (= "Hello" (:text loaded)))))))
