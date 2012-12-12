(ns guess-number.core
  (:gen-class)
  (:use seesaw.core seesaw.dev)
  (:require [clojure.string :as str]))

(declare setup-frame do-turn guess-number)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (setup-frame))

(defn setup-frame
  "Configure our window parts"
  []
  (let [guesses (atom [])
        num-successes (atom 0)
        num-failures (atom 0)
        button-font "VERDANA-ITALIC-24"
        label-font "VERDANA-BOLD-18"
        output-font "VERDANA-BOLD-14"
        history-window (text :border "History"
                             :editable? false
                             :multi-line? true
                             :wrap-lines? true
                             :font output-font
                             :preferred-size [600 :by 200]
                             :text "")
        guess-window (text :border "Computer guess"
                           :editable? false
                           :font output-font
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
    (native!)
    (-> frame pack! show! invoke-later)
    (map (fn [button]
           (listen button :mouse-clicked
                   (fn [_]
                     (do-turn (user-data button)
                              guesses num-successes num-failures
                              history-window guess-window)
                     (-> frame pack!))))
         buttons)))

(defn do-turn
  "Do a turn."
  [new-number guesses num-successes num-failures history-window guess-window]
  (let [guess (guess-number @guesses)
        success (= guess new-number)]
    (swap! guesses conj new-number)
    (swap! (if success num-successes num-failures) inc)
    (let [success-ratio (float (/ @num-successes (+ @num-successes @num-failures)))
          message (str "I guessed " guess " "
                       (if success
                         "and I was right!"
                         (str "but you chose " new-number "."))
                       " Guessed " success-ratio
                       (if (> success-ratio 0.10)
                         " [TRY HARDER]"
                         " [DOING GOOD]")
                       )]
      (text! guess-window message)
      ;; DOESN'T WORK
      ;; (.setBackground guess-window
      ;;                 (java.awt.Color.
      ;;                  (cond (< success-ratio 0.01) 0x00FF00
      ;;                        (< success-ratio 0.03) 0x008000
      ;;                        (< success-ratio 0.06) 0x208020
      ;;                        (< success-ratio 0.09) 0x202020
      ;;                        (< success-ratio 0.11) 0x802020
      ;;                        (< success-ratio 0.15) 0x800000
      ;;                        (< success-ratio 0.20) 0xFF0000)))
      ;; (repaint! guess-window)
      (text! history-window (str/join " " @guesses)))))

(defn chain-matches [[key history]]
  (let [key-len (count key)
        hist-len (count history)]
    (keep-indexed (fn [idx item] (when (and (= key item)
                                            (< (+ idx key-len) hist-len))
                                   (history (+ idx key-len))))
                  (partition key-len 1 history))))

(defn chains [history]
  (let [len (count history)]
    (mapcat chain-matches
         (map #(vector (subvec history %) (subvec history 0 %))
              (range (+ (/ len 2) 1/2) len)))))

(defn guess-number
  "'AI' logic."
  [history]
  (or (first (chains history)) (rand-int 10)))
  

;;; TODO
;;;
;;; Seesaw docs home at https://github.com/daveray/seesaw/wiki
;;; Seesaw API home at http://daveray.github.com/seesaw/
;;; For layouts and skinning, see
;;; https://github.com/daveray/seesaw/blob/develop/examples/substance/src/substance/core.clj
;;; or local copy in /git/mirror










