(ns wombats.daos.core
  (:require [datomic.api :as d]))

(defn- get-users
  [conn]
  (fn []
    (vec (d/q '[:find ?username
                :where [_ :user/username ?username]]
              (d/db conn)))))

(defn- add-user
  [conn]
  (fn [{:keys [username]}]
    (d/transact-async conn [{:db/id (d/tempid :db.part/user)
                             :user/username username}])))

(defn init-dao-map
  "Creates a map of all the data accessors that can be used inside of handlers / socket connections. This makes no assumption of authentication / authorization which should be handled prior to gaining access to these functions."
  [{:keys [conn] :as datomic}]
  {:get-users (get-users conn)
   :add-user (add-user conn)})

(defn get-fn
  [fn-key context]
  (fn-key (or (:wombats.interceptors.dao/daos context) {})))
