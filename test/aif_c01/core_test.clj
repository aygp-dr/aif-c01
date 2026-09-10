(ns aif-c01.core-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [aif-c01.core :as core]))

(deftest main-prints-domain-overview
  (testing "-main prints an overview of all five exam domains"
    (let [out (with-out-str (core/-main))]
      (is (str/includes? out "Exam Prep Overview"))
      (doseq [domain ["D1:" "D2:" "D3:" "D4:" "D5:"]]
        (is (str/includes? out domain)))
      (testing "every lookup hits a real entry"
        (is (not (str/includes? out "not found")))))))
