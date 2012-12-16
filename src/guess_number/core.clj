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
  (:use seesaw.core)
  (:require [guess-number.guess :as guess]
            [guess-number.seesaw-frame :as win]
            [clojure.string :as str]))

(declare do-turn)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (win/setup-frame do-turn))

(defn do-turn
  "Do a turn."
  [new-number guesses num-successes num-failures history-window guess-window]
  (let [guess (guess/guess-number @guesses)
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






  

;;; TODO
;;;
;;; Seesaw docs home at https://github.com/daveray/seesaw/wiki
;;; Seesaw API home at http://daveray.github.com/seesaw/
;;; For layouts and skinning, see
;;; https://github.com/daveray/seesaw/blob/develop/examples/substance/src/substance/core.clj
;;; or local copy in /git/mirror
