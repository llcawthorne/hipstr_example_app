(ns hipstr.routes.albums
  (:require [compojure.core :refer :all]
            [hipstr.layout :as layout]
            [hipstr.models.album-model :as album]))

(defn recently-added-page
  "Renders out the recently-added page."
  []
  (layout/render "albums/recently-added.html"
                 {:albums (album/get-recently-added)}))

(defn albums-by-artist-page
  "Renders out the recently-added page."
  [artist]
  (layout/render "albums/by-artist.html"
                 {:artist artist
                  :albums (album/get-albums-by-artist {:artist artist})}))

(defroutes album-routes
  (GET "/albums/recently-added" [] (recently-added-page))
  (GET "/albums/:artist" [artist] (albums-by-artist-page artist)))
