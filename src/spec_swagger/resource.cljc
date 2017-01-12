(ns spec-swagger.resource
  (:require [clojure.spec :as s]
            ring.core.spec))

(s/def ::info any?)

(s/fdef resource
  :args (s/cat :info ::info)
  :ret :ring.async/handler)
(defn resource
  [info]
  (fn [request]))

