(ns aif-c01.d2-generative-ai.concepts
  (:require [aif-c01.specs :as specs]
            [clojure.spec.alpha :as s]))

(def gen-ai-concepts
  {:prompt-engineering "Crafting effective inputs for AI models"
   :foundation-model "Large, general-purpose AI model"})

(defn explain-gen-ai-concept [concept]
  (get gen-ai-concepts concept "Concept not found"))

(s/fdef explain-gen-ai-concept
  :args (s/cat :concept ::specs/lookup-key)
  :ret ::specs/explanation
  :fn (fn [{{:keys [concept]} :args ret :ret}]
        (if (contains? gen-ai-concepts concept)
          (= ret (gen-ai-concepts concept))
          (= ret "Concept not found"))))

(defn list-gen-ai-use-cases []
  [:image-generation :text-generation :code-generation])

(s/fdef list-gen-ai-use-cases
  :args (s/cat)
  :ret ::specs/gen-ai-use-cases)
