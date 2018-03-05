(ns discogs-wrapper.core
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.string :as str])
  (:import (com.google.common.net UrlEscapers)))

(def discogs-base-uri "https://api.discogs.com")

(defn url-encode-path
  [base-uri path-elements]
  (let [path-esc (UrlEscapers/urlPathSegmentEscaper)]
    (str base-uri
         (when-not (.endsWith base-uri "/")
           "/")
         (str/join "/"
                   (map #(.escape path-esc %)
                        path-elements)))))

(defn make-http-request [{:keys [::verb-fn
                                 ::path-elements
                                 ::qs-params
                                 ::body
                                 ::encoder]
                          :or {::encoder json/encode
                               ::body {}}}]
  (let [encoded-body (encoder body)]
    (-> (verb-fn (url-encode-path discogs-base-uri
                                  path-elements)
                 {:accept :json
                  :content-type "application/json; charset=utf-8"
                  :throw-entire-message? true
                  :query-params qs-params
                  :body encoded-body})
        :body
        json/decode)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;                              USERS ENDPOINTS                               ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-identity [{:keys [token]}]
  {:pre [(not-empty token)]}
  (make-http-request
    {::verb-fn http/get
     ::path-elements ["oauth" "identity"]
     ::qs-params {:token token}}))

(defn get-profile [{:keys [username
                           token]}]
  {:pre [(not-empty username)]}
  (make-http-request
    {::verb-fn http/get
     ::path-elements ["users" username]
     ::qs-params {:token token}}))

(defn get-wantlist [{:keys [username
                            token
                            page]}]
  {:pre [(not-empty username)
         (not-empty token)]}
  "Optional page, starts at 0 if not provided"
  (make-http-request
    {::verb-fn http/get
     ::path-elements ["users" username "wants"]
     ::qs-params {:token token
                  :page page}}))

(defn update-profile [{:keys [username
                              token
                              name
                              home-page
                              location
                              profile
                              curr-abbr]}]
  {:pre [(not-empty username)
         (not-empty token)]}
  (make-http-request
    {::verb-fn http/post
     ::path-elements ["users" username]
     ::qs-params {:token token}
     ::body {:name name
             :home_page home-page
             :location location
             :profile profile
             :curr_abbr curr-abbr}}))

(defn get-submissions [{:keys [username
                               token]}]
  {:pre [(not-empty username)]}
  (make-http-request
    {::verb-fn http/get
     ::path-elements ["users" username "submissions"]
     ::qs-params {:token token}}))

(defn get-contributions [{:keys [username
                                 token
                                 sort
                                 sort-order
                                 page]}]
  {:pre [(not-empty username)]}
  (make-http-request
    {::verb-fn http/get
     ::path-elements ["users" username "contributions"]
     ::qs-params {:token token
                  :sort sort
                  :sort_order sort-order
                  :page page}}))

(defn get-all-collection-folders [{:keys [username
                                          token]}]
  {:pre [(not-empty username)]}
  (make-http-request
    {::verb-fn http/get
     ::path-elements ["users" username "collection" "folders"]
     ::qs-params {:token token}}))

(defn add-collection-folder [{:keys [username
                                     token
                                     folder-name]}]
  {:pre [(not-empty username)
         (not-empty token)]}
  (make-http-request
    {::verb-fn http/post
     ::path-elements ["users" username "collection" "folders"]
     ::qs-params {:token token}
     ::body {:name folder-name}}))

(defn get-collection-folder [{:keys [username
                                     token
                                     folder-id]}]
  {:pre [(not-empty username)
         (or (not-empty token)
             (= folder-id "0"))]}
  (make-http-request
    {::verb-fn http/get
     ::path-elements ["users" username "collection" "folders" folder-id]
     ::qs-params {:token token}}))

(defn update-collection-folder [{:keys [username
                                        token
                                        folder-id
                                        folder-name]}]
  {:pre [(not-empty username)
         (not-empty token)]}
  (make-http-request
    {::verb-fn http/post
     ::path-elements ["users" username "collection" "folders" folder-id]
     ::qs-params {:token token}
     ::body {:name folder-name}}))
