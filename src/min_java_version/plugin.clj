(ns min-java-version.plugin)

(defn >=list [l1 l2]
  (reduce (fn [agg [i1 i2]]
            (if (and i1 i2)
              (>= i1 i2)
              agg))
          true
          (map vector l1 l2)))

(defn at-least-version
  [required running]
  (let [required (->> (re-seq #"(\d)\.(\d)(?:\.(\d)(?:_(\d*))?)?" required) first rest (map #(when % (Integer. %))))
        running (->> (re-seq  #"(\d)\.(\d)\.(\d)_(\d*)" running) first rest (map #(Integer. %)))]
    (>=list running required)))

(defn middleware [project]
  (let [required (:min-java-version project)
        running (System/getProperty "java.version")]
    (if (at-least-version required running)
      project
      (do
        (leiningen.core.main/warn
         (str "ERROR: Java version must be " required " but found " running))
        (System/exit 1)))
    project))
