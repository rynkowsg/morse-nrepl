(ns morse-nrepl
  (:require
   [clojure.string :as str]
   [nrepl.middleware :refer [set-descriptor!]]
   [nrepl.middleware.print :refer [wrap-print]])
  (:import
   (nrepl.transport Transport)))

;; ----- require morse fns -------------

(defn ^:private require-fn
  [sym]
  (try
    (requiring-resolve sym)
    (catch Exception _
      (throw "morse not available"))))

(def ^:private morse-ui-in-proc (require-fn 'dev.nu.morse/launch-in-proc))
(def ^:private morse-submit (require-fn 'dev.nu.morse/submit))

;; --------- cursive check ------------

(def ^:private cursive-commands
  ["(binding [*print-meta* true] (pr-str (cursive.riddley/macroexpand-all"
   "(clojure.core/with-redefs [clojure.test/do-report (clojure.core/fn"
   "(cursive.repl"
   "(do (clojure.core/println (clojure.core/str \"Clojure \" (clojure.core/clojure-version)))"
   "(do (do (do (clojure.core/println (clojure.core/str"
   "(get *compiler-options* :disable-locals-clearing)"
   "(try (clojure.core/cond (clojure.core/resolve (quote cljs.core/*clojurescript-version*)) :cljs (java.la"
   "(try (clojure.lang.Compiler/load (java.io.StringReader. ((clojure.core/deref"])

(defn ->bool [p] (if p true false))

(defn cursive?
  "Takes an nREPL request and returns true if a noisy cursive eval request."
  [{:keys [code op] :as _request}]
  (->bool (and (= op "eval")
               (some #(str/starts-with? code %) cursive-commands))))

;; -------- nrepl middleware ----------

(defn send-to-morse!
  [{:keys [code] :as _req} {:keys [value] :as resp}]
  (when (and code (contains? resp :value))
    (morse-submit (read-string code) value))
  resp)

(defn- wrap-morse-sender
  "Wraps a `Transport` with code that prints the value of messages sent to
  it using the provided function."
  [{:keys [transport] :as request}]
  (reify Transport
    (recv [_]
      (.recv transport))
    (recv [_ timeout]
      (.recv transport timeout))
    (send [this resp]
      (.send transport (if (cursive? request) resp (send-to-morse! request resp)))
      this)))

(defn wrap
  [handler]
  (fn [request]
    (handler (assoc request :transport (wrap-morse-sender request)))))

(defn launch-and-wrap
  [handler]
  (morse-ui-in-proc)
  (wrap handler))

(set-descriptor! #'wrap
                 {:requires #{#'wrap-print}
                  :expects  #{"eval"}
                  :handles  {}})

(set-descriptor! #'launch-and-wrap
                 {:requires #{#'wrap-print}
                  :expects  #{"eval"}
                  :handles  {}})
