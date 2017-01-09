(ns wombats.sockets.game
  (:require [clojure.spec :as s]
            [clojure.core.async :refer [put!]]
            [io.pedestal.http.jetty.websockets :as ws]
            [wombats.sockets.core :as ws-core]))

(def ^:private game-connections (atom {}))

(s/def ::user-id int?)
(s/def ::game-id int?)
(s/def ::game-handshake (s/keys :req [::user-id ::game-id]))

;; -------------------------------------
;; -------- Message Handlers -----------
;; -------------------------------------

(defn- command-handler
  [chan-id msg]
  ;; TODO Handle command queue
  )

(defn- handshake-handler
  "Performs the inital game handshake.

  Handshake Steps:

  1. Client - initiate request, passing conn-id, user-token, and game-id
  2. Server - Authenticate user and lookup game
  3. Server - Check user is authorized to join game
  4. Server - Verify conn-id exists & not take. Assign user data to connection
  5. Server - Send frames (in game) / status messages (pre / post game)"
  [datomic]
  (fn [chan-id msg]
    ;; TODO Auth / Game lookup
    (swap! game-connections assoc-in [chan-id :metadata] msg)))

(defn- message-handlers
  [datomic]
  {:handshake (handshake-handler datomic)
   :cmd command-handler})

;; -------------------------------------
;; -------- Public Functions -----------
;; -------------------------------------

(defn in-game-ws
  [datomic]
  {:on-connect (ws/start-ws-connection (ws-core/new-ws-connection game-connections
                                                                  datomic))
   :on-text    (ws-core/create-socket-handler-map (message-handlers datomic)
                                                  game-connections)
   :on-error   ws-core/socket-error
   :on-close   ws-core/socket-close})

;; -------------------------------------
;; -------- Dev Funcitons -------------
;; -------------------------------------

(defn- reset-atom
  "NOTE: Working on clearing out closed soket connections.
  In the time being this will work."
  []
  (reset! game-connections {}))
