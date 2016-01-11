(ns hipstr.routes.home
  (:require [compojure.core :refer :all]
            [hipstr.layout :as layout]
            [hipstr.util :as util]
            [ring.util.response :as response]
            [hipstr.validators.user-validator :as v]
            [hipstr.models.user-model :as u]))

(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")
                 :person {:first-name "Lewis" 
                          :last-name "Cawthorne"
                          :favourite-animal "Panther"}}))

(defn about-page []
  (layout/render "about.html"))

(defn signup-page-submit [user]
  (let [errors (v/validate-signup user)]
      (if (empty? errors)
        (do
          (u/add-user! user)
          (response/redirect "/signup-success"))
        (layout/render "signup.html"
                       (assoc user :errors errors)))))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/signup" []
    (layout/render "signup.html"))
  (POST "/signup" [& form] (signup-page-submit form))
  (GET "/signup-success" [] "Success!")
  (GET "/login" [] "Hey there, welcome to the login page!")
  (GET "/about" [] (about-page)))
