(ns guess-number.core
  (:gen-class)
  (:use seesaw.core seesaw.dev)
  (:require [clojure.string :as str]))

(declare setup-frame do-turn guess-number)

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
  (let [guesses (atom [])
        button-font "VERDANA-ITALIC-24"
        label-font "VERDANA-BOLD-18"
        output-font "VERDANA-BOLD-14"
        history-window (text :border "History"
                             :editable? false
                             :multi-line? true
                             :wrap-lines? true
                             :font output-font
                             :preferred-size [320 :by 200]
                             :text "")
        guess-window (text :border "Computer guess"
                           :editable? false
                           :font label-font
                           :text "Choose a number")
        buttons (map (fn [n] (button :font button-font
                                     :text (str n)
                                     :user-data n
                                     :listen [:mouse-clicked #(alert "Hi")]))
                     (range 10))
        frame (frame
               :title "Mind-reading number guesser"
               :resizable? false
               :content (vertical-panel
                         :items [(text :editable? false
                                       :halign :center
                                       :font label-font
                                       :text "Written by David Goldfarb to amaze his kids")
                                 history-window
                                 guess-window
                                 (horizontal-panel :items buttons)]))]
    (-> frame pack! show! invoke-later)
    (map (fn [button]
           (listen button :mouse-clicked
                   (fn [_]
                     (do-turn (user-data button) guesses history-window guess-window))))
         buttons)))

(defn do-turn
  "Do a turn."
  [new-number guesses history-window guess-window]
  (let [guess (guess-number @guesses)]
    (swap! guesses conj new-number)
    (text! guess-window (str "I guessed " guess " "
                             (if (= guess new-number)
                               "and I was right!"
                               (str "but you chose " new-number ", you sneak."))))
    (text! history-window (str/join " " @guesses))))

(defn guess-number
  "'AI' logic."
  [history]
  1)
  

;;; TODO
;;;
;;; Seesaw docs home at https://github.com/daveray/seesaw/wiki
;;; Seesaw API home at http://daveray.github.com/seesaw/
;;; For layouts and skinning, see
;;; https://github.com/daveray/seesaw/blob/develop/examples/substance/src/substance/core.clj
;;; or local copy in /git/mirror










