(ns user
  "REPL entry point (clj -M:dev). Loads the project and instruments every
  s/fdef'd fn so bad calls fail fast with explain-data.
  clj -M:dev:rebel starts the rebel-readline REPL below (was `lein rebel`)."
  (:require [rebel-readline.core :as rebel]
            [rebel-readline.clojure.line-reader :as clj-line-reader]
            [rebel-readline.clojure.service.local :as clj-service]
            [rebel-readline.clojure.main :as rebel-main]
            [clojure.main :as main]
            [clojure.spec.test.alpha :as stest]
            [portal.api :as p]
            [cognitect.aws.client.api :as aws]
            [aif-c01.core]
            [aif-c01.d0-setup.environment]
            [aif-c01.d1-fundamentals.basics]
            [aif-c01.d2-generative-ai.concepts]
            [aif-c01.d3-foundation-models.applications]
            [aif-c01.d4-responsible-ai.practices]
            [aif-c01.d5-security-compliance.governance]))

(stest/instrument)

(defn help []
  (println "Available commands:")
  (println "  (help)    - Show this help message")
  (println "  (portal)  - Open a new Portal instance")
  (println "  (tap> x)  - Send data to Portal")
  (println "  (doc x)   - Show documentation for x")
  (println "  (source x) - Show source code for x")
  (println "  (aws-info) - Show current AWS configuration"))

(defn portal []
  (let [p (p/open)]
    (add-tap #'p/submit)
    (println "Portal opened. Use (tap> x) to send data to Portal.")
    p))

(defn aws-info []
  (println "Current AWS configuration:")
  (println "Region:" (System/getProperty "aws.region"))
  (println "Endpoint override:" (System/getenv "endpoint-override"))
  (println "AWS Profile:" (or (System/getenv "AWS_PROFILE") "default")))

(defn configure-aws []
  (when-let [endpoint (System/getenv "endpoint-override")]
    (let [client-opts {:api :sagemaker :endpoint endpoint}]
      (aws/client client-opts)
      (println "AWS client configured with custom endpoint:" endpoint))))

(defn rebel-repl []
  (configure-aws)
  (rebel/with-line-reader
    (clj-line-reader/create
     (clj-service/create))
    (main/repl
     :prompt (fn []) ; prompt is handled by line-reader
     :read (rebel-main/create-repl-read)
     :init #(do
              (println "Welcome to the AIF-C01 REPL with rebel-readline!")
              (println "Type (help) for available commands.")
              (aws-info)
              (portal)))))

(defn -main [& _args]
  (rebel-repl))
