(ns broker-config.rabbit-config
  (:require [clojure.data.json :as json]
            [clojure.java.io :as jio])
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



(defn make-rabbit-config [filename exchange topics]
  (with-open [wrtr (jio/writer filename)]
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


  (with-open [wrtr (jio/writer "projects/make-config/resources/testRabbit.txt")]
    (json/write json wrtr :escape-slash false))


  ())


(comment
  ;; saving this giant json string for reference because im NOT re-formatting it again if I mess up
  ;; https://tools.knowledgewalls.com/jsontostring


  "{\"rabbit_version\":\"3.8.2\",
  \"rabbitmq_version\":\"3.8.2\",
  \"users\":[{\"name\":\"guest\",
  \"password_hash\":\"DgIiBwUFbwyIR+1j15c0HknqmC+VZPw7YZUMTWmLXhCyD45P\",
  \"hashing_algorithm\":\"rabbit_password_hashing_sha256\",
  \"tags\":\"administrator\"}],
  \"vhosts\":[{\"name\":\"/main\"}],
  \"permissions\":
    [{\"user\":\"guest\",
    \"vhost\":\"/main\",
    \"configure\":\".*\",
    \"write\":\".*\",
    \"read\":\".*\"}],
  \"topic_permissions\":[],
  \"parameters\":[],
  \"global_parameters\":
    [{\"name\":\"cluster_name\",
    \"value\":\"rabbit@2faa68ed4849\"}],
  \"policies\":[],

  \"queues\":
  [

  {\"name\":\"person.queue\",
  \"vhost\":\"/main\",
  \"durable\":true,
  \"auto_delete\":false,
  \"arguments\":{\"x-queue-type\":\"classic\"}},

  {\"name\":\"some.queue\",
  \"vhost\":\"/main\",
  \"durable\":true,
  \"auto_delete\":false,
  \"arguments\":{\"x-queue-type\":\"classic\"}},

  {\"name\":\"message.queue\",
  \"vhost\":\"/main\",
  \"durable\":true,
  \"auto_delete\":false,
  \"arguments\":{\"x-queue-type\":\"classic\"}},

  {\"name\":\"wireless-request\",
  \"vhost\":\"/main\",
  \"durable\":true,
  \"auto_delete\":false,
  \"arguments\":{\"x-queue-type\":\"classic\"}},

  {\"name\":\"approved-request\",
  \"vhost\":\"/main\",
  \"durable\":true,
  \"auto_delete\":false,
  \"arguments\":{\"x-queue-type\":\"classic\"}},

  {\"name\":\"unauthorized-request\",
  \"vhost\":\"/main\",
  \"durable\":true,
  \"auto_delete\":false,
  \"arguments\":{\"x-queue-type\":\"classic\"}},

  {\"name\":\"unauthenticated-request\",
  \"vhost\":\"/main\",
  \"durable\":true,
  \"auto_delete\":false,
  \"arguments\":{\"x-queue-type\":\"classic\"}},

  {\"name\":\"request-error\",
  \"vhost\":\"/main\",
  \"durable\":true,
  \"auto_delete\":false,
  \"arguments\":{\"x-queue-type\":\"classic\"}}

  ],

  \"exchanges\":
  [

  {\"name\":\"my-exchange\",
  \"vhost\":\"/main\",
  \"type\":\"direct\",
  \"durable\":true,
  \"auto_delete\":false,
  \"internal\":false,
  \"arguments\":{}},

  {\"name\":\"pb-exchange\",
  \"vhost\":\"/main\",
  \"type\":\"direct\",
  \"durable\":true,
  \"auto_delete\":false,
  \"internal\":false,
  \"arguments\":{}}],

  \"bindings\":

  [{\"source\":\"my-exchange\",
  \"vhost\":\"/main\",
  \"destination\":\"some.queue\",
  \"destination_type\":\"queue\",
  \"routing_key\":\"\",
  \"arguments\":{}},

  {\"source\":\"pb-exchange\",
  \"vhost\":\"/main\",
  \"destination\":\"message.queue\",
  \"destination_type\":\"queue\",
  \"routing_key\":\"\",
  \"arguments\":{}},

  {\"source\":\"pb-exchange\",
  \"vhost\":\"/main\",
  \"destination\":\"person.queue\",
  \"destination_type\":\"queue\",
  \"routing_key\":\"\",
  \"arguments\":{}},

  {\"source\":\"my-exchange\",
  \"vhost\":\"/main\",
  \"destination\":\"wireless-request\",
  \"destination_type\":\"queue\",
  \"routing_key\":\"\",
  \"arguments\":{}},

  {\"source\":\"my-exchange\",
  \"vhost\":\"/main\",
  \"destination\":\"approved-request\",
  \"destination_type\":\"queue\",
  \"routing_key\":\"\",
  \"arguments\":{}},

  {\"source\":\"my-exchange\",
  \"vhost\":\"/main\",
  \"destination\":\"unauthorized-request\",
  \"destination_type\":\"queue\",
  \"routing_key\":\"\",
  \"arguments\":{}},

  {\"source\":\"my-exchange\",
  \"vhost\":\"/main\",
  \"destination\":\"unauthenticated-request\",
  \"destination_type\":\"queue\",
  \"routing_key\":\"\",
  \"arguments\":{}},

  {\"source\":\"my-exchange\",
  \"vhost\":\"/main\",
  \"destination\":\"request-error\",
  \"destination_type\":\"queue\",
  \"routing_key\":\"\",
  \"arguments\":{}}

  ]}"




  ())