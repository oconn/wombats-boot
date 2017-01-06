(ns wombats.components.datomic
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [io.pedestal.log :as log]))

;; Private helper methods



;; Component

(defrecord Datomic [config conn]
  component/Lifecycle
  (start [component]
    component)
  (stop [component]
    component))

;; Public component methods

(defn new-database
  "Datomic component. Creates database connection to Datomic.

  Note: This component is dependent on the Configuration component."
  []
  (map->Datomic {}))
