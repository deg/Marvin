(ns guess-number.core
  (:gen-class)
  (:use seesaw.core seesaw.dev))

(declare setup-frame)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (print "Guess a number: " )
  (let [number (read-line)]
    (println "You guessed " number))
  (setup-frame))

(defn setup-frame
  "Configure our window parts"
  []
  (let [guesses (atom nil))
        button-font "VERDANA-ITALIC-24"
        label-font "VERDANA-BOLD-18"
        output-font "VERDANA-BOLD-14"
        history (text :border "Foo"
                      :editable? false
                      :multi-line? true
                      :wrap-lines? true
                      :font output-font
                      :preferred-size [320 :by 200]
                      :text "History: ")
        buttons (map (fn [n] (button :font button-font
                                     :text (str n)
                                     :user-data n
                                     :listen [:mouse-clicked #(alert "Hi")]
                                     ))
                     (range 10))
        frame
        (frame
         :title "Mind-reading number guesser"
         :resizable? false
         :content (vertical-panel
                   :items [(text :editable? false
                                 :halign :center
                                 :font label-font
                                 :text "Written by David Goldfarb to amaze his kids")
                           :separator
                           history
                           (text :editable? false
                                 :preferred-size [320 :by 40]
                                 :font output-font
                                 :text "Next guess")
                           :separator
                           (horizontal-panel :items buttons)]))]
    (-> frame pack! show! invoke-later)
    (map (fn [button]
           (listen button :mouse-clicked
                   (fn [_]
                     (config! history :text (str (text history) " " (user-data button))))))
         buttons)))

;;; TODO
;;;
;;; Seesaw docs home at https://github.com/daveray/seesaw/wiki
;;; Seesaw API home at http://daveray.github.com/seesaw/
;;; For layouts and skinning, see
;;; https://github.com/daveray/seesaw/blob/develop/examples/substance/src/substance/core.clj
;;; or local copy in /git/mirror
