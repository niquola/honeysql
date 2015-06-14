(defproject honeysql "0.6.1-fxp-SNAPSHOT"
  :description "SQL as Clojure data structures"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :url "https://github.com/jkk/honeysql"
  :scm {:name "git"
        :url "https://github.com/jkk/honeysql"}
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.7.0-RC1"]
                                  [org.clojure/clojurescript "0.0-3308"]]
                   :cljsbuild {:builds [{:source-paths ["src" "test"]}]}
                   :plugins [[lein-cljsbuild "1.0.6"]]}})
