(defproject ring.middleware.conditional "0.2.0"
  :description "Adds arbitrary conditions to the Ring middleware stack"
  :url "http://github.com/pjlegato/ring.middleware.conditional"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [ring "1.6.3"]]
  :profiles {:dev {:dependencies [[ring/ring-mock "0.3.2"]]}})
