(ns lanterna.constants
  (:import com.googlecode.lanterna.SGR
           com.googlecode.lanterna.TextColor$ANSI
           com.googlecode.lanterna.input.KeyType
           java.nio.charset.Charset))

(set! *warn-on-reflection* true)

(def charsets {:utf-8 (Charset/forName "UTF-8")})

(def colors
  {:black   TextColor$ANSI/BLACK
   :white   TextColor$ANSI/WHITE
   :red     TextColor$ANSI/RED
   :green   TextColor$ANSI/GREEN
   :blue    TextColor$ANSI/BLUE
   :cyan    TextColor$ANSI/CYAN
   :magenta TextColor$ANSI/MAGENTA
   :yellow  TextColor$ANSI/YELLOW
   :default TextColor$ANSI/DEFAULT})

(def styles
  {:bold SGR/BOLD
   :reverse SGR/REVERSE
   :underline SGR/UNDERLINE
   :blinking SGR/BLINK})

(def key-codes
  {KeyType/Character :character
   KeyType/Escape :escape
   KeyType/Backspace :backspace
   KeyType/ArrowLeft :left
   KeyType/ArrowRight :right
   KeyType/ArrowUp :up
   KeyType/ArrowDown :down
   KeyType/Insert :insert
   KeyType/Delete :delete
   KeyType/Home :home
   KeyType/End :end
   KeyType/PageUp :page-up
   KeyType/PageDown :page-down
   KeyType/Tab :tab
   KeyType/ReverseTab :reverse-tab
   KeyType/Enter :enter
   KeyType/Unknown :unknown
   KeyType/CursorLocation :cursor-location})

(def sgr
  {:bold com.googlecode.lanterna.SGR/BOLD
   :reverse com.googlecode.lanterna.SGR/REVERSE
   :blinking com.googlecode.lanterna.SGR/BLINK
   :underline com.googlecode.lanterna.SGR/UNDERLINE})
