(ns aif-c01.d1-fundamentals.basics
  (:require [aif-c01.specs :as specs]
            [clojure.spec.alpha :as s]))

(def ai-ml-terms
  {:ai "Artificial Intelligence"
   :ml "Machine Learning"
   :dl "Deep Learning"})

(defn explain-ai-term [term]
  (get ai-ml-terms term "Term not found"))

(s/fdef explain-ai-term
  :args (s/cat :term ::specs/lookup-key)
  :ret ::specs/explanation
  :fn (fn [{{:keys [term]} :args ret :ret}]
        (if (contains? ai-ml-terms term)
          (= ret (ai-ml-terms term))
          (= ret "Term not found"))))

(defn list-ml-types []
  [:supervised :unsupervised :reinforcement])

(s/fdef list-ml-types
  :args (s/cat)
  :ret ::specs/ml-types)
