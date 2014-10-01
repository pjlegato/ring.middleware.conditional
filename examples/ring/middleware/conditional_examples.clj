(ns examples.ring.middleware.conditional-examples
  (:require
   [ring.mock.request :refer [request]]
   [ring.middleware.conditional :as c :refer [if-url-starts-with]]
   [ring.adapter.jetty :refer [run-jetty]]))


(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})


(defn wrap-with-logger
  "Models ring.middleware.logger by printing a log message to STDOUT for
  each request."
  [handler]
  (fn [request]
    (println " ** Logging a request: " request)
    (handler request)))


(defn wrap-with-other-stuff
  "Models a generic Ring middlware that does \"other stuff\" when invoked.
  Actually just prints a message to STDOUT for each request indicating
  that other stuff is being done, and then invokes the next handler."
  [handler]
  (fn [request]
    (println " ** Doing other stuff to request: " request)
    (handler request)))

;;;;;;;;;

;; This is a basic Ring app that logs every request and does other
;; stuff to every request.
(def do-everything-app
  (-> handler
      wrap-with-other-stuff
      wrap-with-logger))

;; examples.ring.middleware.conditional-examples> (do-everything-app (request :get "/foo/bar"))
;;  ** Logging a request:  {:server-port 80, :server-name localhost, :remote-addr localhost, :uri /foo/bar, :query-string nil, :scheme :http, :request-method :get, :headers {host localhost}}
;;  ** Doing other stuff to request:  {:server-port 80, :server-name localhost, :remote-addr localhost, :uri /foo/bar, :query-string nil, :scheme :http, :request-method :get, :headers {host localhost}}
;; {:headers {"Content-Type" "text/html"},
;;  :status 200,
;;  :body "Hello World"}


;;;;;;;;;

;; This app uses ring.middleware.conditional/if to log only requests
;; whose URL begins with "/foo". Other stuff is, however, done to every request.
(def conditional-logging-app
  (-> handler
      (c/if (fn [request]
              (.startsWith (:uri request) "/foo"))
        wrap-with-logger)
      wrap-with-other-stuff))

;; examples.ring.middleware.conditional-examples> (conditional-logging-app (request :get "/foo/bar"))
;;  ** Doing other stuff to request:  {:server-port 80, :server-name localhost, :remote-addr localhost, :uri /foo/bar, :query-string nil, :scheme :http, :request-method :get, :headers {host localhost}}
;;  ** Logging a request:  {:server-port 80, :server-name localhost, :remote-addr localhost, :uri /foo/bar, :query-string nil, :scheme :http, :request-method :get, :headers {host localhost}}
;; {:headers {"Content-Type" "text/html"},
;;  :status 200,
;;  :body "Hello World"}
;;
;;
;; examples.ring.middleware.conditional-examples> (conditional-logging-app (request :get "/baz/bar"))
;;  ** Doing other stuff to request:  {:server-port 80, :server-name localhost, :remote-addr localhost, :uri /baz/bar, :query-string nil, :scheme :http, :request-method :get, :headers {host localhost}}
;; {:headers {"Content-Type" "text/html"},
;;  :status 200,
;;  :body "Hello World"}


;;;;;;;

;; This works like conditional-logging-app, but uses the
;; if-url-starts-with convenience method for cleaner code when making
;; the Ring stack.
;;

(def conditional-logging-app-2
  (-> handler
      (if-url-starts-with "/foo" wrap-with-logger)
      wrap-with-other-stuff))

;; examples.ring.middleware.conditional-examples> (conditional-logging-app-2 (request :get "/foo/bar"))
;;  ** Doing other stuff to request:  {:server-port 80, :server-name localhost, :remote-addr localhost, :uri /foo/bar, :query-string nil, :scheme :http, :request-method :get, :headers {host localhost}}
;;  ** Logging a request:  {:server-port 80, :server-name localhost, :remote-addr localhost, :uri /foo/bar, :query-string nil, :scheme :http, :request-method :get, :headers {host localhost}}
;; {:headers {"Content-Type" "text/html"},
;;  :status 200,
;;  :body "Hello World"}
;;
;; examples.ring.middleware.conditional-examples> (conditional-logging-app-2 (request :get "/baz/bar"))
;;  ** Doing other stuff to request:  {:server-port 80, :server-name localhost, :remote-addr localhost, :uri /baz/bar, :query-string nil, :scheme :http, :request-method :get, :headers {host localhost}}
;; {:headers {"Content-Type" "text/html"},
;;  :status 200,
;;  :body "Hello World"}


