(ns clanhr.memory-gateway.core
  "Memory gateway utilities")

(defn datastore-atom
  "Creates an atom for the datastore"
  []
  (atom {}))

(defn counter-atom
  "Creates an atom to count models"
  []
  (atom 0))

(defn- gen-id
  "Generates an id"
  [counter-atom]
  (swap! counter-atom inc))

(defn with-id
  "Adds an id to the hash, if none exists"
  [model counter-atom]
  (if (nil? (:_id model))
    (assoc model :_id (gen-id counter-atom))
    model))

(defn mem-id
  "Uniformize id type"
  [id]
  (if (string? id)
    (Integer/parseInt id)
    id))

(defn save!
  "Saves a model"
  [model datastore-atom counter-atom]
  (let [model-with-id (with-id model counter-atom)]
    (swap! datastore-atom assoc (:_id model-with-id) model-with-id)
    model-with-id))

(defn get-model
  "Gets a model given it's id"
  [model-id datastore-atom]
  (get @datastore-atom model-id))
