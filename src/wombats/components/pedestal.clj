(ns wombats.components.pedestal
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.jetty.websockets :as ws]))

;; Private helper functions

(defn- create-service-map
  [config service]
  (let [env (:env config)
        {:keys [port type
                join? container-options]} (get-in config [:settings :pedestal])
        {:keys [api-routes ws-routes]} (get-in service [:service])]
    {:env env
     ::http/routes api-routes
     ::http/port port
     ::http/type type
     ::http/join? join?
     ::http/container-options (merge container-options
                                     {:context-configurator #(ws/add-ws-endpoints %
                                                                                  ws-routes)})}))

(defn- start-http-server
  [service-map]
  (let [isDev? (= (:env service-map) :dev)]
    (cond-> service-map
      true http/default-interceptors
      isDev? http/dev-interceptors
      true (http/create-server)
      true (http/start))))

;; Component

(defrecord Pedestal [config service server]
  component/Lifecycle
  (start [component]
    (if server
      component
      (assoc component :server (-> (create-service-map config service)
                                   (start-http-server)))))
  (stop [component]
    (if-not server
      component
      (do
        (http/stop server)
        (assoc component :server nil)))))

;; Public component methods

(defn new-pedestal
  []
  (map->Pedestal {}))
