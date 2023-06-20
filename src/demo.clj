(ns demo
  (:require [lanterna.screen :as s]
            [lanterna.terminal :as t])
  (:import (com.googlecode.lanterna TerminalPosition)
           (com.googlecode.lanterna.screen TerminalScreen)
           (com.googlecode.lanterna.terminal DefaultTerminalFactory)))

(comment
  (def term (t/get-terminal :swing {:cols 100 :rows 100}))
  (def term (t/get-terminal))
  term
  (t/start term)
  (t/stop term)
  (def scr (s/terminal-screen term))

  (s/start scr)

  (s/put-string scr 10 10 "Hello, world!")
  (s/put-string scr 10 11 "Press any key to exit!")
  (s/redraw scr)
  (s/get-key-blocking scr)

  (s/stop scr)






  (let [terminal (-> (DefaultTerminalFactory.) (.createTerminal))
        ^com.googlecode.lanterna.screen.Screen screen (TerminalScreen. terminal)
        text-graphics (.newTextGraphics screen)
        pos (TerminalPosition. 10 10)]
    (.startScreen screen)
    (.putString text-graphics 1 1 "Hello World!")
    (.setCursorPosition screen pos)
    (.refresh screen)
    (.readInput screen)
    (println "aoeunth")
    (.stopScreen screen)))