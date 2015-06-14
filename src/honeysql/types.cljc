(ns honeysql.types
  (:refer-clojure :exclude [array]))

(deftype SqlCall [name args _meta]
  Object
  (hashCode [_] (hash-combine (hash name) (hash args)))
  #?(:cljs cljs.core/IEquiv)
  (#?(:clj equals :cljs -equiv) [this x]
    (cond (identical? this x) true
          (instance? SqlCall x) (let [^SqlCall x x]
                                  (and (= name (.-name x))
                                       (= args (.-args x))))
          :else false))
  #?(:clj clojure.lang.IObj :cljs IMeta)
  (#?(:clj meta :cljs -meta) [_] _meta)
  #?(:cljs IWithMeta)
  (#?(:clj withMeta :cljs -with-meta) [_ m] (SqlCall. name args m)))

(defn call
  "Represents a SQL function call. Name should be a keyword."
  [name & args]
  (SqlCall. name args nil))

(defn read-sql-call [form]
  ;; late bind so that we get new class on REPL reset
  (apply #?(:clj (resolve `call) :cljs call) form))

;;;;

(deftype SqlRaw [s _meta]
  Object
  #?(:clj (hashCode [this]
            (hash-combine (hash (class this)) (hash s))))
  #?(:cljs cljs.core/IEquiv)
  (#?(:clj equals :cljs -equiv) [_ x] (and (instance? SqlRaw x) (= s (.-s ^SqlRaw x))))
  #?(:clj clojure.lang.IObj :cljs IMeta)
  (#?(:clj meta :cljs -meta) [_] _meta)
  #?(:cljs IWithMeta)
  (#?(:clj withMeta :cljs -with-meta) [_ m] (SqlRaw. s m)))

(defn raw
  "Represents a raw SQL string"
  [s]
  (SqlRaw. (str s) nil))

(defn read-sql-raw [form]
  ;; late bind, as above
  (#?(:clj (resolve `raw) :cljs raw) form))

;;;;

(deftype SqlParam [name _meta]
  Object
  #?(:clj (hashCode [this]
            (hash-combine (hash (class this)) (hash (name name)))))
  #?(:cljs cljs.core/IEquiv)
  (#?(:clj equals :cljs -equiv) [_ x] (and (instance? SqlParam x) (= name (.-name ^SqlParam x))))
  #?(:clj clojure.lang.IObj :cljs IMeta)
  (#?(:clj meta :cljs -meta) [_] _meta)
  #?(:cljs IWithMeta)
  (#?(:clj withMeta :cljs -with-meta) [_ m] (SqlParam. name m)))

(defn param
  "Represents a SQL parameter which can be filled in later"
  [name]
  (SqlParam. name nil))

(defn param-name [^SqlParam param]
  (.-name param))

(defn read-sql-param [form]
  ;; late bind, as above
  (#?(:clj (resolve `param) :cljs param) form))

;;;;

(deftype SqlArray [values _meta]
  Object
  #?(:clj (hashCode [this]
            (hash-combine (hash (class this)) (hash values))))
  #?(:cljs cljs.core/IEquiv)
  (#?(:clj equals :cljs -equiv) [_ x]
    (and (instance? SqlArray x) (= values (.-values ^SqlArray x))))
  #?(:clj clojure.lang.IObj :cljs IMeta)
  (#?(:clj meta :cljs -meta) [_] _meta)
  #?(:cljs IWithMeta)
  (#?(:clj withMeta :cljs -with-meta) [_ m] (SqlArray. values m)))

(defn array
  "Represents a SQL array."
  [values]
  (SqlArray. values nil))

(defn array-vals [^SqlArray a]
  (.-values a))

(defn read-sql-array [form]
  ;; late bind, as above
  (#?(:clj (resolve `array) :cljs array) form))

#?(:clj
    (do
      (defmethod print-method SqlCall [^SqlCall o ^java.io.Writer w]
        (.write w (str "#sql/call " (pr-str (into [(.-name o)] (.-args o))))))

      (defmethod print-dup SqlCall [o w]
        (print-method o w))

      (defmethod print-method SqlRaw [^SqlRaw o ^java.io.Writer w]
        (.write w (str "#sql/raw " (pr-str (.s o)))))

      (defmethod print-dup SqlRaw [o w]
        (print-method o w))

      (defmethod print-method SqlParam [^SqlParam o ^java.io.Writer w]
        (.write w (str "#sql/param " (pr-str (.name o)))))

      (defmethod print-dup SqlParam [o w]
        (print-method o w))

      (defmethod print-method SqlArray [^SqlArray a ^java.io.Writer w]
        (.write w (str "#sql/array " (pr-str (.values a)))))

      (defmethod print-dup SqlArray [a w]
        (print-method a w))))
