(ns aif-c01.d5-security-compliance.governance
  (:require [aif-c01.specs :as specs]
            [clojure.spec.alpha :as s]))

(defn list-aws-security-services []
  ["IAM" "KMS" "CloudTrail"])

(s/fdef list-aws-security-services
  :args (s/cat)
  :ret ::specs/aws-security-services)

(defn describe-data-governance-strategies []
  [:data-classification :access-control :encryption :auditing])

(s/fdef describe-data-governance-strategies
  :args (s/cat)
  :ret ::specs/data-governance-strategies)
