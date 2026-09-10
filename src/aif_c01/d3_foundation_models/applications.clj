(ns aif-c01.d3-foundation-models.applications
  (:require [aif-c01.specs :as specs]
            [clojure.spec.alpha :as s]))

(defn describe-rag []
  "Retrieval Augmented Generation: Enhancing LLM responses with external knowledge")

(s/fdef describe-rag
  :args (s/cat)
  :ret ::specs/explanation)

(defn list-model-selection-criteria []
  [:cost :performance :scalability :interpretability])

(s/fdef list-model-selection-criteria
  :args (s/cat)
  :ret ::specs/model-selection-criteria)
