(ns rpi-challenger.core.participant-test
  (:use clojure.test)
  (:require [rpi-challenger.core.participant :as p]
            [rpi-challenger.core.strike :as s]))

(deftest participant-test
  (binding [p/*recent-strikes-limit* 4
            p/*recent-failures-limit* 3
            s/hit? #(= % :hit)
            s/price (fn [_] 10)]
    (let [participant (p/make-participant 123 "Somebody" "http://somewhere")]

      (testing "Recent strikes list shows passes and failures"
        (let [participant
              (-> participant
                  (p/record-strike :miss1)
                  (p/record-strike :hit)
                  (p/record-strike :miss2))]
          (is (= [:miss1 :hit :miss2]
                 (p/recent-strikes participant)))))

      (testing "Recent failures list shows only failures"
        (let [participant
              (-> participant
                  (p/record-strike :miss1)
                  (p/record-strike :hit)
                  (p/record-strike :miss2))]
          (is (= [:miss1 :miss2]
                 (p/recent-failures participant)))))

      (testing "Recent strikes list forgets oldest strikes when it gets over the limit"
        (let [participant
              (-> participant
                  (p/record-strike :miss1)
                  (p/record-strike :miss2)
                  (p/record-strike :hit)
                  (p/record-strike :miss3)
                  (p/record-strike :miss4))]
          (is (= [:miss2 :hit :miss3 :miss4]
                 (p/recent-strikes participant)))))

      (testing "Recent failures list forgets the oldest failures when it gets over the limit"
        (let [participant
              (-> participant
                  (p/record-strike :miss1)
                  (p/record-strike :miss2)
                  (p/record-strike :miss3)
                  (p/record-strike :miss4))]
          (is (= [:miss2 :miss3 :miss4]
                 (p/recent-failures participant)))))

      (testing "Finished rounds list shows the worst failures and significant hits for each past round"
        (let [participant
              (-> participant
                  (p/record-strike :miss1)
                  (p/finish-current-round)
                  (p/record-strike :miss2)
                  (p/finish-current-round))
              finished-rounds (p/finished-rounds participant)]
          (is (= 2 (count finished-rounds)))
          (is (= [:miss1 :miss2] (map :worst-failure finished-rounds)))))

      (testing "Reports the current and max velocity based on the last finished round"
        (let [participant
              (-> participant
                  (p/record-strike :hit)
                  (p/finish-current-round)
                  (p/record-strike :hit)
                  (p/finish-current-round))
              finished-rounds (p/finished-rounds participant)]
          (is (= 2 (p/current-velocity participant)))
          (is (= 10 (p/max-velocity participant))))))))
