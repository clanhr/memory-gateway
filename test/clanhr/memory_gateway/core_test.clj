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

(defn new-model
  [datastore counter]
  (core/save! {:text "Hello"} datastore counter))

(defn create-15-models
  "Create 20 users"
  [datastore counter]
  (dotimes [n 15] (new-model datastore counter)))

(deftest save-and-list
  (let [datastore (core/datastore-atom)
        counter (core/counter-atom)
        batch-result (create-15-models datastore counter)
        query-result (core/paginated-query {:per-page 10 :page 1} datastore)
        second-query-result (core/paginated-query {:per-page 10 :page 2} datastore)]
    (is (= 10 (count (:data query-result))))
    (is (= 2 (:number-of-pages query-result)))
    (is (= 1 (:current-page query-result)))
    (is (= 5 (count (:data second-query-result))))
    (is (= 2 (:number-of-pages second-query-result)))
    (is (= 2 (:current-page second-query-result)))))
