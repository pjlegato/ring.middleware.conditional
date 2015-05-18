(ns ring.middleware.conditional
  "Conditional Ring middleware, which allows conditional execution
   of Ring middleware paths. Includes convenience methods for common cases.")


(defn if
  "Basic conditional Ring middleware.
   For each request, invokes (pred request).

   If the result is truthy, the request is passed to the 'middleware' handler.
   Otherwise, ignores 'middleware' and continues down the stack.

   For example:
    (def app (-> base-handler
                (ring.middleware.conditional/if some-fn? wrap-with-logging)
                 wrap-with-stuff))

    will always run wrap-with-stuff's handler and base-handler, but will
    skip wrap-with-logging when (some-fn? request) is true."
  [f pred middleware]
  (let [wrapped (middleware f)]
    (fn [request]
      (if (pred request)
        (wrapped request)
        (f request)))))

(defn if-url-starts-with
  "Convenience method that invokes the given Ring middleware if the
  URI in the request starts with the provided literal string. Takes a
  single path or a collection of paths as a fn argument"
  [f strs middleware]
  (ring.middleware.conditional/if f
    (fn [request] (some #(.startsWith ^String (:uri request) %)
                        (if (coll? strs) strs [strs])))
    middleware))

(defn if-url-doesnt-start-with
  "Convenience method that invokes the given Ring middleware if the
  URI in the request does NOT start with the provided literal string. Takes a
  single path or a collection of paths as a fn argument"
  [f strs middleware]
  (ring.middleware.conditional/if f
    (fn [request]
      (not (some #(.startsWith ^String  (:uri request) %)
                 (if (coll? strs) strs [strs]))))
    middleware))


(defn if-url-matches
  "Convenience method that invokes the given Ring middleware if the
  URI in the request matches the provided regex."
  [f regex middleware]
  (ring.middleware.conditional/if f
    #(re-matches regex (:uri %))
    middleware))


(defn if-url-doesnt-match
  "Convenience method that invokes the given Ring middleware if the
  URI in the request does NOT match the provided regex."
  [f regex middleware]
  (ring.middleware.conditional/if f
    #(not (re-matches regex (:uri %)))
    middleware))
