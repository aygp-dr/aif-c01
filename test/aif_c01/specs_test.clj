(ns aif-c01.specs-test
  "Generative checks for every pure s/fdef'd fn, plus data-spec sanity.
  Per https://clojure.org/guides/spec (Testing)."
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [aif-c01.core :as core]
            [aif-c01.d0-setup.environment :as d0]
            [aif-c01.d1-fundamentals.basics :as d1]
            [aif-c01.d2-generative-ai.concepts :as d2]
            [aif-c01.d3-foundation-models.applications]
            [aif-c01.d4-responsible-ai.practices]
            [aif-c01.d5-security-compliance.governance]
            [aif-c01.specs :as specs]))

(def ^:private check-opts {:clojure.spec.test.check/opts {:num-tests 50}})

(def ^:private api-nses
  '[aif-c01.core
    aif-c01.d0-setup.environment
    aif-c01.d1-fundamentals.basics
    aif-c01.d2-generative-ai.concepts
    aif-c01.d3-foundation-models.applications
    aif-c01.d4-responsible-ai.practices
    aif-c01.d5-security-compliance.governance])

;; Side-effecting fns: fdef'd for instrumentation, never generatively checked.
(def ^:private side-effecting
  #{`core/-main                       ; prints the overview
    `d0/test-proxy `d0/check-proxy    ; HTTP through localhost:3128
    `d0/check-aws-credentials         ; env, ~/.aws, container/IMDS endpoints
    `d0/check-sagemaker-connection    ; AWS API calls
    `d0/check-bedrock-connection
    `d0/run-all-checks
    `d0/display-check-results         ; prints
    `d0/check-environment})

(defn- checkable []
  (remove side-effecting (stest/enumerate-namespace api-nses)))

(deftest fdefs-hold-under-generative-testing
  (let [results (stest/check (checkable) check-opts)]
    (is (seq results) "expected at least one fdef'd fn to check")
    (doseq [r results]
      (testing (str (:sym r))
        (is (nil? (:failure r))
            (pr-str (stest/abbrev-result r)))))))

(deftest data-specs-generate-and-conform
  (doseq [k [::specs/lookup-key ::specs/ai-ml-terms ::specs/ml-types
             ::specs/gen-ai-concepts ::specs/gen-ai-use-cases
             ::specs/model-selection-criteria ::specs/responsible-ai-features
             ::specs/bias-effects ::specs/aws-security-services
             ::specs/data-governance-strategies ::specs/credentials
             ::specs/check-result ::specs/check-results ::specs/all-checks]]
    (testing (str k)
      (is (every? (fn [[v _]] (s/valid? k v)) (s/exercise k 10))))))

(def ^:private fixture-results
  "Shaped like (d0/check-environment) output on a machine with credentials
  but no proxy (the key pair is the AWS documentation example)."
  {:aws-credentials {:status :success :message "AWS credentials found"
                     :credentials {:aws/access-key-id "AKIAIOSFODNN7EXAMPLE"
                                   :aws/secret-access-key "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"}}
   :sagemaker {:status :success :message "Successfully connected to SageMaker"}
   :bedrock {:status :error :message "Error connecting to Bedrock: timeout"}
   :partyrock (d0/check-partyrock-connection)
   :amazon-q (d0/check-amazon-q-connection)
   :proxy {:status :error :message "Proxy test failed: Connection refused"}})

(deftest real-values-conform
  (testing "glossaries"
    (is (s/valid? ::specs/ai-ml-terms d1/ai-ml-terms))
    (is (s/valid? ::specs/gen-ai-concepts d2/gen-ai-concepts)))
  (testing "an environment-check result"
    (is (s/valid? ::specs/all-checks fixture-results))
    (is (s/valid? ::specs/check-results fixture-results))
    (is (not (s/valid? ::specs/check-result {:status :ok :message "unknown status"}))))
  (testing "display-check-results renders the fetched credentials"
    (let [out (with-out-str (d0/display-check-results fixture-results))]
      (is (str/includes? out "Access Key ID: AKIAI ...")))))
