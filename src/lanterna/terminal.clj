(ns lanterna.terminal
  (:refer-clojure :exclude [flush])
  (:import
   com.googlecode.lanterna.TerminalPosition
   com.googlecode.lanterna.terminal.Terminal
   com.googlecode.lanterna.terminal.ansi.UnixTerminal
   com.googlecode.lanterna.terminal.ansi.CygwinTerminal
   ;; com.googlecode.lanterna.terminal.swing.SwingTerminal
   ;; com.googlecode.lanterna.terminal.swing.TerminalAppearance
   ;; java.awt.Font
   ;; java.awt.GraphicsEnvironment
   )
  (:require
   [lanterna.common :refer [parse-key block-on]]
   [lanterna.constants :as c]))

;; (defn add-resize-listener
;;   "Create a listener that will call the supplied fn when the terminal is resized.

;;   The function must take two arguments: the new number of columns and the new
;;   number of rows.

;;   The listener itself will be returned.  You don't need to do anything with it,
;;   but you can use it to remove it later with remove-resize-listener.

;;   "
;;   [^Terminal terminal listener-fn]
;;   (let [listener (reify com.googlecode.lanterna.terminal.Terminal$ResizeListener
;;                    (onResized [this newSize]
;;                      (listener-fn (.getColumns newSize)
;;                                   (.getRows newSize))))]
;;     (.addResizeListener terminal listener)
;;     listener))

;; (defn remove-resize-listener
;;   "Remove a resize listener from the given terminal."
;;   [^Terminal terminal listener]
;;   (.removeResizeListener terminal listener))

;; (defn get-available-fonts []
;;   (set (.getAvailableFontFamilyNames
;;          (GraphicsEnvironment/getLocalGraphicsEnvironment))))

;; (defn- get-font-name [font]
;;   (let [fonts (if (coll? font) font [font])
;;         fonts (concat fonts ["Monospaced"])
;;         available (get-available-fonts)]
;;     (first (filter available fonts))))


;; (defn- get-swing-terminal [cols rows
;;                            {:as opts
;;                             :keys [font font-size palette]
;;                             :or {font ["Menlo" "Consolas" "Monospaced"]
;;                                  font-size 14
;;                                  palette :mac-os-x}}]
;;   (let [font (get-font-name font)
;;         appearance (new TerminalAppearance
;;                         (new Font font Font/PLAIN font-size)
;;                         (new Font font Font/BOLD font-size)
;;                         (c/palettes palette) true)]
;;     (new SwingTerminal appearance cols rows)))

(defn windows? []
  (-> (System/getProperty "os.name" "")
      (.toLowerCase)
      (.startsWith "windows")))

(defn text-terminal []
  (if (windows?)
    (new CygwinTerminal System/in System/out (java.nio.charset.Charset/forName "UTF8"))
    (new UnixTerminal System/in System/out (java.nio.charset.Charset/forName "UTF8"))))

(defn start
  "Start the terminal.  Consider using in-terminal instead."
  [^Terminal terminal]
  (.enterPrivateMode terminal))

(defn stop
  "Stop the terminal.  Consider using in-terminal instead."
  [^Terminal terminal]
  (.exitPrivateMode terminal))


(defmacro in-terminal
  "Start the given terminal, perform the body, and stop the terminal afterward."
  [terminal & body]
  `(do
     (start ~terminal)
     (try ~@body
       (finally (stop ~terminal)))))


(defn get-size
  "Return the current size of the terminal as [cols rows]."
  [^Terminal terminal]
  (let [size (.getTerminalSize terminal)
        cols (.getColumns size)
        rows (.getRows size)]
    [cols rows]))


(defn move-cursor
  "Move the cursor to a specific location on the screen."
  [^Terminal terminal x y]
  (.moveCursor terminal x y))

(defn put-character
  "Draw the character at the current cursor location.

  If x and y are given, moves the cursor there first.

  Moves the cursor one character to the right, so a sequence of calls will
  output next to each other.

  "
  ([^Terminal terminal ch]
   (.putCharacter terminal ch))
  ([^Terminal terminal ch x y]
   (move-cursor terminal x y)
   (put-character terminal ch)))


(defn put-string
  ([^Terminal terminal ^String s]
   (let [position ^TerminalPosition (.getPosition terminal)]
     (put-string terminal s (.getColumn position) (.getRow position))))
  ([^Terminal terminal ^String s ^Integer column ^Integer row]
   (doall
    (reduce (fn [acc c]
              (.setCursorPosition terminal (:column acc) (:row acc))
              (.putCharacter terminal c)
              (update acc :column inc))
            {:column column :row row}
            s))
   nil))

(defn clear
  "Clear the terminal.

  The cursor will be at 0 0 afterwards.

  "
  [^Terminal terminal]
  (.clearScreen terminal)
  (move-cursor terminal 0 0))

(defn flush
  ""
  [^Terminal terminal]
  (.flush terminal))


(defn set-fg-color [^Terminal terminal color]
  (.applyForegroundColor terminal (c/colors color)))

(defn set-bg-color [^Terminal terminal color]
  (.applyBackgroundColor terminal (c/colors color)))

(defn set-style
  "Enter a style"
  [^Terminal terminal style]
  (.applySGR terminal (into-array com.googlecode.lanterna.terminal.Terminal$SGR [(c/enter-styles style)])))

(defn remove-style
  "Exit a style"
  [^Terminal terminal style]
  (.applySGR terminal (into-array com.googlecode.lanterna.terminal.Terminal$SGR [(c/exit-styles style)])))

(defn reset-styles
  "Reset all styles"
  [^Terminal terminal]
  (.applySGR terminal (into-array com.googlecode.lanterna.terminal.Terminal$SGR [c/reset-style])))


(defn get-key
  "Get the next keypress from the user, or nil if none are buffered.

  If the user has pressed a key, that key will be returned (and popped off the
  buffer of input).

  If the user has *not* pressed a key, nil will be returned immediately.  If you
  want to wait for user input, use get-key-blocking instead.

  "
  [^Terminal terminal]
  (parse-key (.readInput terminal)))

(defn get-key-blocking
  "Get the next keypress from the user.

  If the user has pressed a key, that key will be returned (and popped off the
  buffer of input).

  If the user has *not* pressed a key, this function will block, checking every
  50ms.  If you want to return nil immediately, use get-key instead.

  Options can include any of the following keys:

  :interval - sets the interval between checks
  :timeout  - sets the maximum amount of time blocking will occur before
              returning nil

  "
  ([^Terminal terminal] (get-key-blocking terminal {}))
  ([^Terminal terminal {:keys [interval timeout] :as opts}]
     (block-on get-key [terminal] opts)))


;; (comment

;;   (def t (get-terminal :swing
;;                        {:cols 40 :rows 30
;;                         :font ["Menlo"]
;;                         :font-size 24
;;                         :palette :gnome}))
;;   (start t)
;;   (set-fg-color t :yellow)
;;   (put-string t "Hello, world!")
;;   (get-key-blocking t {:timeout 1000})
;;   (get-key-blocking t {:interval 2000})
;;   (stop t))
