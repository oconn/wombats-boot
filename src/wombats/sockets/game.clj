(ns wombats.sockets.game
  (:require [clojure.spec :as s]
            [clojure.core.async :refer [put!]]
            [io.pedestal.http.jetty.websockets :as ws]
            [wombats.sockets.core :as ws-core]))

(def ^:private game-connections (atom {}))

(s/def ::user-id int?)
(s/def ::game-id int?)
(s/def ::game-handshake (s/keys :req [::user-id ::game-id]))

(defn game-message
  [raw-msg]
  (let [msg (ws-core/parse-raw raw-msg)]
    (prn msg)))

(defn in-game-ws
  [datomic]
  {:on-connect (ws/start-ws-connection (ws-core/new-ws-connection game-connections))
   :on-text    game-message
   :on-error   ws-core/socket-error
   :on-close   ws-core/socket-close})
