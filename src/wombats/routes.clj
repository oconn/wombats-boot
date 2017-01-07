(ns wombats.routes
  (:require [io.pedestal.http :refer [html-body]]
            [wombats.interceptors.content-negotiation :refer [coerce-body content-neg-intc]]
            [wombats.handlers.static-pages :as static]
            [wombats.handlers.echo :as echo]
            [wombats.handlers.game :as game]
            [wombats.sockets.chat :as chat-ws]
            [wombats.sockets.game :as game-ws]))

(defn new-api-router
  [datomic]
  [[["/" ^:interceptors [html-body]
     {:get static/home-page}]
    ["/echo" {:get echo/echo}]
    ["/api"
     ^:interceptors [coerce-body
                     content-neg-intc]
     ["/v1"
      ["/games"
       {:get game/get-games
        :post game/add-game}]]]]])

(defn new-ws-router
  [datomic]
  {"/ws/chat" (chat-ws/chat-room-map datomic)
   "/ws/game" (game-ws/in-game-ws datomic)})
