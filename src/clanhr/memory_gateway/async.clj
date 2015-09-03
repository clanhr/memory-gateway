(ns clanhr.memory-gateway.async
  "Memory async gateway utilities"
  (:require [clanhr.memory-gateway.core :as core]
            [clojure.core.async :refer [go]]))

(defn save!
  "Returns a channel with the result"
  [& args]
  (go (apply core/save! args)))

(defn get-model
  "Returns a channel with the result"
  [& args]
  (go (apply core/get-model args)))

(defn paginated-query
  "Returns a channel with the result"
  [& args]
  (go (apply core/paginated-query args)))

(defn begin!
  [& args])

(defn commit!
  [& args])

(defn rollback!
  [& args])
