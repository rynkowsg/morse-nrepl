(ns nrebl.middleware-test
  (:require
   [clojure.test :refer [deftest is]]
   [morse-nrepl :refer :all]))

(deftest test-cursive?
  (is (not (cursive? {:op "eval" :code "1"})))
  (is (not (cursive? {:op "eval" :code "(+ 1 1)"})))
  (is (not (cursive? {:op "eval" :code "nil"})))
  (is (cursive? {:op "eval" :code "(cursive.repl.runtime/completions 'clojure.pprint)"}))
  ;; eval 42 issue: https://github.com/cursive-ide/cursive/issues/3001
  (is (cursive? {:op "eval" :code "42"})))
