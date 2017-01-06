(ns wombats.system
  (:require [com.stuartsierra.component :as component]
            [reloaded.repl :refer[init start stop go reset]]
            [wombats.components.configuration :as config-component]
            [wombats.components.datomic :as datomic-component]
            ;; [wombats.service :as service]
            ;; [wombats.components.pedestal :as pedestal-component]
            ))

;; [io.pedestal.http.jetty.websockets :as ws]

;; (defn pedestal-config
;;   []
;;   {:env          :dev
;;    ::http/routes (expand-routes routes/routes)
;;    ::http/type   :jetty
;;    ::http/port   8890
;;    ::http/join?  false
;;    ::http/container-options {:h2c? true
;;                              :h2? false
;;                              :ssl? false
;;                              :context-configurator #(ws/add-ws-endpoints % routes/sockets)}})

(defn system
  []
  (component/system-map
   :config (config-component/new-configuration)

   :datomic (component/using (datomic-component/new-database)
                             [:config])

   ;; :service (component/using (service/new-service)
   ;;                           [:config])

   ;; :pedestal (component/using
   ;;            (pedestal-component/new-pedestal pedestal-start-fn pedestal-stop-fn)
   ;;            {:service-map-provider :service
   ;;             :config               :config
   ;;             :datomic              :datomic})

   ))
