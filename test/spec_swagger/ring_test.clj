(ns spec-swagger.ring-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [clojure.spec.test :as stest]
            [com.gfredericks.test.chuck.clojure-test :refer [checking]]
            [juxt.iota :refer [given]]
            [ring.mock.request :as mock]
            [ring.util.http-response :refer [ok]]
            [spec-swagger.ring :refer [resource] :as r]
            [clojure.spec :as s]))

(defn instrument-ns [a-ns f]
  (stest/instrument)
  (f)
  (stest/unstrument))

(use-fixtures :once (partial instrument-ns *ns*))

(def my-resource
  {:get {:handler (fn [request] (ok "okay"))}})

(deftest test-resource
  (let [handler (resource my-resource)]
    (given (handler (mock/request :get "/"))
      :status := 200
      :body   := "okay")
    (given (handler (mock/request :post "/"))
      :status := 405)))

(deftest check-specs
  (-> (stest/enumerate-namespace 'spec-swagger.ring-test)
      stest/check))

#_(deftest check-generative
  (checking "that resource generates working handlers" 10
    [a-resource (s/gen :spec-swagger.ring/resource)]
    ((resource a-resource) (mock/request :post "/"))))
