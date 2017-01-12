(ns spec-swagger.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [com.gfredericks.test.chuck.clojure-test :refer [checking]]
            [clojure.spec :as s]
            [spec-swagger.core :as ss]
            [ring.swagger.validator :as v]))

(deftest swagger-test
  (checking "that generated Swagger specs are valid" 100
    [spec (s/gen ::ss/swagger)]
    (is (nil? (v/validate spec)))))
