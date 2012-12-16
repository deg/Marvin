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

(ns guess-number.seesaw-frame
  (:use [seesaw.core]
        [seesaw.dev]
        [seesaw.style :only [apply-stylesheet]])
  (:require [clojure.string :as str]
            [seesaw.bind :as bind]
            [guess-number.guess :as guess]))

(defn layout []
  (frame
   :title "Mind-reading number guesser"
   :content
   (border-panel
    :border 4 :hgap 4 :vgap 4
    :north (text :id :subtitle)
    :center (text :id :history
                  :multi-line? true)
    :east (grid-panel
            :columns 3
            :items (concat (map #(button :class :digit :text %)
                                [7 8 9
                                 4 5 6
                                 1 2 3
                                 0])
                           [(button :id :reset)]))
    :south (text :id :patter))))


(def style
   (let [font-name "Verdana"
         output-font {:name font-name :size 14 :style :bold}
         button-font {:name font-name :size 24 :style :italic}]
     {[:#subtitle] {:halign    :center
                    :font      {:name font-name :size 18 :style :bold}
                    :editable? false
                    :text "Written by David Goldfarb to amaze his kids"}
      [:#history] {:font output-font
                   :border "History"
                   :editable? false
                   :wrap-lines? true
                   :preferred-size [600 :by 200]
                   :text ""}
      [:.digit]   {:font button-font}
      [:#reset]   {:font {assoc button-font :size 14}
                   :text "reset"}
      [:#patter]  {:font output-font
                   :editable? false
                   :text "Choose a number"}}))



(defn setup-frame
  "Configure our window parts"
  [turn-callback]
  (let [guesses (atom [])
        num-successes (atom 0)
        num-failures (atom 0) ]
    (-> (layout) (apply-stylesheet style) pack! show! invoke-later)
    #_(map (fn [button]
           (listen button :mouse-clicked
                   (fn [_]
                     (turn-callback (user-data button)
                              guesses num-successes num-failures
                              history-window guess-window)
                     (-> frame pack!))))
         buttons)))

(defn behave
  [root]
  (let [numbers (atom [])
        num-successes (atom 0)
        num-failures (atom 0)
        history-pane (select root [:#history])
        patter-pane (select root [:#patter])
        ]
    (listen (select root [:.digit])
            :action (fn [e]
                     (let [guess (guess/guess-number @numbers)
                           new-digit (text e)
                           new-number (Integer/valueOf new-digit)
                           success (= guess new-number)]
                       (swap! (if success num-successes num-failures) inc)
                       (swap! numbers conj new-number)
                       (let [success-ratio
                             (float (/ @num-successes (+ @num-successes @num-failures)))
                             message (str "I guessed " guess " "
                                          (if success
                                            "and I was right!"
                                            (str "but you chose " new-number "."))
                                          " Guessed " success-ratio
                                          (if (> success-ratio 0.10)
                                            " [TRY HARDER]"
                                            " [DOING GOOD]")
                                          )]
                         (text! patter-pane message)
                         (text! history-pane (str/join " " @numbers))))))
    (listen (select root [:#reset])
            :action (fn [e]
                      (text! patter-pane "RESET")))
    root))


(defn do-turn
  "Do a turn."
  [new-number guesses num-successes num-failures history-window guess-window]
  (let []
    (let [])))


(defn- run-layout
  [& {:keys [on-close] :or {on-close :dispose}}]
  (->
   (layout)
   behave
   (apply-stylesheet style)
   invoke-now
   (config! :on-close on-close)
   pack!
   show!)
  nil)

(defn run []
  (run-layout))

(defn run-and-exit []
  (run-layout :on-close :exit))
