(ns broker-config.make-config
  (:require [clojure.tools.cli :as cli]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [broker-config.rabbit-config :as rc])
  (:gen-class))

(defn io-slurp-string
  "Takes in a filename (or path) as a string and
  slurps its contents into edn"
  [file]
  (if (.exists (io/as-file file))
    (->> file
         io/resource
         slurp
         edn/read-string)
    (prn "Failed to find resource: " file)))

(defn append-to-file-amqp
  [filename s]
  (if (some? s)
    (spit filename (str "qpid-config add queue " s "\n") :append true)))



(defn make-amqp-config [filename exchange topics]
  (if (.exists (io/as-file filename))
    (io/delete-file filename))
  (do (spit filename (str
                       "#!/bin/bash \n"
                       "nohup /usr/sbin/qpidd & \n"
                       "#Gives qpid time to startup preventing race conditions \n"
                       "sleep 10 \n"
                       "qpid-config add exchange direct " exchange " -a localhost:5672 \n")
            :append true)
      (doall (map (partial append-to-file-amqp filename) topics))
      (spit filename
            (str "qpid-config bind " exchange " user-requests user-requests.binding" "\n"
                 "tail -f nohup.out") :append true)))




(defn -main [& args]
  (if (seq args)
    (doseq [arg args]
      ;(prn arg)

      (let [channel (io-slurp-string (str "../rocky-road/channels/" arg ".edn"))
            exchange (:exchange-name channel)
            events (:events channel)
            topics (mapv #(:channel (io-slurp-string (str "../rocky-road/events/events/" % ".edn"))) events)]

        (case arg   ;;Fix the paths below to be the actual config files once we iron out the events files
          "amqp" (make-amqp-config "../rocky-road/docker/amqp/TESTamqp-service-config.sh" exchange topics)
          "rabbitmq" (rc/make-rabbit-config "../rocky-road/docker/rabbitmq/TESTdefinitions.json" exchange topics))))


    (throw (Exception. "Must have at least one arg!"))))


(comment

  (def filename "test-name.out")
  (def exchange "the-exchange")

  (def arg "amqp")
  (def channel (io-slurp-string (str "../rocky-road/channels/" arg ".edn")))
  (def exchange (:exchange-name channel))
  (def events (:events channel))
  (def event "wireless-request")
  (def topics (atom []))
  (doseq [event events]
    (conj topics (:event-channel channel (io-slurp-string (str "events/events/" event ".edn")))))


  (make-amqp-config "amqp-test-config.out" "my-exchange" ["wireless-request" "request-error" "test-error"])


  (.list (io/file "../"))
  (System/getProperty "user.dir")
  (io-slurp-string "../rocky-road/channels/amqp.edn")
  ())