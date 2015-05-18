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
  ([datastore counter]
    (core/save! {:text "Hello"} datastore counter))
  ([account datastore counter]
    (core/save! {:account account :text "Hello"} datastore counter)))

(defn create-15-models
  "Create 15 models"
  [datastore counter]
  (dotimes [n 15] (new-model datastore counter)))

(defn create-15-models-two-accounts
  "Create 15 models"
  [datastore counter]
  (dotimes [n 7] (new-model 1 datastore counter))
  (dotimes [n 7] (new-model 2 datastore counter)))

(deftest save-and-list
  (let [datastore (core/datastore-atom)
        counter (core/counter-atom)
        batch-result (create-15-models datastore counter)
        query-result (core/paginated-query {:per-page 10 :page 1} nil datastore)
        second-query-result (core/paginated-query {:per-page 10 :page 2} nil datastore)]
    (is (= 10 (count (:data query-result))))
    (is (= 2 (:number-of-pages query-result)))
    (is (= 1 (:current-page query-result)))
    (is (= 5 (count (:data second-query-result))))
    (is (= 2 (:number-of-pages second-query-result)))
    (is (= 2 (:current-page second-query-result)))))

(deftest save-filter-and-list
  (let [datastore (core/datastore-atom)
        counter (core/counter-atom)
        batch-result (create-15-models-two-accounts datastore counter)
        query-result (core/paginated-query {:per-page 10 :page 1} #(= 1 (get-in % [:account])) datastore)]
    (is (= 7 (count (:data query-result))))
    (is (= 1 (:number-of-pages query-result)))
    (is (= 1 (:current-page query-result)))))

(deftest clear-db-test
  (let [datastore (core/datastore-atom)
        counter (core/counter-atom)
        batch-result (create-15-models datastore counter)
        before-clear-query-result (core/paginated-query {:per-page 10 :page 1} nil datastore)]
    (is (= 10 (count (:data before-clear-query-result))))

    (core/clear-db! datastore)

    (let [after-clear-query-result (core/paginated-query {:per-page 10 :page 1} nil datastore)]
      (is (= 0 (count (:data after-clear-query-result)))))))

(deftest save-and-get-with-custom-ids
  (let [raw-id "553a779ee4b0255bad041854"
        model {:_id raw-id}
        saved-model (core/save! model)
        loaded-model (core/get-model raw-id)]
    (is (= raw-id (:_id saved-model)))
    (is (= raw-id (:_id loaded-model)))))
