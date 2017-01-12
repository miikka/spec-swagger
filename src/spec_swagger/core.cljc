(ns spec-swagger.core
  (:require [clojure.spec :as s]
            [clojure.spec.gen :as gen]
            [clojure.string :as string]
            [spec-tools.core :as st]))

(s/def ::external-docs
  (st/coll-spec
    ::external-docs
    {(st/opt :description) string?
     :url string?}))

(s/def ::security-definitions
  (st/coll-spec
    ::security-definitions
    {string? {:type (st/enum "basic" "apiKey" "oauth2")
              (st/opt :description) string?
              (st/opt :name) string?
              (st/opt :in) (st/enum "query" "header")
              (st/opt :flow) (st/enum "implicit" "password" "application" "accessCode")
              (st/opt :authorizationUrl) string?
              (st/opt :tokenUrl) string?
              (st/opt :scopes) {string? string?}}}))

(s/def ::security-requirements
  (st/coll-spec
    ::security-requirements
    {string? [string?]}))

(s/def ::header-type #{"string" "number" "integer" "boolean" "array"})

(s/def ::header-object
  (st/coll-spec
   ::header-object
   {(st/req :type) ::header-type}))

(s/def ::spec (st/coll-spec ::spec {}))

(s/def ::email-type
  (s/with-gen (s/and string? #(re-find #"@" %))
    #(gen/fmap (fn [s1 s2] (str s1 "@" s2)) (gen/tuple (gen/string) (gen/string)))))

(s/def ::path-type
  (s/with-gen (s/and string? #(string/starts-with? % "/"))
    #(gen/fmap (fn [s] (str "/" s)) (gen/string))))

(def response-code (s/or :number (s/int-in 100 600) :default #{:default}))

(s/def ::response
  (st/coll-spec ::response
                {(st/req :description) string?
                 (st/opt :schema) ::spec
                 (st/opt :headers) {string? ::header-object}
                 (st/opt :examples) {string? any?}}))

(s/def ::operation
  (st/coll-spec
    ::operation
    {(st/opt :tags) [string?]
     (st/opt :summary) string?
     (st/opt :description) string?
     (st/opt :externalDocs) ::external-docs
     (st/opt :operationId) string?
     (st/opt :consumes) #{string?}
     (st/opt :produces) #{string?}
     (st/opt :parameters) {:query ::spec
                           :header ::spec
                           :path ::spec
                           :formData ::spec
                           :body ::spec}
     (st/req :responses) (s/map-of response-code ::response :min-count 1)
     (st/opt :schemes) (st/set-of #{"http", "https", "ws", "wss"})
     (st/opt :deprecated) boolean?
     (st/opt :security) [::security-requirements]}))

(s/def ::swagger
  (st/coll-spec
    ::swagger
    {:swagger (st/eq "2.0")
     :info {:title string?
            (st/opt :description) string?
            (st/opt :termsOfService) string?
            (st/opt :contact) {(st/opt :name) string?
                               (st/opt :url) string?
                               (st/opt :email) ::email-type}
            (st/opt :license) {:name string?
                               (st/opt :url) string?}
            :version string?}
     :paths (s/map-of ::path-type (s/map-of #{:get :put :post :delete :options :head :patch}
                                            ::operation))
     (st/opt :host) string?
     (st/opt :basePath) ::path-type
     (st/opt :schemes) (st/set-of #{"http", "https", "ws", "wss"})
     (st/opt :consumes) #{string?}
     (st/opt :produces) #{string?}
     ;(st/opt :definitions) map?
     ;(st/opt :parameters) map?
     ;(st/opt :responses) map?
     (st/opt :securityDefinitions) ::security-definitions
     (st/opt :security) ::security-requirements
     (st/opt :tags) [{:name string?
                      (st/opt :description) string?
                      (st/opt :externalDocs) ::external-docs}]
     (st/opt :externalDocs) ::external-docs}))

(comment
  (clojure.pprint/pprint
    (last (map first (s/exercise ::swagger 10)))))

