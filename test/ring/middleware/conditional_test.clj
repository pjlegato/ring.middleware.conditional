(ns ring.middleware.conditional-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer [request]]
            [ring.middleware.conditional :as c :refer  [if-url-starts-with
                                                        if-url-doesnt-start-with
                                                        if-url-matches
                                                        if-url-doesnt-match]]))

(defn wrap-with-replacer
  "Changes the query-string of the request to be \"replaced!\" each time it is invoked."
  [handler]
  (fn [request]
    (handler (assoc request :query-string "replaced!"))))

(defn echo-handler
  "Ring handler that returns the query string of the request unaltered."
  [request]
  (:query-string request))


(deftest test-if
  (testing "The conditional handler is executed if and only if the condition is true."
    (let [stack (-> echo-handler
                    ;; Executes wrap-with-replace iff the URI is "do-replace".
                    (c/if #(= "do-replace" (:uri %))
                      wrap-with-replacer))]
      (is (= "bar=123"   (stack (request :get "foo"        {:bar 123}))))
      (is (= "bar=123"   (stack (request :get "bar"        {:bar 123}))))
      (is (= "replaced!" (stack (request :get "do-replace" {:bar 123})))))))

(deftest test-if-url-starts-with
  (testing "The if-url-starts-with convenience method works as advertised."
    (testing "single path"
      (let [stack (-> echo-handler
                      (if-url-starts-with "/do-replace/" wrap-with-replacer))]
        (is (= "bar=123"   (stack (request :get "/foo/bar"        {:bar 123}))))
        (is (= "bar=123"   (stack (request :get "/baz/do-replace" {:bar 123}))))
        (is (= "replaced!" (stack (request :get "/do-replace/baz" {:bar 123}))))))
    (testing "multiple paths"
      (let [stack (-> echo-handler
                     (if-url-starts-with ["/do-replace1/" "/do-replace2/"]
                                         wrap-with-replacer))]
        (is (= "bar=123"   (stack (request :get "/foo/bar"             {:bar 123}))))
        (is (= "replaced!" (stack (request :get "/do-replace1/baz" {:bar 123}))))
        (is (= "replaced!" (stack (request :get "/do-replace2/bar" {:bar 123}))))))))

(deftest test-if-url-doesnt-start-with
  (testing "The if-url-doesnt-start-with convenience method works as advertised."
    (testing "single path"
      (let [stack (-> echo-handler
                      (if-url-doesnt-start-with "/dont-replace/" wrap-with-replacer))]
        (is (= "replaced!" (stack (request :get "/foo/bar"            {:bar 123}))))
        (is (= "replaced!" (stack (request :get "/dont-replace"       {:bar 123}))))
        (is (= "bar=123"   (stack (request :get "/dont-replace/baz"   {:bar 123}))))))
    (testing "multiple paths"
      (let [stack (-> echo-handler
                     (if-url-doesnt-start-with ["/dont-replace1/" "/dont-replace2/"]
                                               wrap-with-replacer))]
        (is (= "replaced!" (stack (request :get "/foo/bar"           {:bar 123}))))
        (is (= "bar=123"   (stack (request :get "/dont-replace1/baz" {:bar 123}))))
        (is (= "bar=123"   (stack (request :get "/dont-replace2/bar" {:bar 123}))))))))

(deftest test-if-url-matches
  (testing "The if-url-matches convenience method works as advertised."
    (let [stack (-> echo-handler
                    (if-url-matches #"[/0-9]+" wrap-with-replacer))]
      (is (= "bar=123"   (stack (request :get "/foo/bar"        {:bar 123}))))
      (is (= "bar=123"   (stack (request :get "/baz/do-replace" {:bar 123}))))
      (is (= "bar=123"   (stack (request :get "/987/baz"        {:bar 123}))))
      (is (= "replaced!" (stack (request :get "/987/654"        {:bar 123})))))))


(deftest test-if-url-doesnt-match
  (testing "The if-url-doesnt-matche convenience method works as advertised."
    (let [stack (-> echo-handler
                    (if-url-doesnt-match #"[/0-9]+" wrap-with-replacer))]
      (is (= "replaced!" (stack (request :get "/foo/bar"        {:bar 123}))))
      (is (= "replaced!" (stack (request :get "/baz/do-replace" {:bar 123}))))
      (is (= "replaced!" (stack (request :get "/987/baz"        {:bar 123}))))
      (is (= "bar=123"   (stack (request :get "/987/654"        {:bar 123})))))))
