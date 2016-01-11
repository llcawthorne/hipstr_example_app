(ns hipstr.handler
  (:require [compojure.core :refer [defroutes]]
            [hipstr.routes.home :refer [home-routes]]
            [hipstr.routes.albums :refer [album-routes]]
            [hipstr.routes.test-routes :refer [test-routes]]
            [hipstr.middleware :refer [load-middleware]]
            [hipstr.session-manager :as session-manager]
            [noir.response :refer [redirect]]
            [noir.util.middleware :refer [app-handler]]
            [ring.middleware.defaults :refer [site-defaults]]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]
            [taoensso.timbre.appenders.rolling :as rolling]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [cronj.core :as cronj]
            [migratus.core]))

(defroutes base-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def migratus-config
  {:store :database
   :migration-dir "migrations"
   :migration-table-name "_migrations"
   :db {:classname "org.postgresql.Driver"
        :subprotocol "postgresql"
        :subname "//localhost/postgres"
        :user "hipstr"
        :password "password"}})

(defn migrate-db []
  (timbre/info "checking migrations")
  (try
    (migratus.core/migrate migratus-config)
    (catch Exception e
      (timbre/error "Failed to migrate" e)))
  (timbre/info "finished migrations"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (timbre/set-config!
    [:appenders :rolling]
    (rolling/make-rolling-appender {:min-level :info}))

  (timbre/set-config!
    [:shared-appender-config :rolling :path]
    "logs/hipstr.log")

  (if (env :dev) (parser/cache-off!))
  (migrate-db)
  ;;start the expired session cleanup job
  (cronj/start! session-manager/cleanup-job)
  (timbre/info "\n-=[ hipstr started successfully"
               (when (env :dev) "using the development profile") "]=-"))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "hipstr is shutting down...")
  (cronj/shutdown! session-manager/cleanup-job)
  (timbre/info "shutdown complete!"))

;; timeout sessions after 30 minutes
(def session-defaults
  {:timeout (* 60 30)
   :timeout-response (redirect "/")})

(defn- mk-defaults
       "set to true to enable XSS protection"
       [xss-protection?]
       (-> site-defaults
           (update-in [:session] merge session-defaults)
           (assoc-in [:security :anti-forgery] xss-protection?)))

(def app (app-handler
           ;; add your application routes here
           [home-routes album-routes test-routes base-routes]
           ;; add custom middleware here
           :middleware (load-middleware)
           :ring-defaults (mk-defaults false)
           ;; add access rules here
           :access-rules []
           ;; serialize/deserialize the following data formats
           ;; available formats:
           ;; :json :json-kw :yaml :yaml-kw :edn :yaml-in-html
           :formats [:json-kw :edn :transit-json]))
