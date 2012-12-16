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
  (:use seesaw.core seesaw.dev))

(defn setup-frame
  "Configure our window parts"
  [turn-callback]
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
                     (turn-callback (user-data button)
                              guesses num-successes num-failures
                              history-window guess-window)
                     (-> frame pack!))))
         buttons)))
