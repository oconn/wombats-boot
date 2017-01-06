(ns wombats.routes
  (:require [clojure.core.async :refer [put!]]
            [io.pedestal.http :refer [html-body]]
            [io.pedestal.http.jetty.websockets :as ws]
            [wombats.components.pedestal :refer [using-component]]
            [wombats.interceptors.content-negotiation :refer [coerce-body content-neg-intc]]
            [wombats.handlers.static-pages :as static]
            [wombats.handlers.echo :as echo]
            [wombats.handlers.game :as game]))

(def routes
  [[["/" ^:interceptors [html-body]
     {:get static/home-page}]
    ["/echo" {:get echo/echo}]
    ["/api"
     ^:interceptors [coerce-body
                     content-neg-intc
                     (using-component :datomic)]
     ["/v1"
      ["/games"
       {:get game/get-games
        :post game/add-game}]]]]])

;; Web Sockets

(def ^:private ws-clients (atom {}))

(defn- new-ws-client
  [ws-session send-ch]
  (let [uid (.hashCode ws-session)]
    (put! send-ch "This will be a text message")
    (swap! ws-clients assoc uid {:session ws-session
                                 :chan send-ch})))

(def sockets
  {"/ws/chat"
   {:on-connect (ws/start-ws-connection new-ws-client)
    :on-text (fn [msg] (prn (str "A client sent - " msg)))
    :on-binary (fn [payload offset length] (prn "Binary Message!"))
    :on-error (fn [t]
                (prn (str "WS Error happened"))
                (prn t))
    :on-close (fn [num-code reason-text]
                (prn (str "WS Closed:"))
                (prn reason-text))}
   "/ws/game"
   {:on-connect (ws/start-ws-connection new-ws-client)
    :on-text (fn [msg] (prn (str "A client sent a game message- " msg)))
    :on-binary (fn [payload offset length] (prn "Binary Message!"))
    :on-error (fn [t]
                (prn (str "WS Error happened"))
                (prn t))
    :on-close (fn [num-code reason-text]
                (prn (str "WS Closed:"))
                (prn reason-text))}})
