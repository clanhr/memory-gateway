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

(def ^:private global-datastore (datastore-atom))
(def ^:private global-counter (counter-atom))

(defn clear-db!
  "Erases all the database"
  ([]
   (clear-db! global-datastore))
  ([datastore]
    (reset! datastore {})))

(defn gen-id
  "Generates an id"
  [counter-atom]
  (str (swap! counter-atom inc)))

(defn with-id
  "Adds an id to the hash, if none exists"
  ([model]
   (with-id model global-counter))
  ([model counter-atom]
   (if (nil? (:_id model))
     (assoc model :_id (gen-id counter-atom))
     model)))

(defn mem-id
  "Uniformize id type"
  [id]
  id)

(defn save!
  "Saves a model"
  ([model]
   (save! model global-datastore global-counter))
  ([model datastore-atom counter-atom]
   (let [model-with-id (with-id model counter-atom)]
     (swap! datastore-atom assoc (:_id model-with-id) model-with-id)
     model-with-id)))

(defn get-model
  "Gets a model given it's id"
  ([model-id]
   (get-model model-id global-datastore))
  ([model-id datastore-atom]
   (get @datastore-atom (mem-id model-id))))

(defn paginated-query
  "Gets a paginated-list"
  ([query filter-fn]
   (paginated-query query filter-fn global-datastore))
  ([query filter-fn datastore-atom]
    (let [per-page (:per-page query)
          filters (:filters query)
          page (:page query)
          without-n-items (* per-page (- page 1))
          store (vals @datastore-atom)
          filtered-data (if-not (nil? filter-fn) (filter filter-fn store) store)
          results (take per-page (drop without-n-items filtered-data))
          total (count filtered-data)
          number-of-pages (quot (+ total per-page) per-page)]
      {:data results
       :number-of-pages number-of-pages
       :current-page page})))
