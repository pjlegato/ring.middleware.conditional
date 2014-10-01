(defproject ring.middleware.conditional "0.1.0"
  :description "Adds arbitrary conditions to the Ring middleware stack"
  :url "http://github.com/pjlegato/ring.middleware.conditional"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.3.1"]]
  :profiles {:dev {:dependencies [[ring-mock "0.1.5"]]}})
