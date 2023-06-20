(ns lanterna.terminal
  (:refer-clojure :exclude [flush])
  (:import
    com.googlecode.lanterna.TerminalPosition
    com.googlecode.lanterna.SGR
    com.googlecode.lanterna.terminal.Terminal
    com.googlecode.lanterna.TerminalSize
    com.googlecode.lanterna.terminal.TerminalResizeListener
    com.googlecode.lanterna.terminal.ansi.UnixTerminal
    com.googlecode.lanterna.terminal.ansi.CygwinTerminal
    com.googlecode.lanterna.terminal.swing.SwingTerminal
    (com.googlecode.lanterna.terminal.swing AWTTerminalFontConfiguration$BoldMode SwingTerminalFontConfiguration TerminalEmulatorColorConfiguration TerminalEmulatorDeviceConfiguration)
    (java.awt Font GraphicsEnvironment)
    (java.nio.charset Charset))

  (:require
   [lanterna.common :refer [parse-key block-on]]
   [lanterna.constants :as c]))

(set! *warn-on-reflection* true)

(defn- windows? []
  (-> (System/getProperty "os.name" "")
      (.toLowerCase)
      (.startsWith "windows")))

(defn add-resize-listener
  "Create a listener that will call the supplied fn when the terminal is resized.

  The function must take two arguments: the new number of columns and the new
  number of rows.

  The listener itself will be returned.  You don't need to do anything with it,
  but you can use it to remove it later with remove-resize-listener.

  "
  [^Terminal terminal listener-fn]
  (let [listener (reify TerminalResizeListener
                   (onResized [this terminal newSize]
                     (listener-fn (.getColumns newSize)
                                  (.getRows newSize))))]
    (.addResizeListener terminal listener)
    listener))

(defn remove-resize-listener
  "Remove a resize listener from the given terminal."
  [^Terminal terminal listener]
  (.removeResizeListener terminal listener))

(defn get-available-fonts []
  (set (.getAvailableFontFamilyNames
         (GraphicsEnvironment/getLocalGraphicsEnvironment))))

(defn- get-font-name [font]
  (let [fonts (if (coll? font) font [font])
        fonts (concat fonts ["Monospaced"])
        available (get-available-fonts)]
    (first (filter available fonts))))

(defn- get-swing-terminal
  [cols rows
   {:as opts
    :keys [font font-size palette]
    :or {font ["Menlo" "Consolas" "Monospaced"]
         font-size 14
         palette :xterm}}]
  (let [font (get-font-name font)
        ^TerminalSize sz (TerminalSize. cols rows)
        ^SwingTerminalFontConfiguration font-config (SwingTerminalFontConfiguration. true AWTTerminalFontConfiguration$BoldMode/EVERYTHING
                                                                                    (into-array ^Font [(new Font font Font/PLAIN font-size)
                                                                                                       (new Font font Font/BOLD font-size)]))
        ^TerminalEmulatorColorConfiguration color-config (TerminalEmulatorColorConfiguration/newInstance (c/palettes palette))
        ^TerminalEmulatorDeviceConfiguration device-config (TerminalEmulatorDeviceConfiguration/getDefault)]
    (new SwingTerminal sz device-config font-config color-config)))




(defn get-terminal
  ([] (get-terminal :auto {}))
  ([kind] (get-terminal kind {}))
  ([kind {:as opts
          :keys [cols rows charset resize-listener]
          :or {cols 80
               rows 24
               charset :utf-8
               resize-listener nil}}]
   (let [in  System/in
         out System/out]
     (case kind
       (:auto :text) ;; TODO: we should do something different for :text
       (if (windows?)
         (new CygwinTerminal in out (Charset/forName "UTF8"))
         (new UnixTerminal in out (Charset/forName "UTF8")))
       :swing  (get-swing-terminal cols rows opts)))))

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
  (.setCursorPosition terminal x y))

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
   (let [position ^TerminalPosition (.getCursorPosition terminal)]
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
  (.setForegroundColor terminal (c/colors color)))

(defn set-bg-color [^Terminal terminal color]
  (.setBackgroundColor terminal (c/colors color)))

(defn set-style
  "Enter a style"
  [^Terminal terminal style]
  (.enableSGR terminal (into-array SGR [(c/styles style)])))

(defn get-key
  "Get the next keypress from the user, or nil if none are buffered.

  If the user has pressed a key, that key will be returned (and popped off the
  buffer of input).

  If the user has *not* pressed a key, nil will be returned immediately.  If you
  want to wait for user input, use get-key-blocking instead.

  "
  [^Terminal terminal]
  (parse-key (.pollInput terminal)))

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
  ([^Terminal terminal]
   (get-key-blocking terminal {}))
  ([^Terminal terminal {:keys [interval timeout] :as opts}]
   (block-on get-key [terminal] opts)))
