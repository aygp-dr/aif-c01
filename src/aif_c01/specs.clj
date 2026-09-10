(ns aif-c01.specs
  "Data specs for aif-c01 (https://clojure.org/guides/spec).
  Function specs (s/fdef) live next to each defn in the domain namespaces."
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as str]))

;; --- Shared ---

(s/def ::non-blank-string
  (s/with-gen (s/and string? (complement str/blank?))
    #(gen/not-empty (gen/string-alphanumeric))))

;; A glossary explanation, e.g. "Machine Learning".
(s/def ::explanation ::non-blank-string)

;; Any keyword can be looked up; generate a mix of real glossary keys and misses.
(s/def ::lookup-key
  (s/with-gen keyword?
    #(gen/one-of [(gen/elements [:ai :ml :dl :prompt-engineering :foundation-model
                                 :embeddings :rag])
                  (gen/keyword)])))

;; --- D1: Fundamentals of AI and ML ---

(s/def ::ai-term #{:ai :ml :dl})
(s/def ::ai-ml-terms (s/map-of ::ai-term ::explanation :min-count 1))
(s/def ::ml-type #{:supervised :unsupervised :reinforcement})
(s/def ::ml-types (s/coll-of ::ml-type :kind vector? :distinct true :min-count 1))

;; --- D2: Fundamentals of Generative AI ---

(s/def ::gen-ai-concept #{:prompt-engineering :foundation-model})
(s/def ::gen-ai-concepts (s/map-of ::gen-ai-concept ::explanation :min-count 1))
(s/def ::gen-ai-use-case #{:image-generation :text-generation :code-generation})
(s/def ::gen-ai-use-cases
  (s/coll-of ::gen-ai-use-case :kind vector? :distinct true :min-count 1))

;; --- D3: Applications of Foundation Models ---

(s/def ::model-selection-criterion #{:cost :performance :scalability :interpretability})
(s/def ::model-selection-criteria
  (s/coll-of ::model-selection-criterion :kind vector? :distinct true :min-count 1))

;; --- D4: Guidelines for Responsible AI ---

(s/def ::responsible-ai-feature #{:fairness :accountability :transparency :ethics})
(s/def ::responsible-ai-features
  (s/coll-of ::responsible-ai-feature :kind vector? :distinct true :min-count 1))
(s/def ::bias-effect #{:demographic :performance})
(s/def ::bias-effects (s/map-of ::bias-effect ::explanation :min-count 1))

;; --- D5: Security, Compliance, and Governance ---

(s/def ::aws-security-service
  (s/with-gen ::non-blank-string
    #(gen/elements ["IAM" "KMS" "CloudTrail" "Macie" "GuardDuty" "AWS Config"
                    "Inspector" "Artifact" "Audit Manager" "Secrets Manager"])))
(s/def ::aws-security-services
  (s/coll-of ::aws-security-service :kind vector? :distinct true :min-count 1))
(s/def ::data-governance-strategy
  #{:data-classification :access-control :encryption :auditing})
(s/def ::data-governance-strategies
  (s/coll-of ::data-governance-strategy :kind vector? :distinct true :min-count 1))

;; --- D0: environment checks ---

;; The keys cognitect.aws.credentials/fetch returns. AWS access key ids are
;; 16-128 chars (display-check-results prints the first 5).
(defn- gen-key-id []
  (gen/fmap (fn [cs] (str "AKIA" (apply str cs)))
            (gen/vector (gen/elements "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567") 16)))
(s/def :aws/access-key-id
  (s/with-gen (s/and string? #(<= 16 (count %) 128)) gen-key-id))
(s/def :aws/secret-access-key ::non-blank-string)
(s/def :aws/session-token ::non-blank-string)
(s/def ::credentials
  (s/keys :req [:aws/access-key-id :aws/secret-access-key]
          :opt [:aws/session-token]))

(s/def ::status #{:success :error :info})
(s/def ::message ::non-blank-string)
(s/def ::body (s/nilable string?))
(s/def ::check-result
  (s/keys :req-un [::status ::message] :opt-un [::body ::credentials]))
(s/def ::info-result (s/and ::check-result #(= :info (:status %))))

(s/def ::service #{:aws-credentials :sagemaker :bedrock :partyrock :amazon-q :proxy})
(s/def ::check-results (s/map-of ::service ::check-result :gen-max 6))

;; run-all-checks / check-environment return one result per service.
(s/def ::aws-credentials ::check-result)
(s/def ::sagemaker ::check-result)
(s/def ::bedrock ::check-result)
(s/def ::partyrock ::info-result)
(s/def ::amazon-q ::info-result)
(s/def ::proxy ::check-result)
(s/def ::all-checks
  (s/keys :req-un [::aws-credentials ::sagemaker ::bedrock ::partyrock ::amazon-q]
          :opt-un [::proxy]))
