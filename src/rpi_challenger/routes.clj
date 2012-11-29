(ns rpi-challenger.routes
  (:use ring.util.response
        compojure.core
        [ring.middleware.params :only [wrap-params]])
  (:require [rpi-challenger.controller :as ctrl]
            [rpi-challenger.views :as views]
            [compojure.route :as route]
            [net.cgrand.enlive-html :as html]
            [clojure.string :as string]))

(defn using-template
  [template & args]
  (response (apply str (apply template args))))

(defn handle-register-form
  [{name "name" url "url"}]
  (if (and
        (not (string/blank? name))
        (not (string/blank? url)))
    (do
      (ctrl/register name url)
      (redirect "/"))
    (redirect "/?message=Registration failed")))

(defn handle-hello-world
  [question]
  (cond
    (= question "ping") "pong"
    (= question "Say hello to World") "Hello World"
    :else (str "Don't understand: " question)))

(defn make-routes
  [app]
  (->
    (routes
      (GET "/" [] (using-template views/tournament-overview-page (ctrl/get-participants)))
      (GET "/participant-:id" [id] (using-template views/participant-details-page (ctrl/get-participant-by-id (Integer/parseInt id))))
      (POST "/register" {params :params} (handle-register-form params))
      (POST "/hello-world" {body :body} (handle-hello-world (slurp body)))
      (route/resources "/")
      (route/not-found "Page Not Found"))
    (wrap-params)))
