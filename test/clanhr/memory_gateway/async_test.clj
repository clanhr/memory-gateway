(ns clanhr.memory-gateway.async-test
  (:require [clojure.test :refer :all]
            [clanhr.memory-gateway.core :as core]
            [clanhr.memory-gateway.async :as core-async]
            [clojure.core.async :refer [<!!]]))

(deftest saving-with-global-atoms
  (let [model {:text "Hello"}
        result (<!! (core-async/save! model))]
    (let [loaded (<!! (core-async/get-model (result :_id)))]
      (is (= "Hello" (:text loaded))))))

(deftest saving-with-given-atoms
  (let [datastore (core/datastore-atom)
        counter (core/counter-atom)
        model {:text "Hello"}
        result (<!! (core-async/save! model datastore counter))]
    (let [loaded (<!! (core-async/get-model (result :_id) datastore))]
      (is (= "Hello" (:text loaded))))))

