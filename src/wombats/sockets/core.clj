(ns wombats.sockets.core
  "Core library for working with wombat socket channels"
  (:require [clojure.core.async :as a :refer [put! <! go-loop timeout]]
            [clojure.edn :as edn]))

;; Helpers

(defn parse-raw
  [raw-message]
  (try
    (edn/read-string raw-message)
    (catch Exception e (prn (str "Invalid client message: " raw-message)))))

(defn send-msg [msg] (prn-str msg))

;; Handlers

(defn socket-error
  [t]
  (prn (str "WS Error " (.getMessage t))))

(defn socket-close
  [code reason]
  (prn (str "WS Closed - Code: " code " Reason: " reason)))

(defn- handshake-handler
  [ws-atom]
  (fn [chan-id msg]
    (swap! ws-atom assoc-in [chan-id :metadata] msg)
    (clojure.pprint/pprint @ws-atom)))

(defn- keep-alive
  [chan-id msg])

(defn create-socket-handler-map
  [handler-map ws-atom]
  (fn [raw-msg]
    (let [msg (parse-raw raw-msg)
          {:keys [chan-id msg-type]} (get msg :meta)
          msg-payload (get msg :payload {})
          msg-fn (msg-type (merge handler-map
                                  {:handshake (handshake-handler ws-atom)
                                   :keep-alive keep-alive}))]

      ;; Log in dev mode
      (println "\n---------------------------------")
      (clojure.pprint/pprint msg)
      (println "---------------------------------\n\n")

      (msg-fn chan-id msg-payload))))

(defn- remove-chan
  [ws-atom chan-id]
  (prn (str "remove-chan" chan-id)))

(defn new-ws-connection
  [ws-atom]
  (fn [ws-session send-ch]
    (let [chan-id (.hashCode ws-session)]
      (prn (str "Connection " chan-id " establised"))

      ;; Poll for closed socket
      (go-loop [is-closed? (.closed? send-ch)]
        (<! (timeout 1000))
        (prn is-closed?)
        (if is-closed?
          (remove-chan ws-atom chan-id)
          (recur (.closed? send-ch))))

      (swap! ws-atom assoc chan-id {:session ws-session
                                    :chan send-ch
                                    :metadata {}})

      (put! send-ch (send-msg {:meta {:msg-type :handshake}
                               :payload {:chan-id chan-id}})))))
