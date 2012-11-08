(ns rpi-challenger.core
  (:require [ring.adapter.jetty :as jetty])
  (:gen-class ))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})

(defn -main
  [& args]
  (jetty/run-jetty handler {:port 3000}))
