(ns rpi-challenger.core.tournament
  (:use [clojure.algo.generic.functor :only [fmap]])
  (:require [rpi-challenger.core.participant :as p]
            [rpi-challenger.core.challenges :as c]))

(defn make-tournament
  []
  {:participants {}
   :challenge-functions []})

(defn serialize
  [tournament]
  (:participants tournament))

(defn deserialize
  [data]
  (assoc (make-tournament) :participants data))

(defn participants
  [tournament]
  (sort-by :id (vals (:participants tournament))))

(defn participant-by-id
  [tournament id]
  (get (:participants tournament) id))

(defn next-participant-id
  [tournament]
  (+ 1 (count (:participants tournament))))

(defn make-participant
  [tournament name url]
  (p/make-participant (next-participant-id tournament) name url))

(defn register-participant
  [tournament participant]
  (assoc-in tournament [:participants (:id participant)] participant))

(defn record-strike
  [tournament participant strike]
  (update-in tournament [:participants (:id participant)] p/record-strike strike))

(defn set-challenge-functions
  [tournament challenge-functions]
  (assoc-in tournament [:challenge-functions ] (sort-by c/price < challenge-functions)))

(defn generate-challenges
  [tournament]
  (map c/generate (:challenge-functions tournament)))

(defn finish-current-round
  [tournament]
  (update-in tournament [:participants ] #(fmap p/finish-current-round %)))
