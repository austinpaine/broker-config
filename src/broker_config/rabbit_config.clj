(ns broker-config.rabbit-config
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io])
  (:gen-class))


(defn make-binding [dest]
  {:source "my-exchange",
   :vhost "/main",
   :destination dest,
   :destination_type "queue",
   :routing_key "",
   :arguments {}})

(defn make-queue [q]
  {:name q,
   :vhost "/main",
   :durable true,
   :auto_delete false,
   :arguments {:x-queue-type "classic"}})

(defn main-json [exchange topics]
  {:rabbit_version "3.8.2",
   :rabbitmq_version "3.8.2"

   :users
       [{:name "guest",
         :password_hash
               "DgIiBwUFbwyIR+1j15c0HknqmC+VZPw7YZUMTWmLXhCyD45P",
         :hashing_algorithm
               "rabbit_password_hashing_sha256",
         :tags "administrator"}],

   :vhosts [{:name "/main"}],

   :permissions
       [{:user "guest",
         :vhost "/main",
         :configure ".*",
         :write ".*",
         :read ".*"}],

       :global_parameters
       [{:name "cluster_name",
         :value "rabbit@2faa68ed4849"}],

   :topic_permissions [],
   :parameters [],
   :policies [],

   :queues
       (doall (mapv make-queue topics)),

   :exchanges
        [{:name exchange,
          :vhost "/main",
          :type "direct",
          :durable true,
          :auto_delete false,
          :internal false,
          :arguments {}}],

   :bindings
        (doall (mapv make-binding topics))})


;; Using the io writer was much prefered to spitting the text.
;; Attempting to convert to json string and convert back was a nightmare
;; so using the json/write to convert and write edn-json in one step was perfect.
;; https://github.com/clojure/data.json
;; https://clojure.github.io/data.json/

(defn make-rabbit-config [filename exchange topics]
  (if (.exists (io/as-file filename))
    (io/delete-file filename))
  (with-open [wrtr (io/writer filename)]
    (json/write (main-json exchange topics) wrtr :escape-slash false)))


(comment
  (def filename "projects/make-config/resources/testRabbit.txt")
  (def topics ["user-requests" "wireless-requests" "request-error"])

  (make-rabbit-config "projects/make-config/resources/testRabbit.txt" "my-exchange" ["user-requests" "wireless-requests" "request-error"])

  (make-queue "why")

  (def json {:rabbit_version "3.8.2"
                   :rabbitmq_version "3.8.2"
                   :users [{:name "guest"
                            :password_hash "DgIiBwUFbwyIR+1j15c0HknqmC+VZPw7YZUMTWmLXhCyD45P"
                            :hashing_algorithm "rabbit_password_hashing_sha256"
                            :tags "administrator"}]
                   :vhosts [{:name "/main"}]
                   :permissions [{:user "guest"
                                  :vhost "/main"
                                  :configure ".*"
                                  :write ".*",
                                  :read ".*"}]})


  (with-open [wrtr (io/writer "projects/make-config/resources/testRabbit.txt")]
    (json/write json wrtr :escape-slash false))


  ())