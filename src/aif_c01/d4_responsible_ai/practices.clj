(ns aif-c01.d4-responsible-ai.practices
  (:require [aif-c01.specs :as specs]
            [clojure.spec.alpha :as s]))

(defn list-responsible-ai-features []
  [:fairness :accountability :transparency :ethics])

(s/fdef list-responsible-ai-features
  :args (s/cat)
  :ret ::specs/responsible-ai-features)

(defn describe-bias-effects []
  {:demographic "Impact on specific groups"
   :performance "Overall model accuracy issues"})

(s/fdef describe-bias-effects
  :args (s/cat)
  :ret ::specs/bias-effects)
