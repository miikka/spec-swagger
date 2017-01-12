(ns spec-swagger.resource-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [clojure.spec :as s]
   [clojure.spec.test :as stest]
   [ring.mock.request :as mock]
   [ring.util.http-response :refer [ok]]
   [spec-swagger.resource :refer [resource]]))

(stest/instrument)

(def my-resource
  {:get {:handler (fn [request] (ok "okay"))}})

(deftest test-resource
  (let [handler (resource my-resource)]
    (is (= (handler (mock/request :get "/")))
        {:status 200
         :body "okay"})))
