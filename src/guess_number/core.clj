(ns guess-number.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (print "Guess a number: " )
  (let [number (read-line)]
    (println "You guessed " number)))



;; (use 'swank.core)
;; (with-read-line-support 
;;   (println "a line from Emacs:" (read-line)))


(defn get-input []
  (let [input (javax.swing.JOptionPane/showInputDialog "Enter your next move (row/column)")]
    (map #(Integer/valueOf %) (.split input "/"))))