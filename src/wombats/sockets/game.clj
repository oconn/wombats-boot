(ns wombats.sockets.game
  (:require [clojure.spec :as s]
            [clojure.core.async :refer [put!]]
            [io.pedestal.http.jetty.websockets :as ws]
            [wombats.sockets.core :as ws-core]))

(def ^:private game-connections (atom {}))

(defn reset-atom
  []
  (reset! game-connections {}))

(s/def ::user-id int?)
(s/def ::game-id int?)
(s/def ::game-handshake (s/keys :req [::user-id ::game-id]))

(defn- command-handler
  [chan-id msg]
  )

(def ^:private
  message-handlers
  {:cmd command-handler})

(defn in-game-ws
  [datomic]
  {:on-connect (ws/start-ws-connection (ws-core/new-ws-connection game-connections))
   :on-text    (ws-core/create-socket-handler-map message-handlers
                                                  game-connections)
   :on-error   ws-core/socket-error
   :on-close   ws-core/socket-close})
