;;; Copyright (c) 2012 Degel Software Ltd.  All rights reserved.
;;;
;;; This software may have been provided to you, as a licensee of Degel
;;; Software Ltd.  If so, usage of this software is permitted only to
;;; the limit described in the license or contract between you and Degel
;;; Software Ltd.
;;;
;;; If you do not hold such a license or contract, your usage of this
;;; software is limited by the following default license terms.
;;;
;;; If you do hold a license or contract with Degel Software Ltd., then
;;; your usage of this software is limited by the following default
;;; license terms except in instances where the license or contract
;;; explicitly grants you additional rights.
;;;
;;; DEFAULT LICENSE TERMS:
;;;
;;; This software is provided 'as-is', without any express or implied
;;; warranty.  In no event will Degel Software Ltd.  be held liable for
;;; any damages arising from the use of this software.
;;;
;;; Permission is granted to anyone to use this software for any purpose
;;; including commercial applications, and to alter it and redistribute
;;; it freely, subject to the following restrictions:
;;;
;;;    1. The origin of this software must not be misrepresented; you
;;;    must not claim that you wrote the original software. If you use
;;;    this software in a product, you must acknowledge Degel Software
;;;    Ltd. in the product documentation and splash screen or credits.
;;;
;;;    2. Altered source versions must be plainly marked as such, and
;;;    must not be misrepresented as being the original software.
;;;
;;;    3. This notice may not be removed or altered from any source
;;;    distribution.
;;;
;;; For more information, contact us at info@degel.com
;;;
;;; ****************************************************************************

(ns guess-number.core
  (:gen-class)
  (:use seesaw.core seesaw.dev)
  (:require [clojure.string :as str]))

(declare setup-frame do-turn guess-number best-patterns make-sequences find-matches)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (setup-frame))

(defmacro dbg
  ([x] `(let [x# ~x] (println "dbg:" '~x "=" x#) x#))
  ([prefix x] `(let [x# ~x] (println ~prefix x#) x#)))

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


(defn guess-number
  "'AI' logic."
  [history]
  (let [{:keys [raw first-diff]} (best-patterns history)]
    (cond
     ;; If we find a long-enough repeating run, continue it.
     (and raw (>= (:match-len raw) 2))
     (history (:guess-pos raw))
     ;; Or, look for a run of first-diffs.
     (and first-diff
          (>= (:match-len first-diff) 1))
     (let [hot-pos (:guess-pos first-diff)
           diff (- (history (inc hot-pos)) (history hot-pos))
           raw-guess (+ diff (last history))]
       (cond (>= raw-guess 10) (- raw-guess 10)
             (< raw-guess 0)   (+ raw-guess 10)
             :else             raw-guess))
     ;; Or, extend a shorter repeating run
     (and raw (>= (:match-len raw) 1))
     (history (:guess-pos raw))
     ;; Or, just guess
     :else (rand-int 10))))

(defn best-patterns
  "Look for patterns in history. Let our caller decide which patterns are more
   relevant; our job is just to return the best of each pattern type.
   For now, look for :raw (long repeating sequence) and :first-diff (longest
   repeating sequence of first differences)."
   [history]
  (let [raw-chains (mapcat find-matches (make-sequences history :raw))
        first-diffs (mapv - (rest history) history)
        diff-chains (mapcat find-matches (make-sequences first-diffs :diff1))]
    ;; We only need the best, so grab best of each search.
    {:raw (first raw-chains) :first-diff  (first diff-chains)}))

(defn make-sequences [history tag]
  (let [len (count history)]
    (map #(conj (split-at % history) tag)
         (range (int (/ len 2)) len))))

(defn find-matches [[history key tag]]
  (let [key-len (count key)
        hist-len (count history)]
    (keep-indexed (fn [idx item] (when (and (= key item)
                                            (<= (+ idx key-len) hist-len))
                                   {:guess-pos (+ idx key-len)
                                    :match-start idx
                                    :match-len key-len
                                    :tag tag}))
                  (partition key-len 1 history))))





  

;;; TODO
;;;
;;; Seesaw docs home at https://github.com/daveray/seesaw/wiki
;;; Seesaw API home at http://daveray.github.com/seesaw/
;;; For layouts and skinning, see
;;; https://github.com/daveray/seesaw/blob/develop/examples/substance/src/substance/core.clj
;;; or local copy in /git/mirror
