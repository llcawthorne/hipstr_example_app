(ns hipstr.routes.test-routes
  (:require [compojure.core :refer :all]))

(defn render-request-val [request-map & [request-key]]
  "Simply returns the value of request-key in request-map,
  if request-key is provided; otherwise return the request-map.
  If request-key is provided but not present, returns a message."
  (str (if request-key
         (if-let [result ((keyword request-key) request-map)]
           result
           (str request-key " is not a valid key."))
       request-map)))

(defroutes test-routes
  (POST "/req" request (render-request-val request))
  ;; try http://localhost:3000/req/key/server-port
  (GET "/req/key/:key" [key :as request] (render-request-val request key))
  ;; the next one works for http://localhost:3000/req/something?more=other
  (GET "/req/:val" [val more & others :as request] 
       (str val "<br />" more "<br />" others "<br /><br />" request))
  (ANY "/reqmap" request (str request)))
