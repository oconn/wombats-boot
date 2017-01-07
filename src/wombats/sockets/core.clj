(ns wombats.sockets.core
  (:require [clojure.core.async :refer [put!]]
            [clojure.edn :as edn]))

;; Helpers

(defn parse-raw
  [raw-message]
  (try
    (edn/read-string raw-message)
    (catch Exception e (prn (str "Invalid client message: " raw-message)))))

;; Handlers

(defn socket-error
  [t]
  (prn (str "WS Error " t)))

(defn socket-close
  [num-code reason-text]
  (prn (str "WS Closed: " reason-text)))

(defn new-ws-connection
  [ws-atom]
  (fn [ws-session send-ch]
    (let [conn-id (.hashCode ws-session)]
      (prn (str "Connection " conn-id " establised"))
      (put! send-ch "This will be a text message")
      (swap! ws-atom assoc (keyword conn-id) {:session ws-session
                                              :chan send-ch}))))
