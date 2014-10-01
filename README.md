# ring.middleware.conditional

Ring middleware that allows conditional execution of other middleware
based on properties of the runtime request.

Convenience methods are provided for common cases such as URL
matching. Suggestions and especially pull requests for additional
convenience methods are welcome!

## Usage

Add `[ring.middleware.conditional "0.1.0"]` to your `project.clj`. `refer` to the functions you want, e.g:

```clojure
  (:require [ring.middleware.conditional :as c :refer  [if-url-starts-with
                                                        if-url-doesnt-start-with
                                                        if-url-matches
                                                        if-url-doesnt-match]])
```

See also the tests and examples directories for more examples.

These examples use the `wrap-with-logger` middleware from
[ring.middleware.logger](https://github.com/pjlegato/ring.middleware.logger)
to do conditional logging of certain requests, but any middleware
should work.

```clojure



;; Log only URLs that start with "/foo":
(def conditional-logging-app
  (-> handler
      (if-url-starts-with "/foo" wrap-with-logger)
       wrap-with-other-stuff))


;; Log only URLs that don't start with "/foo":
(def conditional-logging-app
  (-> handler
      (if-url-doesnt-start-with "/foo" wrap-with-logger)
       wrap-with-other-stuff))


;; Log URLs that match the regexp
(def conditional-logging-app
  (-> handler
      (if-url-matches #"[/0-9]+" wrap-with-logger)
       wrap-with-other-stuff))


;; Log URLs that don't match the regexp
(def conditional-logging-app
  (-> handler
      (if-url-doesnt-match #"[/0-9]+" wrap-with-logger)
       wrap-with-other-stuff))


```

## License

Copyright © 2014 Paul Legato.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
