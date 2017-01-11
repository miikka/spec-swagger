(ns spec-swagger.ring
  "Create Ring handlers from Swagger data."
  (:require [clojure.spec :as s]
            [ring.util.http-response :refer [method-not-allowed]]))

(s/def ::request-method #{:get})
(s/def ::parameters map?)
(s/def ::responses map?)
(s/def ::handler (s/fspec :args (s/cat :request map?) :ret map?))

(s/def ::operation (s/keys :req-un [::handler] :opt-un [::parameters ::responses]))
(s/def ::resource (s/map-of ::request-method ::operation))

(s/fdef resource
  :args (s/cat :info ::resource)
  :ret fn?)
(defn resource
  [info]
  (fn [{:keys [request-method] :as request}]
    (let [handler (get-in info [request-method :handler])]
      (if-not handler
        (method-not-allowed (format "No handler for request method %s available."
                                    request-method))
        (handler request)))))
